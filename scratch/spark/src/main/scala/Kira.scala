import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import java.io._

object Kira {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("Kira")
    val sc = new SparkContext(conf)

    val src = "/Users/zhaozhang/projects/scratch/Kira/data"
    val flist = new File(src).listFiles.filter(_.getName.endsWith(".fits"))

    val results = flist.map(f => filter(f.toString))
    val num = results.reduce(_ + _)
    println("Spark: " + num + " objects were detected")
  }
  def filter(path: String): Int = {
    var matrix = Utils.load(path)
    var bkg = new Background(matrix)
    matrix = bkg.subfrom(matrix)
    val ex = new Extractor
    val objects = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat)
    return objects.length
  }
}
