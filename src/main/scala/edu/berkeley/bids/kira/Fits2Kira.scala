/*
 * Copyright (c) 2014. Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.berkeley.bids.kira

import edu.berkeley.bids.kira.avro.FitsValue
import edu.berkeley.bids.kira.models._
import edu.berkeley.bids.kira.util.FitsUtils
import java.io._
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.SparkContext._
import org.apache.spark.{ SparkConf, SparkContext }
import org.apache.spark.rdd.RDD
import parquet.avro.{
  AvroParquetInputFormat,
  AvroParquetOutputFormat,
  AvroReadSupport
}
import parquet.filter.UnboundRecordFilter
import parquet.hadoop.{
  ParquetInputFormat,
  ParquetOutputFormat
}
import parquet.hadoop.util.ContextUtil
import parquet.hadoop.metadata.CompressionCodecName
import scala.annotation.tailrec

object Fits2Kira extends Serializable {
  def main(args: Array[String]) {

    def parallelize(f: Fits,
                    metadata: FitsMetadata,
                    sc: SparkContext): RDD[FitsValue] = {
      sc.parallelize(f.matrix.zipWithIndex.flatMap(vk => {
        val (array, idx) = vk

        array.zipWithIndex.map(vk2 => {
          val (value, jdx) = vk2

          FitsValue.newBuilder()
            .setXPos(idx)
            .setYPos(jdx)
            .setValue(value)
            .setStart(metadata.start)
            .setEnd(metadata.end)
            .setOffset(metadata.offset)
            .build()
        })
      }))
    }

    /**
     * Create a job using either the Hadoop 1 or 2 API
     * @param sc A Spark context
     */
    def newJob(sc: SparkContext): Job = {
      newJobFromConfig(sc.hadoopConfiguration)
    }

    def newJobFromConfig(config: Configuration): Job = {
      val jobClass: Class[_] = Class.forName("org.apache.hadoop.mapreduce.Job")
      try {
        // Use the getInstance method in Hadoop 2
        jobClass.getMethod("getInstance", classOf[Configuration]).invoke(null, config).asInstanceOf[Job]
      } catch {
        case ex: NoSuchMethodException =>
          // Drop back to Hadoop 1 constructor
          jobClass.getConstructor(classOf[Configuration]).newInstance(config).asInstanceOf[Job]
      }
    }

    def save(rdd: RDD[FitsValue],
             job: Job,
             filePath: String,
             blockSize: Int = 128 * 1024 * 1024,
             pageSize: Int = 1 * 1024 * 1024,
             compressCodec: CompressionCodecName = CompressionCodecName.GZIP,
             disableDictionaryEncoding: Boolean = false) {

      // configure parquet
      ParquetOutputFormat.setCompression(job, compressCodec)
      ParquetOutputFormat.setEnableDictionary(job, !disableDictionaryEncoding)
      ParquetOutputFormat.setBlockSize(job, blockSize)
      ParquetOutputFormat.setPageSize(job, pageSize)
      AvroParquetOutputFormat.setSchema(job, new FitsValue().getSchema)

      // Add the Void Key
      val recordToSave = rdd.map(p => (null, p))

      // Save the values to the parquet file
      recordToSave.saveAsNewAPIHadoopFile(filePath,
        classOf[java.lang.Void],
        classOf[FitsValue],
        classOf[AvroParquetOutputFormat],
        ContextUtil.getConfiguration(job))
    }

    val conf = new SparkConf().setAppName("SparkMadd")
    if (conf.getOption("spark.master").isEmpty) {
      conf.setMaster("local[%d]".format(Runtime.getRuntime.availableProcessors()))
    }
    val sc = new SparkContext(conf)

    @tailrec def buildUp(datasets: Iterator[(Fits, FitsMetadata)],
                         lastRdd: RDD[FitsValue] = sc.parallelize(Array[FitsValue]())): RDD[FitsValue] = {
      if (!datasets.hasNext) {
        lastRdd
      } else {
        // cache last RDD
        lastRdd.cache()

        // parallelize the current dataset
        val (data, metadata) = datasets.next
        val newRdd = parallelize(data, metadata, sc) ++ lastRdd

        // unpersist the old rdd and recurse
        lastRdd.unpersist()
        buildUp(datasets, newRdd)
      }
    }

    def add(rdd: RDD[FitsValue],
            tcol: Int,
            trow: Int): RDD[FitsValue] = {
      // populate matrix
      rdd.flatMap(v => {
        val coords = Coordinate(v.getXPos + v.getStart - 1, v.getYPos + v.getOffset)

        if (coords.x < trow && coords.y < tcol) {
          Some((coords, v.getValue.toFloat))
        } else {
          None
        }
      }).groupByKey()
        .map(kv => {
          val (idx, values) = kv
          val l = values.filter(x => !x.isNaN)
          val v = if (l.size > 0) l.reduce(_ + _) / l.size else Float.NaN

          FitsValue.newBuilder()
            .setXPos(idx.x)
            .setYPos(idx.y)
            .setValue(v)
            .setStart(0)
            .setEnd(tcol)
            .setHeight(trow)
            .setOffset(0)
            .build()
        })
    }

    def buildMatrix(rdd: RDD[FitsValue]): Array[Array[Float]] = {
      // cache rdd
      rdd.cache()

      // get first entry, to get matrix dimensions
      val one = rdd.first

      // build matrix
      val matrix = Array.fill[Float](one.getHeight, one.getEnd)(Float.NaN)

      // iterate over rdd and build matrix
      rdd.toLocalIterator.foreach(v => {
        matrix(v.getXPos)(v.getYPos) = v.getValue
      })

      // unpersist rdd
      rdd.unpersist()

      matrix
    }

    // create job
    val job = newJob(sc)

    //Main program entrance
    val flist = new File("resources/corrdir/").listFiles.filter(_.getName.endsWith(".fits"))

    // get data and metadata
    val fitsList = (0 until flist.length).map(i => FitsUtils.readFits(flist(i).toString)).toArray
    val map = FitsUtils.processMeta(fitsList).toSeq.sortBy(_._1).map(kv => kv._2)

    // get template
    val template = new Template("resources/template.hdr")

    // parallelize files
    val fitsRdd = buildUp(fitsList.zip(map).toIterator)

    // save to parquet
    save(fitsRdd, job, "resources/tmp_parquet")
    fitsRdd.unpersist()

    /*
    val start = System.currentTimeMillis()
    // load back from parquet, just because
    ParquetInputFormat.setReadSupportClass(job, classOf[AvroReadSupport[FitsValue]])
    val records = sc.newAPIHadoopFile("resources/tmp_parquet",
      classOf[ParquetInputFormat[FitsValue]],
      classOf[Void],
      classOf[FitsValue],
      ContextUtil.getConfiguration(job)).map(p => p._2)

    // reduce down to matrix
    val matrixRdd = add(records, template.tcol, template.trow)

    // save to parquet
    save(matrixRdd, job, "resources/final_parquet")
    matrixRdd.unpersist()

    // again, load back from parquet, just because
    ParquetInputFormat.setReadSupportClass(job, classOf[AvroReadSupport[FitsValue]])
    val reloadedMatrixRdd = sc.newAPIHadoopFile("resources/final_parquet",
      classOf[ParquetInputFormat[FitsValue]],
      classOf[Void],
      classOf[FitsValue],
      ContextUtil.getConfiguration(job)).map(p => p._2)

    val end = System.currentTimeMillis()
    println("madd in parquet takes: " + (end - start).toFloat / 1000)
    // build matrix
    val matrix = buildMatrix(reloadedMatrixRdd)

    // save output
    FitsUtils.createFits(template, matrix, "final.fits")*/
    /*val matrixRdd = add(fitsRdd, template.tcol, template.trow)
    val matrix = buildMatrix(matrixRdd)
    FitsUtils.createFits(template, matrix, "final.fits")*/

    /*val template = new Template("resources/template.hdr")

    ParquetInputFormat.setReadSupportClass(job, classOf[AvroReadSupport[FitsValue]])
    val reloadedMatrixRdd = sc.newAPIHadoopFile("resources/final_parquet",
      classOf[ParquetInputFormat[FitsValue]],
      classOf[Void],
      classOf[FitsValue],
      ContextUtil.getConfiguration(job)).map(p => p._2)

    val matrix = buildMatrix(reloadedMatrixRdd)

    FitsUtils.createFits(template, matrix, "final.fits")*/
  }
}
