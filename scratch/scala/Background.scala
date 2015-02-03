import util.Random


class Background(matrix:Array[Array[Double]], mask:Array[Array[Boolean]]=null, maskthresh:Double=0.0, 
  bw:Int=64, bh:Int=64, fw:Int=3, fh:Int=3, fthresh:Double=0.0){
	/**Macro definitions from sep.h*/
	val SEP_TBYTE :Int = 11
	val SEP_TINT :Int = 31
	val SEP_TFLOAT :Int = 42
	val SEP_TDOUBLE :Int = 82

	val SUPPORTED_IMAGES_DTYPES = Array[Int](SEP_TDOUBLE, SEP_TFLOAT, SEP_TINT)

	/**Input flag values*/
	val SEP_ERROR_IS_VAR :Int = 0x0001
	val SEP_ERROR_IS_ARRAY :Int = 0x0002
	val SEP_MAST_IGNORE :Int = 0x0004

	/**Output flag values accesible from Scala*/
	val OBJ_MERGED :Short = 0x0001
	val OBJ_TRUNC :Short = 0x0002
	val OBJ_DOVERFLOW :Short = 0x0004
	val OBJ_SINGU :Short = 0x0008
	val APER_TRUNC :Short = 0x0010
	val APER_HASMASKED :Short = 0x0020
	val APER_ALLMASKED :Short = 0x0040
	val APER_NONPOSITIVE :Short = 0x0080

	/**Macro definition from sepcore.h*/
	val MEMORY_ALLOC_ERROR :Int = 1 

  System.loadLibrary("BackgroundImpl")
  var bkgmap = new Sepbackmap()

  val data: Array[Byte] = Utils.flatten(matrix)
  val h = matrix.length
  val w = matrix(0).length
  val status = sep_makeback(data, mask, SEP_TDOUBLE, SEP_TBYTE, w, h, bw, bh, 0.0, fw, fh, 0.0, bkgmap)
  

  @native
  def sep_makeback(data:Array[Byte], mask:Array[Array[Boolean]], dtype:Int, mdtype:Int, 
    w:Int, h:Int, bw:Int, bh:Int, mthresh:Double, fw:Int, fh:Int, fthresh:Double, backmap:Sepbackmap):Int

  @native
  def sep_backarray(bkgmap:Sepbackmap, data:Array[Byte], dtype:Int, back:Array[Float], dback:Array[Float], 
    sigma:Array[Float], dsigma:Array[Float]):Int

  @native
  def sep_subbackarray(bkgmap:Sepbackmap, data:Array[Byte], dtype:Int, 
    back:Array[Float], dback:Array[Float], sigma:Array[Float], dsigma:Array[Float]):Int

  def back(dtype:Int):Array[Array[Double]] = {
    var result = Array.ofDim[Double](bkgmap.h, bkgmap.w)
    var data = new Array[Byte](bkgmap.h*bkgmap.w*8)
    val status = sep_backarray(bkgmap, data, dtype, bkgmap.back, bkgmap.dback, bkgmap.sigma, bkgmap.dsigma)
    println("Scala: back: return: "+status)
    result = Utils.deflatten(data, bkgmap.h, bkgmap.w)
    for(r <- result){
      println(r.mkString(", "))
    }
    return result
  }

  def subfrom(matrix:Array[Array[Double]]): Array[Array[Double]] = {
    /**need to check the shape of the matrices*/
    var data = Utils.flatten(matrix)
    val status = sep_subbackarray(bkgmap, data, SEP_TDOUBLE, bkgmap.back, bkgmap.dback, bkgmap.sigma, bkgmap.dsigma)
    var result = Utils.deflatten(data, matrix.length, matrix(0).length)
    println("Scala: subfrom: result: ")
    for(r <- result){
      println(r.mkString(", "))
    }
    return result
  }
}

object Test{
	def backTest(){
    val dim:Int = 6
    var matrix = Array.ofDim[Double](dim, dim)
    for(i <- (0 until matrix.length)){
      for(j <- (0 until matrix(i).length)){
        matrix(i)(j) = 0.1
      }
    }
    matrix(1)(1) = 1.0
    matrix(1)(4) = 1.0
    matrix(4)(1) = 1.0
    matrix(4)(4) = 1.0

    var mask = Array.ofDim[Boolean](dim, dim)
    for(i <- (0 until mask.length)){
      for(j <- (0 until mask(i).length)){
        mask(i)(j) = false
      }
    }

    var bkg = new Background(matrix, mask, 0.0, 3, 3, 1, 1, 0.0)
    //var bkg = new Background(matrix)
    println("Scala: bkgmap: globalback: "+bkg.bkgmap.globalback+"\t globalrms:"+bkg.bkgmap.globalrms)
    println("Scala: bkgmap: w: "+bkg.bkgmap.w+"\t h: "+bkg.bkgmap.h);
    println("Scala: bkgmap: bw: "+bkg.bkgmap.bw+"\t h: "+bkg.bkgmap.bh);
    println("Scala: bkgmap: nx: "+bkg.bkgmap.nx+"\t ny: "+bkg.bkgmap.ny+"\t n: "+bkg.bkgmap.n);
    println("Scala: bkgmap: back: "+bkg.bkgmap.back.mkString(", "))
    println("Scala: bkgmap: dback: "+bkg.bkgmap.dback.mkString(", "))
    println("Scala: bkgmap: sigma: "+bkg.bkgmap.sigma.mkString(", "))
    println("Scala: bkgmap: dsigma: "+bkg.bkgmap.dsigma.mkString(", "))
    var result = bkg.back(bkg.SEP_TDOUBLE)
    result = bkg.subfrom(result)
  }

  def sumCircleTest(){
    val ex = new Extractor
    val naper = 1000
    val x = Array.fill(naper)(Random.nextDouble*(800-200)+200.0)
    val y = Array.fill(naper)(Random.nextDouble*(800-200)+200.0)
    val r = 3.0
    val matrix = Array.fill[Double](naper, naper){1.0}
    val mask = Array.fill[Boolean](naper, naper){false}

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
    val (sum3, sumerr3, flag3) = ex.sum_circle(matrix, x, y, r, bkgann=bkgann, subpix=1)
    println("Scala: sum_circle with bkgann result: ")
    println(sum3.take(10).mkString(", "))
  }

  def sumEllipseTest(){
    val ex = new Extractor
    val naper = 1000
    val x = Array.fill(naper)(Random.nextDouble*(800-200)+200.0)
    val y = Array.fill(naper)(Random.nextDouble*(800-200)+200.0)
    var r = 3.0
    val matrix = Array.fill[Double](naper, naper){1.0}
    val mask = Array.fill[Boolean](naper, naper){false}
    val aa = Array.fill[Double](naper){1.0}
    val tt = Array.fill[Double](naper){0.0}

    println("==================sum_ellipse() test================")
    val (sum, sumerr, flag) = ex.sum_ellipse(matrix, x, x, aa, aa, tt, r)
    println("Scala: sum_ellipse result: ")
    println(sum.take(10).mkString(", "))

    println("==================sum_ellipann() test================")
    val pi = 3.1415926
    var a = Array.fill(naper)(1.0)
    val theta = Array.fill(naper)(Random.nextDouble*pi - pi/2)
    val ratio = Array.fill(naper)(Random.nextDouble*0.8 + 0.2)
    val rin = 3.0
    val rout = rin * 1.1
    val (sum1, sumerr1, flag1) = ex.sum_ellipann(matrix, x, y, a, ratio, theta, rin, rout, subpix=0)
    println("Scala: sum_ellipann result: ")
    println(sum1.take(10).mkString(", "))

    println("==================sum_ellipann() with bkgann test================")
    a = Array.fill(naper)(2.0)
    val b = Array.fill(naper)(1.0)
    val t = Array.fill(naper)(pi/4)
    r = 5.0
    val bkgann = Array(6.0, 8.0)

    val (sum2, sumerr2, flag2) = ex.sum_ellipse(matrix, x, y, a, b, t, r, bkgann=bkgann)
    println("Scala: sum_ellipse with bkgann result: ")
    println(sum2.take(10).mkString(", "))
  }

  def main(args: Array[String]){
    //backTest()
    sumCircleTest()
    sumEllipseTest()
  }
}