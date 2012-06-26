package bruce.io.elf.buffer

import java.nio.ByteOrder
import java.nio.ByteBuffer
import bruce.io.elf.util.Util

trait ChannelBuffer {
  val capacity: Int = 0

  var readIndex = 0
  protected var readMark = 0
  var writeIndex = 0
  protected var writeMark = 0

  def get(i: Int): Byte

  def get(dst: Array[Byte]): ChannelBuffer

  def get(dst: Array[Byte], offset: Int, length: Int): ChannelBuffer

  def put(src: Array[Byte]): ChannelBuffer

  def put(src: Array[Byte], offset: Int, length: Int): ChannelBuffer

  def get(): Byte

  def put(byte: Byte): ChannelBuffer

  def getChar(): Char

  def putChar(ch: Char): ChannelBuffer

  def getShort(): Short

  def putShort(sh: Short): ChannelBuffer

  def getInt(): Int

  def putInt(int: Int): ChannelBuffer

  def getLong(): Long

  def putLong(long: Long): ChannelBuffer

  def markRead(): ChannelBuffer = {
    readMark = readIndex
    this
  }

  def markWrite(): ChannelBuffer = {
    writeMark = writeIndex
    this
  }

  def resetReadIndex(): ChannelBuffer = {
    readIndex = readMark
    this
  }

  def resetWriteIndex(): ChannelBuffer = {
    writeIndex = writeMark
    this
  }

  def setIndex(newReadIndex: Int, newWriteIndex: Int): ChannelBuffer = {
    readIndex = newReadIndex
    writeIndex = newWriteIndex
    this
  }

  /**
   * 表示可读数据的ByteBuffer
   */
  def readableByteBuffer(): ByteBuffer

  /**
   * 表示数据可写空间的ByteBuffer
   */
  def writeableByteBuffer(): ByteBuffer

  /**
   * 清除标记
   */
  def clear(): ChannelBuffer

  /**
   * 压缩已读的空间
   */
  def discardReaded(): ChannelBuffer

  /**
   * 返回可读的字节数
   */
  def readable(): Int = writeIndex - readIndex

  /**
   * 返回可写的字节数
   */
  def writable(): Int

  /**
   * 返回底层的缓冲数组
   */
  def bufferArray(): Array[Byte]

  def byteOrder: ByteOrder

  def isDirect(): Boolean

  def hasArray(): Boolean

}

object ChannelBuffer {
  def apply(capacity: Int): ChannelBuffer = apply(capacity, ByteOrder.BIG_ENDIAN)

  def apply(capacity: Int, byteOrder: ByteOrder): ChannelBuffer = apply(capacity, byteOrder, false)

  def apply(capacity: Int, byteOrder: ByteOrder, isDirect: Boolean): ChannelBuffer = {
    if (isDirect) {
      null
    } else {
      if (byteOrder == ByteOrder.BIG_ENDIAN) new BigEndianChannelBuffer(capacity)
      else new LittleEndianHeapChannelBuffer(capacity)
    }
  }

  def apply(array: Array[Byte]): ChannelBuffer = apply(array, 0, array.length)

  def apply(array: Array[Byte], start: Int, end: Int): ChannelBuffer = apply(array, 0, array.length, ByteOrder.BIG_ENDIAN)

  def apply(array: Array[Byte], start: Int, end: Int, byteOrder: ByteOrder): ChannelBuffer = {
    var buffer: ChannelBuffer = null
    
    if (byteOrder == ByteOrder.BIG_ENDIAN) buffer = new BigEndianChannelBuffer(array, start, end)
    else buffer = null

    buffer.writeIndex = end

    buffer
  }

  def apply(byteBuffer: ByteBuffer): ChannelBuffer = {
    if (byteBuffer.order() == ByteOrder.BIG_ENDIAN) {

    } else {

    }
    null
  }

  /**
   * 工具方法，使util包不需依赖于buffer包
   */
  def find(buffer: ChannelBuffer, pattern: Array[Byte]): Int = {
    if (buffer.hasArray()) {
      return Util.find(buffer.bufferArray(), buffer.readIndex, buffer.writeIndex, pattern)
    }

    -1
  }

  def main(args: Array[String]) {
    val buffer = ChannelBuffer(128)
  }

}
