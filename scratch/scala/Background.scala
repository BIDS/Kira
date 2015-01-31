import java.lang.{Double =>jDouble}

class Background(matrix:Array[Array[Double]], mask:Array[Array[Boolean]], maskthresh:Double, 
  bw:Int, bh:Int, fw:Int, fh:Int, fthresh:Double){
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

  val data: Array[Byte] = flatten(matrix)
  println(data.mkString(" "))
  val h = matrix.length
  val w = matrix(0).length
  val status = sep_makeback(data, mask, SEP_TDOUBLE, SEP_TBYTE, w, h, bw, bh, 0.0, fw, fh, 0.0, bkgmap)
  

  @native
  def sep_makeback(data:Array[Byte], mask:Array[Array[Boolean]], dtype:Int, mdtype:Int, 
    w:Int, h:Int, bw:Int, bh:Int, mthresh:Double, fw:Int, fh:Int, fthresh:Double, backmap:Sepbackmap):Int

	def flatten(matrix:Array[Array[Double]]):Array[Byte] = {
    val h = matrix.length
    val w = matrix(0).length
    var stream = new Array[Byte](w*h*8)

    for(i <- (0 until h)){
      for(j <- (0 until w)){
        var l: Long = jDouble.doubleToLongBits(matrix(i)(j))
        println(matrix(i)(j))
        println(l)
        for(k <- (0 until 8)){
          stream((i*w+j)*8+k) = ((l >>> (k * 8)) & 0xff).toByte
        }
      }
    }
    stream
  }


   
}

class Sepbackmap(var w:Int, var h:Int, var globalback:Float, var globalrms:Float, var bw:Int, 
  var bh:Int, var nx:Int, var ny:Int, var n:Int, var back:Array[Float], var dback:Array[Float], 
  var sigma:Array[Float], var dsigma:Array[Float]){
  back = new Array[Float](n)
  dback = new Array[Float](n)
  sigma = new Array[Float](n)
  dsigma = new Array[Float](n)

  def this(){
    this(0, 0, 0.0F, 0.0F, 0, 0, 0, 0, 0, null, null, null, null)
  }

  def setBack(vback: Array[Float]){
    back = new Array[Float](vback.length)
    for(i <- (0 until back.length)){
      back(i) = vback(i)
    }
  }

  def setDback(vdback: Array[Float]){
    dback = new Array[Float](vdback.length)
    for(i <- (0 until dback.length)){
      dback(i) = vdback(i)
    }
  }

  def setSigma(vsigma: Array[Float]){
    sigma = new Array[Float](vsigma.length)
    for(i <- (0 until sigma.length)){
      sigma(i) = vsigma(i)
    }
  }

  def setDsigma(vdsigma: Array[Float]){
    dsigma = new Array[Float](vdsigma.length)
    for(i <- (0 until dsigma.length)){
      dsigma(i) = vdsigma(i)
    }
  }
}

class Sepobj(thresh:Double, npix:Int, tnpix:Int, xmin:Int, xmax:Int,
  ymin:Int, ymax:Int, x:Double, y:Double, x2:Double, y2:Double, 
  xy:Double, a:Float, b:Float, theta:Float, cxx:Float, cyy:Float,
  cxy:Float, cflux:Float, cpeak:Float, peak:Float, xpeak:Float, ypeak:Float,
  xcpeak:Float, ycpeak:Float, flag:Short, pix:Int)

object Test{
	def main(args: Array[String]){
    var matrix = Array.ofDim[Double](6, 6)
    for(i <- (0 until matrix.length)){
      for(j <- (0 until matrix(i).length)){
        matrix(i)(j) = 0.1
      }
    }
    matrix(1)(1) = 1.0
    matrix(1)(4) = 1.0
    matrix(4)(1) = 1.0
    matrix(4)(4) = 1.0

    var mask = Array.ofDim[Boolean](6, 6)
    for(i <- (0 until mask.length)){
      for(j <- (0 until mask(i).length)){
        mask(i)(j) = false
      }
    }

    for(r <- matrix)
      println(r.mkString(" "))

    var bkg = new Background(matrix, mask, 0.0, 3, 3, 1, 1, 0.0)
    println("Scala: bkgmap: globalback: "+bkg.bkgmap.globalback+"\t globalrms:"+bkg.bkgmap.globalrms)
    println("Scala: bkgmap: w: "+bkg.bkgmap.w+"\t h: "+bkg.bkgmap.h);
    println("Scala: bkgmap: bw: "+bkg.bkgmap.bw+"\t h: "+bkg.bkgmap.bh);
    println("Scala: bkgmap: nx: "+bkg.bkgmap.nx+"\t ny: "+bkg.bkgmap.ny+"\t n: "+bkg.bkgmap.n);
    println("Scala: bkgmap: back: "+bkg.bkgmap.back.mkString(", "))
    println("Scala: bkgmap: dback: "+bkg.bkgmap.dback.mkString(", "))
    println("Scala: bkgmap: sigma: "+bkg.bkgmap.sigma.mkString(", "))
    println("Scala: bkgmap: dsigma: "+bkg.bkgmap.dsigma.mkString(", "))
  }
}