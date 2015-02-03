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