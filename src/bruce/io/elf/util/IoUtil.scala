package bruce.io.elf.util

import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel
import java.nio.channels.ReadableByteChannel

object IoUtil {

  def readAllPossible(sc: ReadableByteChannel, buffer: ByteBuffer): scala.Tuple2[Int, Boolean] = {
    var totalRead = 0
    var lastReadSize = 0
    do {
      totalRead = totalRead + lastReadSize
      lastReadSize = sc.read(buffer)
    } while (lastReadSize > 0)

    scala.Tuple2(totalRead, lastReadSize == -1)
  }

  def writeAllPossible(sc: WritableByteChannel, data: ByteBuffer): Int = {
    var total = 0
    var one = 0
    do {
      total = total + one
      one = sc.write(data)
    } while (one > 0)

    total
  }

  def writeAll(fc: WritableByteChannel, data: ByteBuffer): Int = {
    var total = 0
    var one = 0
    do {
      one = fc.write(data)
      total = total + one
    } while (data.hasRemaining())

    total
  }

}