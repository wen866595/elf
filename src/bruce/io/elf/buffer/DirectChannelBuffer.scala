package bruce.io.elf.buffer

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.BufferUnderflowException

class DirectChannelBuffer(byteBuffer: ByteBuffer) extends ChannelBuffer {
  override val capacity = byteBuffer.capacity()

  def getChar(): Char = {
    val char = byteBuffer.getChar(readIndex)
    readIndex = readIndex + 2
    char
  }

  def putChar(ch: Char): ChannelBuffer = {
    byteBuffer.putChar(writeIndex, ch)
    writeIndex = writeIndex + 2
    this
  }

  def getShort(): Short = {
    val short = byteBuffer.getShort(readIndex)
    readIndex = readIndex + 2
    short
  }

  def putShort(sh: Short): ChannelBuffer = {
    byteBuffer.putShort(writeIndex, sh)
    writeIndex = writeIndex + 2
    this
  }

  def getInt(): Int = {
    val int = byteBuffer.getInt(readIndex)
    readIndex = readIndex + 4
    int
  }

  def putInt(int: Int): ChannelBuffer = {
    byteBuffer.putInt(writeIndex, int)
    writeIndex = writeIndex + 4
    this
  }

  def getLong(): Long = {
    val long = byteBuffer.getLong(readIndex)
    readIndex = readIndex + 8
    long
  }

  def putLong(long: Long): ChannelBuffer = {
    byteBuffer.putLong(writeIndex, long)
    writeIndex = writeIndex + 8
    this
  }

  def get(): Byte = {
    val b = get(readIndex)
    readIndex = readIndex + 1
    b
  }

  def put(byte: Byte): ChannelBuffer = {
    byteBuffer.put(writeIndex, byte)
    writeIndex = writeIndex + 1
    this
  }

  def get(i: Int): Byte = { byteBuffer.get(i) }

  def get(dst: Array[Byte]): ChannelBuffer = get(dst, 0, dst.length)

  def get(dst: Array[Byte], offset: Int, length: Int): ChannelBuffer = {
    if (readable() < length) throw new BufferUnderflowException()

    byteBuffer.position(readIndex)
    byteBuffer.get(dst, offset, length)
    readIndex = readIndex + length
    this
  }

  def put(src: Array[Byte]): ChannelBuffer = { put(src, 0, src.length) }

  def put(src: Array[Byte], offset: Int, length: Int): ChannelBuffer = {
    if (writable() < length) throw new BufferUnderflowException()

    byteBuffer.position(writeIndex)
    byteBuffer.put(src, offset, length)
    writeIndex = writeIndex + length

    this
  }

  def readableByteBuffer(): ByteBuffer = {
    val newBuffer = byteBuffer.slice()
    newBuffer.position(readIndex).limit(writeIndex)
    newBuffer
  }

  def writeableByteBuffer(): ByteBuffer = {
    val newBuffer = byteBuffer.slice()
    newBuffer.position(writeIndex).limit(capacity)
    newBuffer
  }

  def setReadIndex(newReadIndex: Int): ChannelBuffer = {
    readIndex = newReadIndex
    this
  }

  def setWriteIndex(newWriteIndex: Int): ChannelBuffer = {
    writeIndex = newWriteIndex
    this
  }

  def clear(): ChannelBuffer = {
    byteBuffer.clear()
    this
  }

  /**
   * 压缩已读的空间
   */
  def discardReaded(): ChannelBuffer = {
    val len = readable()
    if (len > 0) {
      byteBuffer.position(readIndex)
      byteBuffer.compact()
      readIndex = 0
      writeIndex = len
    }
    this
  }

  def bufferArray() = null

  def byteOrder(): ByteOrder = byteBuffer.order()

  def isDirect(): Boolean = true

  def hasArray(): Boolean = byteBuffer.hasArray()

}