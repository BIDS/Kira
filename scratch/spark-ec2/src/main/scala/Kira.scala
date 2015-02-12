import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import java.io._

object Kira {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Kira")
    val sc = new SparkContext(conf)

    val src = args(0)

    val flist = sc.binaryFiles(src)
    val results = flist.map(c => extract_str(c._2.toArray))
    val flatresults = results.flatMap(p => p.map(r => (r._1, r._2, r._3, r._4, r._5)))

    flatresults.saveAsTextFile(args(1))
    /*val flist = new File(src).listFiles.filter(_.getName.endsWith(".fits"))

    val dflist = sc.parallelize(flist)
    val results = dflist.map(f => extract(f.toString))

    val flatresults = results.flatMap(p => p.map(r => (r._1, r._2, r._3, r._4, r._5)))
    println("count: " + flatresults.count)
    flatresults.saveAsTextFile("output")*/
  }
  def extract(path: String): Array[(Double, Double, Double, Double, Short)] = {
    var matrix = Utils.load(path)
    var bkg = new Background(matrix)
    matrix = bkg.subfrom(matrix)
    val ex = new Extractor
    val objects = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat)
    //return objects.length
    var x: Array[Double] = Array.ofDim[Double](objects.length)
    var y: Array[Double] = Array.ofDim[Double](objects.length)
    for (i <- (0 until objects.length)) {
      x(i) = objects(i).x
      y(i) = objects(i).y
    }

    var err: Array[Array[Double]] = Array.fill(matrix.length, matrix(0).length) { bkg.bkgmap.globalrms }

    val (flux, fluxerr, flag) = ex.sum_circle(matrix, x, y, 5.0, err = err)
    val retArray = (0 until objects.length).map(i => (x(i), y(i), flux(i), fluxerr(i), flag(i))).toArray
    return retArray
  }

  def extract_str(content: Array[Byte]): Array[(Double, Double, Double, Double, Short)] = {
    var matrix = Utils.load_byte(content)
    var bkg = new Background(matrix)
    matrix = bkg.subfrom(matrix)
    val ex = new Extractor
    val objects = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat)
    //return objects.length
    var x: Array[Double] = Array.ofDim[Double](objects.length)
    var y: Array[Double] = Array.ofDim[Double](objects.length)
    for (i <- (0 until objects.length)) {
      x(i) = objects(i).x
      y(i) = objects(i).y
    }

    var err: Array[Array[Double]] = Array.fill(matrix.length, matrix(0).length) { bkg.bkgmap.globalrms }

    val (flux, fluxerr, flag) = ex.sum_circle(matrix, x, y, 5.0, err = err)
    val retArray = (0 until objects.length).map(i => (x(i), y(i), flux(i), fluxerr(i), flag(i))).toArray
    return retArray
  }
}
