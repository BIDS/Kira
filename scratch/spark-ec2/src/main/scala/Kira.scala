import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import java.io._
import java.util.zip.CRC32
import java.util.zip.Checksum

object Kira {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Kira")
    val sc = new SparkContext(conf)

    val src = args(0)
    val flist = sc.binaryFiles(src)

    val results = flist.map(c => extract_str(c._2.toArray))
    val flatresults = results.flatMap(p => p.map(r => (r._1, r._2, r._3, r._4, r._5, r._6, r._7, r._8, r._9)))
    flatresults.saveAsTextFile(args(1))

    /*val flist = new File(src).listFiles.filter(_.getName.endsWith(".fit"))

    val dflist = sc.parallelize(flist)
    val results = dflist.map(f => extract(f.toString))

    val flatresults = results.flatMap(p => p.map(r => (r._1, r._2, r._3, r._4, r._5, r._6, r._7, r._8, r._9)))
    //println("count: " + flatresults.count)
    flatresults.saveAsTextFile(args(1))*/
  }
  def extract(path: String): Array[(Int, Double, Double, Double, Double, Double, Double, Double, Short)] = {
    var matrix = Utils.load(path)
    var bkg = new Background(matrix)
    matrix = bkg.subfrom(matrix)
    val ex = new Extractor
    val objects = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat)
    //return objects.length
    var x: Array[Double] = Array.ofDim[Double](objects.length)
    var y: Array[Double] = Array.ofDim[Double](objects.length)
    var a: Array[Double] = Array.ofDim[Double](objects.length)
    var b: Array[Double] = Array.ofDim[Double](objects.length)
    var theta: Array[Double] = Array.ofDim[Double](objects.length)
    var r: Array[Double] = Array.fill(objects.length) { 6.0 }
    for (i <- (0 until objects.length)) {
      x(i) = objects(i).x
      y(i) = objects(i).y
      a(i) = objects(i).a
      b(i) = objects(i).b
      theta(i) = objects(i).theta
    }

    var err: Array[Array[Double]] = Array.fill(matrix.length, matrix(0).length) { bkg.bkgmap.globalrms }

    val (flux, fluxerr, flag) = ex.sum_circle(matrix, x, y, 5.0, err = err)
    val (kr, flag2) = ex.kron_radius(matrix, x, y, a, b, theta, r)
    val kr_ellipse = kr.map(x => 2.5 * x)

    val (flux_auto, flux_auto_err, auto_flag) = ex.sum_ellipse(matrix, x, y, a, b, theta, kr_ellipse, err = err, subpix = 1)

    val retArray = (0 until objects.length).map(i => (i, x(i), y(i), flux(i), fluxerr(i), kr(i), flux_auto(i), flux_auto_err(i), auto_flag(i))).toArray

    return retArray
  }

  def extract_str(content: Array[Byte]): Array[(Int, Double, Double, Double, Double, Double, Double, Double, Short)] = {
    var matrix = Utils.load_byte(content)
    var bkg = new Background(matrix)
    matrix = bkg.subfrom(matrix)
    val ex = new Extractor
    val objects = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat)
    //return objects.length
    var x: Array[Double] = Array.ofDim[Double](objects.length)
    var y: Array[Double] = Array.ofDim[Double](objects.length)
    var a: Array[Double] = Array.ofDim[Double](objects.length)
    var b: Array[Double] = Array.ofDim[Double](objects.length)
    var theta: Array[Double] = Array.ofDim[Double](objects.length)
    var r: Array[Double] = Array.fill(objects.length) { 6.0 }
    for (i <- (0 until objects.length)) {
      x(i) = objects(i).x
      y(i) = objects(i).y
      a(i) = objects(i).a
      b(i) = objects(i).b
      theta(i) = objects(i).theta
    }

    var err: Array[Array[Double]] = Array.fill(matrix.length, matrix(0).length) { bkg.bkgmap.globalrms }

    val (flux, fluxerr, flag) = ex.sum_circle(matrix, x, y, 5.0, err = err)

    val (kr, flag2) = ex.kron_radius(matrix, x, y, a, b, theta, r)
    val kr_ellipse = kr.map(x => 2.5 * x)

    val (flux_auto, flux_auto_err, auto_flag) = ex.sum_ellipse(matrix, x, y, a, b, theta, kr_ellipse, err = err, subpix = 1)

    val retArray = (0 until objects.length).map(i => (i, x(i), y(i), flux(i), fluxerr(i), kr(i), flux_auto(i), flux_auto_err(i), auto_flag(i))).toArray
    return retArray
  }
}
