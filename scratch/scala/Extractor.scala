import util.Random

class Extractor{
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

  @native
  def sep_sum_circle(data:Array[Byte], err:Array[Byte], mask:Array[Byte],
    dtype:Int, edtype:Int, mdtype:Int, w:Int, h:Int,
    maskthresh:Double, gain:Double, inflags:Short,
    x:Array[Double], y:Array[Double], r:Double, subpix:Int, sum:Array[Double],
    sumerr:Array[Double], area:Array[Double], flag:Array[Short]):Int

  @native
  def sep_sum_circann(data:Array[Byte], err:Array[Byte], mask:Array[Byte],
    dtype:Int, edtype:Int, mdtype:Int, w:Int, h:Int,
    maskthresh:Double, gain:Double, inflags:Short,
    x:Array[Double], y:Array[Double], rin:Double, rout:Double, subpix:Int, sum:Array[Double],
    sumerr:Array[Double], area:Array[Double], flag:Array[Short]):Int

  @native
  def sep_sum_ellipse(data:Array[Byte], err:Array[Byte], mask:Array[Byte],
    dtype:Int, edtype:Int, mdtype:Int, w:Int, h:Int,
    maskthresh:Double, gain:Double, inflags:Short,
    x:Array[Double], y:Array[Double], a:Array[Double], b:Array[Double], 
    theta:Array[Double], r:Double, subpix:Int, sum:Array[Double],
    sumerr:Array[Double], area:Array[Double], flag:Array[Short]):Int

  @native
  def sep_sum_ellipann(data:Array[Byte], err:Array[Byte], mask:Array[Byte],
    dtype:Int, edtype:Int, mdtype:Int, w:Int, h:Int,
    maskthresh:Double, gain:Double, inflags:Short,
    x:Array[Double], y:Array[Double], a:Array[Double], b:Array[Double], 
    theta:Array[Double], rin:Double, rout:Double, subpix:Int, sum:Array[Double],
    sumerr:Array[Double], area:Array[Double], flag:Array[Short]):Int

  @native
  def sep_extract(data:Array[Byte], noise:Array[Byte], dtype:Int, ndtype:Int, 
    noise_flag:Short, w:Int, h:Int, thresh:Float, minarea:Int, conv:Array[Byte],
    convw:Int, convh:Int, deblend_nthresh:Int, deblend_cont:Double, clean_flag:Boolean,
    clean_param:Double, objects:Array[Sepobj], nobj:Int):Int

  @native
  def sep_kron_radius(data:Array[Byte], mask:Array[Byte], dtype:Int, mdtype:Int,
    w:Int, h:Int, maskthresh:Double, x:Array[Double], y:Array[Double], 
    cxx:Array[Double], cyy:Array[Double], cxy:Array[Double], r:Array[Double], 
    kronrad:Array[Double], flag:Array[Short])

  @native
  def sep_ellipse_coeffs(a:Array[Double], b:Array[Double], theta:Array[Double], cxx:Array[Double], cyy:Array[Double], cxy:Array[Double])

  @native
  def sep_ellipse_axes(cxx:Array[Double], cyy:Array[Double], cxy:Array[Double], a:Array[Double], b:Array[Double], theta:Array[Double])

  def sum_circle(matrix:Array[Array[Double]], x:Array[Double], y:Array[Double], r:Double, 
    variance:Array[Array[Double]]=null, err:Array[Array[Double]]=null, gain:Double=0.0, mask:Array[Array[Double]]=null, 
    maskthresh:Double=0.0, bkgann:Array[Double]=null, subpix:Int=5):(Array[Double], Array[Double], Array[Short]) = {
    val dtype = SEP_TDOUBLE
    val edtype = if (err==null) 0 else SEP_TDOUBLE;
    val mdtype = if (err==null) 0 else SEP_TBYTE;
    val h = matrix.length
    val w = matrix(0).length
    val ptr = Utils.flatten(matrix)
    val eptr = if (err==null) null else Utils.flatten(err)
    val mptr = if (mask==null) null else Utils.flatten(mask)
    /**WARNING, need to implement the _parse_arrays function in the python implementation, this is a temporary solution here*/
    var inflag:Short = 0

    var status = 0
    var sum = Array.ofDim[Double](x.length)
    var sumerr = Array.ofDim[Double](x.length)
    var area = Array.ofDim[Double](x.length)
    var flag = Array.ofDim[Short](x.length)
    if(bkgann == null){
      status = sep_sum_circle(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, r, 
        subpix, sum, sumerr, area, flag)
    }
    else{
      var flux1 = Array.ofDim[Double](x.length)
      var fluxerr1 = Array.ofDim[Double](x.length)
      var area1 = Array.ofDim[Double](x.length)
      var flag1 = Array.ofDim[Short](x.length)
      status = sep_sum_circle(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, r, 
        subpix, flux1, fluxerr1, area1, flag1)

      var bkgflux = Array.ofDim[Double](x.length)
      var bkgfluxerr = Array.ofDim[Double](x.length)
      var bkgarea = Array.ofDim[Double](x.length)
      var bkgflag = Array.ofDim[Short](x.length)
      val rin = bkgann(0)
      val rout = bkgann(1)
      inflag = (inflag | SEP_MAST_IGNORE).toShort
      status = sep_sum_circann(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, 
        rin, rout, subpix, bkgflux, bkgfluxerr, bkgarea, bkgflag) 

      for(i <- (0 until x.length)){
        sum(i) = flux1(i) - bkgflux(i)/bkgarea(i)*area1(i);
        sumerr(i) = fluxerr1(i)*fluxerr1(i)+(bkgfluxerr(i)/bkgarea(i)*area1(i))
        flag(i) = flag1(i)
      }
    }
    println("Scala: sum_circle: return value: "+status)
    return (sum, sumerr, flag)
  }

  def sum_circann(matrix:Array[Array[Double]], x:Array[Double], y:Array[Double], rin:Double, rout:Double,  
    variance:Array[Array[Double]]=null, err:Array[Array[Double]]=null, gain:Double=0.0, mask:Array[Array[Double]]=null, 
    maskthresh:Double=0.0, bkgann:Array[Double]=null, subpix:Int=5):(Array[Double], Array[Double], Array[Short]) = {
    val dtype = SEP_TDOUBLE
    val edtype = if (err==null) 0 else SEP_TDOUBLE;
    val mdtype = if (err==null) 0 else SEP_TBYTE;
    val h = matrix.length
    val w = matrix(0).length
    val ptr = Utils.flatten(matrix)
    val eptr = if (err==null) null else Utils.flatten(err)
    val mptr = if (mask==null) null else Utils.flatten(mask)
    /**WARNING, need to implement the _parse_arrays function in the python implementation, this is a temporary solution here*/
    val inflag:Short = 0

    var sum = Array.ofDim[Double](x.length)
    var sumerr = Array.ofDim[Double](x.length)
    var area = Array.ofDim[Double](x.length)
    var flag = Array.ofDim[Short](x.length)

    val status = sep_sum_circann(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain,
      inflag, x, y, rin, rout, subpix, sum, sumerr, area, flag)

    return (sum, sumerr, flag)
  }

  def sum_ellipse(matrix:Array[Array[Double]], x:Array[Double], y:Array[Double], a:Array[Double], b:Array[Double], 
    theta:Array[Double], r:Double, 
    variance:Array[Array[Double]]=null, err:Array[Array[Double]]=null, gain:Double=0.0, mask:Array[Array[Double]]=null, 
    maskthresh:Double=0.0, bkgann:Array[Double]=null, subpix:Int=5):(Array[Double], Array[Double], Array[Short]) = {
    val dtype = SEP_TDOUBLE
    val edtype = if (err==null) 0 else SEP_TDOUBLE;
    val mdtype = if (err==null) 0 else SEP_TBYTE;
    val h = matrix.length
    val w = matrix(0).length
    val ptr = Utils.flatten(matrix)
    val eptr = if (err==null) null else Utils.flatten(err)
    val mptr = if (mask==null) null else Utils.flatten(mask)
    /**WARNING, need to implement the _parse_arrays function in the python implementation, this is a temporary solution here*/
    var inflag:Short = 0

    var sum = Array.ofDim[Double](x.length)
    var sumerr = Array.ofDim[Double](x.length)
    var area = Array.ofDim[Double](x.length)
    var flag = Array.ofDim[Short](x.length)

    var status = 0
    if(bkgann == null){
      status = sep_sum_ellipse(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, 
        a, b, theta, r, subpix, sum, sumerr, area, flag)
    }
    else{
      var flux1 = Array.ofDim[Double](x.length)
      var fluxerr1 = Array.ofDim[Double](x.length)
      var area1 = Array.ofDim[Double](x.length)
      var flag1 = Array.ofDim[Short](x.length)
      status = sep_sum_ellipse(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, 
        a, b, theta, r, subpix, flux1, fluxerr1, area1, flag1)

      var bkgflux = Array.ofDim[Double](x.length)
      var bkgfluxerr = Array.ofDim[Double](x.length)
      var bkgarea = Array.ofDim[Double](x.length)
      var bkgflag = Array.ofDim[Short](x.length)
      val rin = bkgann(0)
      val rout = bkgann(1)
      inflag = (inflag | SEP_MAST_IGNORE).toShort
      status = sep_sum_ellipann(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, 
        a, b, theta, rin, rout, subpix, bkgflux, bkgfluxerr, bkgarea, bkgflag) 

      for(i <- (0 until x.length)){
        sum(i) = flux1(i) - bkgflux(i)/bkgarea(i)*area1(i);
        sumerr(i) = fluxerr1(i)*fluxerr1(i)+(bkgfluxerr(i)/bkgarea(i)*area1(i))
        flag(i) = flag1(i)
      }
    }
    return (sum, sumerr, flag)
  }

  def sum_ellipann(matrix:Array[Array[Double]], x:Array[Double], y:Array[Double], a:Array[Double], b:Array[Double], 
    theta:Array[Double], rin:Double, rout:Double, 
    variance:Array[Array[Double]]=null, err:Array[Array[Double]]=null, gain:Double=0.0, mask:Array[Array[Double]]=null, 
    maskthresh:Double=0.0, bkgann:Array[Double]=null, subpix:Int=5):(Array[Double], Array[Double], Array[Short]) = {
    val dtype = SEP_TDOUBLE
    val edtype = if (err==null) 0 else SEP_TDOUBLE;
    val mdtype = if (err==null) 0 else SEP_TBYTE;
    val h = matrix.length
    val w = matrix(0).length
    val ptr = Utils.flatten(matrix)
    val eptr = if (err==null) null else Utils.flatten(err)
    val mptr = if (mask==null) null else Utils.flatten(mask)
    /**WARNING, need to implement the _parse_arrays function in the python implementation, this is a temporary solution here*/
    var inflag:Short = 0

    var sum = Array.ofDim[Double](x.length)
    var sumerr = Array.ofDim[Double](x.length)
    var area = Array.ofDim[Double](x.length)
    var flag = Array.ofDim[Short](x.length)

    var status = 0
    if(bkgann == null){
      status = sep_sum_ellipann(ptr, eptr, mptr, dtype, edtype, mdtype, w, h, maskthresh, gain, inflag, x, y, 
        a, b, theta, rin, rout, subpix, sum, sumerr, area, flag)
    }
    return (sum, sumerr, flag)
  }

  def init_obj(thresh:Double, npix:Int, tnpix:Int, xmin:Int, xmax:Int, ymin:Int, ymax:Int, x:Double, y:Double, x2:Double, y2:Double, 
    xy:Double, a:Float, b:Float, theta:Float, cxx:Float, cyy:Float, cxy:Float, cflux:Float, flux:Float, cpeak:Float, peak:Float, xpeak:Int, ypeak:Int, 
      xcpeak:Int, ycpeak:Int, flag:Short, pix:Int):Sepobj = {
    var obj = new Sepobj(thresh, npix, tnpix, xmin, xmax, ymin, ymax, 
      x, y, x2, y2, xy, a, b, theta, cxx, cyy, cxy, cflux, flux, cpeak, peak, 
      xpeak, ypeak, xcpeak, ycpeak, flag, pix)
    return obj  
  }

  val default_conv = Array(Array(1.0, 2.0, 1.0), Array(2.0, 4.0, 2.0), Array(1.0, 2.0, 1.0))
  def extract(matrix:Array[Array[Double]], thresh:Float, noise:Array[Array[Double]]=null, minarea:Int=5, 
    conv:Array[Array[Double]]=default_conv, deblend_nthresh:Int=32, deblend_cont:Double=0.005, 
    clean:Boolean=true, clean_param:Double=1.0):Array[Sepobj]={
    val dtype = SEP_TDOUBLE
    val ndtype = SEP_TDOUBLE

    val h = matrix.length
    val w = matrix(0).length
    val data = Utils.flatten(matrix)

    val convh = if(conv == null) 0 else conv.length
    val convw = if(conv == null) 0 else conv(0).length
    val cstream = if(conv == null) null else Utils.flatten(conv)

    val nstream = if(noise == null) null else Utils.flatten(noise)

    var objects:Array[Sepobj] = Array.ofDim[Sepobj](8192)
    val nobj = sep_extract(data, nstream, dtype, ndtype, 0.toShort, w, h, thresh,
      minarea, cstream, convw, convh, deblend_nthresh, deblend_cont, clean, clean_param, objects, 0)

    var retobjs = Array.ofDim[Sepobj](nobj)
    for(i <- (0 until nobj)){
      retobjs(i) = objects(i)
    }
    return retobjs
  }

  def kron_radius(matrix:Array[Array[Double]], x:Array[Double], y:Array[Double], a:Array[Double], 
    b:Array[Double], theta:Array[Double], r:Array[Double], mask:Array[Array[Double]]=null, 
    maskthresh:Double=0.0):(Array[Double], Array[Short]) = {
    val data = Utils.flatten(matrix)
    val mstream = if(mask == null) null else Utils.flatten(mask)
    val mdtype = if(mask == null) 0 else SEP_TBYTE

    val (cxx, cyy, cxy) = ellipse_coeffs(a, b, theta)

    val h = matrix.length
    val w = matrix(0).length
    var kr = Array.ofDim[Double](a.length)
    var flag = Array.ofDim[Short](a.length)

    sep_kron_radius(data, mstream, SEP_TDOUBLE, mdtype, w, h, maskthresh, x, y, cxx, cyy, cxy, r, kr, flag)

    return (kr, flag)
  }

  def ellipse_coeffs(a:Array[Double], b:Array[Double], theta:Array[Double]):(Array[Double], Array[Double], Array[Double]) = {
    var cxx = Array.ofDim[Double](a.length)
    var cyy = Array.ofDim[Double](a.length)
    var cxy = Array.ofDim[Double](a.length)

    sep_ellipse_coeffs(a, b, theta, cxx, cyy, cxy)

    return(cxx, cyy, cxy)
  }

  def ellipse_axes(cxx:Array[Double], cyy:Array[Double], cxy:Array[Double]):(Array[Double], Array[Double], Array[Double]) = {
    var a = Array.ofDim[Double](cxx.length)
    var b = Array.ofDim[Double](cxx.length)
    var theta = Array.ofDim[Double](cxx.length)

    sep_ellipse_axes(cxx, cyy, cxy, a, b, theta)

    return(a, b, theta)
  }
}