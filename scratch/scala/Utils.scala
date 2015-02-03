import java.lang.{Double =>jDouble}
import java.nio.ByteBuffer;

object Utils{
  /**Helper function that converts 2d double matrix to 1d byte stream*/
  def flatten(matrix:Array[Array[Double]]):Array[Byte] = {
    val h = matrix.length
    val w = matrix(0).length
    var stream = new Array[Byte](w*h*8)

    for(i <- (0 until h)){
      for(j <- (0 until w)){
        var l: Long = jDouble.doubleToLongBits(matrix(i)(j))
        for(k <- (0 until 8)){
          stream((i*w+j)*8+k) = ((l >>> (k * 8)) & 0xff).toByte
        }
      }
    }
    stream
  }

  /**Helper function that converts 1d byte stream to 2d double matrix*/
  def deflatten(data:Array[Byte], h:Int, w:Int):Array[Array[Double]] = {
    var matrix = Array.ofDim[Double](h, w)
    for(i <- (0 until h)){
      for(j <- (0 until w)){
        var bytes = new Array[Byte](8)
        for(k <- (0 until 8)){
          bytes(7-k) = (data((i*w+j)*8+k) & 0xff).toByte
        }
        matrix(i)(j) = ByteBuffer.wrap(bytes).getDouble()
      }
    }
    return matrix
  } 
}