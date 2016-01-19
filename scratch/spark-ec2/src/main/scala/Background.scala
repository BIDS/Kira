import util.Random
import java.io._

class Background(matrix: Array[Array[Double]], mask: Array[Array[Boolean]] = null, maskthresh: Double = 0.0,
                 bw: Int = 64, bh: Int = 64, fw: Int = 3, fh: Int = 3, fthresh: Double = 0.0) {
  /**Macro definitions from sep.h*/
  val SEP_TBYTE: Int = 11
  val SEP_TINT: Int = 31
  val SEP_TFLOAT: Int = 42
  val SEP_TDOUBLE: Int = 82

  val SUPPORTED_IMAGES_DTYPES = Array[Int](SEP_TDOUBLE, SEP_TFLOAT, SEP_TINT)

  /**Input flag values*/
  val SEP_ERROR_IS_VAR: Int = 0x0001
  val SEP_ERROR_IS_ARRAY: Int = 0x0002
  val SEP_MAST_IGNORE: Int = 0x0004

  /**Output flag values accesible from Scala*/
  val OBJ_MERGED: Short = 0x0001
  val OBJ_TRUNC: Short = 0x0002
  val OBJ_DOVERFLOW: Short = 0x0004
  val OBJ_SINGU: Short = 0x0008
  val APER_TRUNC: Short = 0x0010
  val APER_HASMASKED: Short = 0x0020
  val APER_ALLMASKED: Short = 0x0040
  val APER_NONPOSITIVE: Short = 0x0080

  /**Macro definition from sepcore.h*/
  val MEMORY_ALLOC_ERROR: Int = 1

  //System.loadLibrary("BackgroundImpl")
  System.load("/Users/zhaozhang/projects/scratch/Kira/src/main/C/libBackgroundImpl.jnilib")
  var bkgmap = new Sepbackmap()

  val data: Array[Byte] = Utils.flatten(matrix)
  val h = matrix.length
  val w = matrix(0).length
  val status = sep_makeback(data, mask, SEP_TDOUBLE, SEP_TBYTE, w, h, bw, bh, 0.0, fw, fh, 0.0, bkgmap)

  @native
  def sep_makeback(data: Array[Byte], mask: Array[Array[Boolean]], dtype: Int, mdtype: Int,
                   w: Int, h: Int, bw: Int, bh: Int, mthresh: Double, fw: Int, fh: Int, fthresh: Double, backmap: Sepbackmap): Int

  @native
  def sep_backarray(bkgmap: Sepbackmap, data: Array[Byte], dtype: Int, back: Array[Float], dback: Array[Float],
                    sigma: Array[Float], dsigma: Array[Float]): Int

  @native
  def sep_subbackarray(bkgmap: Sepbackmap, data: Array[Byte], dtype: Int,
                       back: Array[Float], dback: Array[Float], sigma: Array[Float], dsigma: Array[Float]): Int

  def back(dtype: Int): Array[Array[Double]] = {
    var result = Array.ofDim[Double](bkgmap.h, bkgmap.w)
    var data = new Array[Byte](bkgmap.h * bkgmap.w * 8)
    val status = sep_backarray(bkgmap, data, dtype, bkgmap.back, bkgmap.dback, bkgmap.sigma, bkgmap.dsigma)
    println("Scala: back: return: " + status)
    result = Utils.deflatten(data, bkgmap.h, bkgmap.w)
    /*for(r <- result){
      println(r.mkString(", "))
    }*/
    return result
  }

  def subfrom(matrix: Array[Array[Double]]): Array[Array[Double]] = {
    /**need to check the shape of the matrices*/
    var data = Utils.flatten(matrix)
    val status = sep_subbackarray(bkgmap, data, SEP_TDOUBLE, bkgmap.back, bkgmap.dback, bkgmap.sigma, bkgmap.dsigma)
    var result = Utils.deflatten(data, matrix.length, matrix(0).length)
    /*println("Scala: subfrom: result: ")
    for(r <- result){
      println(r.mkString(", "))
    }*/
    return result
  }
}

/*object Test {
  def backTest() {
    val dim: Int = 6
    var matrix = Array.ofDim[Double](dim, dim)
    for (i <- (0 until matrix.length)) {
      for (j <- (0 until matrix(i).length)) {
        matrix(i)(j) = 0.1
      }
    }
    matrix(1)(1) = 1.0
    matrix(1)(4) = 1.0
    matrix(4)(1) = 1.0
    matrix(4)(4) = 1.0

    var mask = Array.ofDim[Boolean](dim, dim)
    for (i <- (0 until mask.length)) {
      for (j <- (0 until mask(i).length)) {
        mask(i)(j) = false
      }
    }

    var bkg = new Background(matrix, mask, 0.0, 3, 3, 1, 1, 0.0)
    //var bkg = new Background(matrix)
    println("Scala: bkgmap: globalback: " + bkg.bkgmap.globalback + "\t globalrms:" + bkg.bkgmap.globalrms)
    println("Scala: bkgmap: w: " + bkg.bkgmap.w + "\t h: " + bkg.bkgmap.h);
    println("Scala: bkgmap: bw: " + bkg.bkgmap.bw + "\t h: " + bkg.bkgmap.bh);
    println("Scala: bkgmap: nx: " + bkg.bkgmap.nx + "\t ny: " + bkg.bkgmap.ny + "\t n: " + bkg.bkgmap.n);
    println("Scala: bkgmap: back: " + bkg.bkgmap.back.mkString(", "))
    println("Scala: bkgmap: dback: " + bkg.bkgmap.dback.mkString(", "))
    println("Scala: bkgmap: sigma: " + bkg.bkgmap.sigma.mkString(", "))
    println("Scala: bkgmap: dsigma: " + bkg.bkgmap.dsigma.mkString(", "))
    var result = bkg.back(bkg.SEP_TDOUBLE)
    result = bkg.subfrom(result)
  }

  def sumCircleTest() {
    val ex = new Extractor
    val naper = 1000
    val x = Array.fill(naper)(Random.nextDouble * (800 - 200) + 200.0)
    val y = Array.fill(naper)(Random.nextDouble * (800 - 200) + 200.0)
    val r = 3.0
    val matrix = Array.fill[Double](naper, naper) { 1.0 }
    val mask = Array.fill[Boolean](naper, naper) { false }

    println("==================sum_circle() test================")
    val (sum, sumerr, flag) = ex.sum_circle(matrix, x, y, r)
    println("Scala: sum_circle result: ")
    println(sum.take(10).mkString(", "))

    println("==================sum_circann() test================")
    val (sum2, sumerr2, flag2) = ex.sum_circann(matrix, x, y, 0.0, r)
    println("Scala: sum_circle result: ")
    println(sum2.take(10).mkString(", "))

    println("==================sum_circle() with bkgann test================")
    val bkgann = Array(6.0, 8.0)
    val (sum3, sumerr3, flag3) = ex.sum_circle(matrix, x, y, r, bkgann = bkgann, subpix = 1)
    println("Scala: sum_circle with bkgann result: ")
    println(sum3.take(10).mkString(", "))
  }

  def sumEllipseTest() {
    val ex = new Extractor
    val naper = 1000
    val x = Array.fill(naper)(Random.nextDouble * (800 - 200) + 200.0)
    val y = Array.fill(naper)(Random.nextDouble * (800 - 200) + 200.0)
    var r = Array.fill[Double](naper) { 3.0 }
    val matrix = Array.fill[Double](naper, naper) { 1.0 }
    val mask = Array.fill[Boolean](naper, naper) { false }
    val aa = Array.fill[Double](naper) { 1.0 }
    val tt = Array.fill[Double](naper) { 0.0 }

    println("==================sum_ellipse() test================")
    val (sum, sumerr, flag) = ex.sum_ellipse(matrix, x, x, aa, aa, tt, r)
    println("Scala: sum_ellipse result: ")
    println(sum.take(10).mkString(", "))

    println("==================sum_ellipann() test================")
    val pi = 3.1415926
    var a = Array.fill(naper)(1.0)
    val theta = Array.fill(naper)(Random.nextDouble * pi - pi / 2)
    val ratio = Array.fill(naper)(Random.nextDouble * 0.8 + 0.2)
    val rin = 3.0
    val rout = rin * 1.1
    val (sum1, sumerr1, flag1) = ex.sum_ellipann(matrix, x, y, a, ratio, theta, rin, rout, subpix = 0)
    println("Scala: sum_ellipann result: ")
    println(sum1.take(10).mkString(", "))

    println("==================sum_ellipann() with bkgann test================")
    a = Array.fill(naper)(2.0)
    val b = Array.fill(naper)(1.0)
    val t = Array.fill(naper)(pi / 4)
    r = Array.fill[Double](naper) { 5.0 }
    val bkgann = Array(6.0, 8.0)

    val (sum2, sumerr2, flag2) = ex.sum_ellipse(matrix, x, y, a, b, t, r, bkgann = bkgann)
    println("Scala: sum_ellipse with bkgann result: ")
    println(sum2.take(10).mkString(", "))
  }

  def extractTest() {
    val ex = new Extractor
    var matrix: Array[Array[Double]] = Utils.load("/root/Kira/scratch/spark-ec2/data/image1.fits")
    val bkg = new Background(matrix)
    matrix = bkg.subfrom(matrix)

    println("==================sep_extract() with noise without conv test 1================")
    val noise = Array.fill[Double](matrix.length, matrix(0).length)(1.0)
    val objects: Array[Sepobj] = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat, conv = null)
    println("Scala: extract: extracted " + objects.length + " objects")
    for (i <- (0 until objects.length)) {
      println("objects: " + i + "\tx: " + objects(i).x + "\ty: " + objects(i).y + "\tflux: " + objects(i).flux)
    }

    println("==================sep_extract() with noise without conv test 2================")
    val objects2 = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat, noise = noise, conv = null)
    println("Scala: extract: extracted " + objects2.length + " objects")
    for (i <- (0 until objects2.length)) {
      println("objects2: " + i + "\tx: " + objects2(i).x + "\ty: " + objects2(i).y + "\tflux: " + objects2(i).flux)
    }

    /**
     * The following test fails, as the extract function returns 7, which is from the sortit function in extract.c.
     * Decrease the deblend_nthresh to 8 works for the test image.
     */
    /*println("==================sep_extract() with noise test without conv test 3================")
    val noise2 = Array.fill[Double](matrix.length, matrix(0).length)(bkg.bkgmap.globalrms)
    val objects3 = ex.extract(matrix, 1.5F, noise = noise2, deblend_nthresh = 8, conv = null)
    println("Scala: extract: extracted " + objects3.length + " objects")
    for (i <- (0 until objects3.length)) {
      println("object3: " + i + "\tx: " + objects3(i).x + "\ty: " + objects3(i).y + "\tflux: " + objects3(i).flux)
    }*/
    println("==================sep_extract() without noise with conv test 1================")
    val objects4 = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat)
    println("Scala: extract: extracted " + objects4.length + " objects")
    for (i <- (0 until objects4.length)) {
      println("objects4: " + i + "\tx: " + objects4(i).x + "\ty: " + objects4(i).y + "\tflux: " + objects4(i).flux)
    }

    println("==================sep_extract() with noise with conv test 1================")
    val objects5 = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat, noise = noise)
    println("Scala: extract: extracted " + objects5.length + " objects")
    for (i <- (0 until objects5.length)) {
      println("objects5: " + i + "\tx: " + objects5(i).x + "\ty: " + objects5(i).y + "\tflux: " + objects5(i).flux)
    }

    println("==================sep_extract() without noise, sum_circle with error================")
    val objects6: Array[Sepobj] = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat)
    var x: Array[Double] = Array.ofDim[Double](objects6.length)
    var y: Array[Double] = Array.ofDim[Double](objects6.length)
    for (i <- (0 until objects6.length)) {
      x(i) = objects6(i).x
      y(i) = objects6(i).y
    }
    var err: Array[Array[Double]] = Array.fill(matrix.length, matrix(0).length) { bkg.bkgmap.globalrms }
    val (flux, fluxerr, flag) = ex.sum_circle(matrix, x, y, 5.0, err = err)
    println(fluxerr.mkString(", "))
  }

  def ellipseTest() {
    val ex = new Extractor
    var matrix: Array[Array[Double]] = Utils.load("/root/Kira/scratch/spark-ec2/data/image1.fits")
    val bkg = new Background(matrix)
    matrix = bkg.subfrom(matrix)
    val noise = Array.fill[Double](matrix.length, matrix(0).length)(1.0)
    val objects: Array[Sepobj] = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat)
    println("Scala: extract: extracted " + objects.length + " objects")

    println("==================ellipse_test() test 1 ellipse_coeffs()================")
    var a: Array[Double] = Array.ofDim[Double](objects.length)
    var b: Array[Double] = Array.ofDim[Double](objects.length)
    var theta: Array[Double] = Array.ofDim[Double](objects.length)

    for (i <- (0 until objects.length)) {
      a(i) = objects(i).a
      b(i) = objects(i).b
      theta(i) = objects(i).theta
    }

    var (cxx, cyy, cxy) = ex.ellipse_coeffs(a, b, theta)

    for (i <- (0 until 10))
      println("cxx: " + cxx(i) + "\tcyy: " + cyy(i) + "\tcxy: " + cxy(i))

    println("==================ellipse_test() test 1 ellipse_axes()================")
    var xx: Array[Double] = Array.ofDim[Double](objects.length)
    var yy: Array[Double] = Array.ofDim[Double](objects.length)
    var xy: Array[Double] = Array.ofDim[Double](objects.length)
    for (i <- (0 until objects.length)) {
      xx(i) = objects(i).cxx
      yy(i) = objects(i).cyy
      xy(i) = objects(i).cxy
    }

    var (aa, bb, tt) = ex.ellipse_axes(xx, yy, xy)
    for (i <- (0 until 10))
      println("a: " + aa(i) + "\tb: " + bb(i) + "\ttheta: " + tt(i))

    println("==================ellipse_test() test 1 round_trip()================")
    var (cxx1, cyy1, cxy1) = ex.ellipse_coeffs(aa, bb, tt)
    for (i <- (0 until 10))
      println("cxx: " + cxx1(i) + "\tcyy: " + cyy1(i) + "\tcxy: " + cxy1(i))
  }

  def kron_radiusTest() {
    val ex = new Extractor
    var matrix: Array[Array[Double]] = Utils.load("/root/Kira/scratch/spark-ec2/data/image1.fits")
    val bkg = new Background(matrix)
    matrix = bkg.subfrom(matrix)
    val noise = Array.fill[Double](matrix.length, matrix(0).length)(1.0)
    val objects: Array[Sepobj] = ex.extract(matrix, (1.5 * bkg.bkgmap.globalrms).toFloat)
    println("Scala: extract: extracted " + objects.length + " objects")

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

    var (kr, flag) = ex.kron_radius(matrix, x, y, a, b, theta, r)
    for (i <- (0 until 10))
      println("kr: " + kr(i) + "\tflag: " + flag(i))
  }

  def mask_ellipseTest() {
    val ex = new Extractor
    var mask = Array.fill[Boolean](20, 20) { false }
    val x = Array.fill(1) { 10.0 }
    val y = Array.fill(1) { 10.0 }
    val a = Array.fill(1) { 1.0 }
    val b = Array.fill(1) { 1.0 }
    val theta = Array.fill(1) { 0.0 }
    val r = Array.fill(1) { 1.001 }
    println("==================mask_ellipseTest() test 1================")
    val retmatrix = ex.mask_ellipse(mask, x, y, a = a, b = b, theta = theta, r)
    println(retmatrix.map(r => (r.map(x => if (x == true) 1 else 0).reduce(_ + _))).reduce(_ + _))

    println("==================mask_ellipseTest() test 2================")
    val r2 = Array.fill(1) { 2.001 }
    val retmatrix2 = ex.mask_ellipse(mask, x, y, a = a, b = b, theta = theta, r2)
    println(retmatrix2.map(r => (r.map(x => if (x == true) 1 else 0).reduce(_ + _))).reduce(_ + _))
  }

  def batch_Test(input: String, output: String) {
    val src = input
    val results = extract(src)
    val writer = new PrintWriter(new File(output))
    results.map(r => writer.write(r.toString + "\n"))
    writer.close()
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

  def main(args: Array[String]) {
    /*backTest()
    sumCircleTest()
    sumEllipseTest()
    extractTest()
    ellipseTest()
    kron_radiusTest()
    mask_ellipseTest()*/
    batch_Test(args(0), args(1))
  }
}*/ 