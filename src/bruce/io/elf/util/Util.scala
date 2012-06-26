package bruce.io.elf.util

import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.nio.channels.FileChannel
import bruce.io.elf.buffer.ChannelBuffer

object Util {

  def showRemaining(data: ByteBuffer) {
    val oldLimit = data.limit()
    val oldPosition = data.position()

    val arr = new Array[Byte](data.remaining())
    data.get(arr)
    println("show remaining:\n" + new String(arr))

    data.limit(oldLimit).position(oldPosition)
  }
  
  def find(array: Array[Byte], pattern: Array[Byte]): Int = find(array, 0, array.length, pattern)

  def find(array: Array[Byte], start: Int, end: Int, pattern: Array[Byte]): Int = {
    if ((end - start) < pattern.length) return -1

    var texti = start
    var pi = 0

    while (pi < pattern.length && texti < end) {
      val t = array(texti)
      val p = pattern(pi)

      if (t != p) {
        texti = texti - pi + 1
        pi = 0
      } else {
        texti = texti + 1
        pi = pi + 1
      }
    }

    if (pi == pattern.length) (texti - pi) else -1
  }

  def find(buffer: ByteBuffer, target: Array[Byte]): Int = find(buffer, target, buffer.position())

  def find(buffer: ByteBuffer, target: Array[Byte], startIndex: Int): Int = {
    if (buffer.remaining() < target.length) return -1

    val oldPosition = buffer.position()
    val limit = buffer.limit()
    val targetLength = target.length
    //    val end = buffer.limit() - targetLength - 1
    var bi = startIndex
    var ti = 0

    while (bi < limit && ti < targetLength) {
      val s = buffer.get(bi)
      val t = target(ti)
      if (s != t) {
        bi = bi - ti + 1
        ti = 0
      } else {
        bi = bi + 1
        ti = ti + 1
      }
    }

    buffer.position(oldPosition)

    if (ti == targetLength) bi - ti else -1
  }
  
  def main(args: Array[String]) {
    val text = "0123456789abcdef".getBytes()
    val pattern = "g".getBytes()
    
    val p = find(text, pattern)
    
    println(p)
    
  }
  
}