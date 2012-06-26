package bruce.io.elf.buffer

import java.nio.ByteOrder
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer

abstract class HeapChannelBuffer(array: Array[Byte], start: Int, end: Int) extends ChannelBuffer {

  override val capacity = end - start

  def writable(): Int = end - writeIndex

  def get(): Byte = {
    val b = get(readIndex)
    readIndex = readIndex + 1
    b
  }

  def put(byte: Byte): ChannelBuffer = {
    array(writeIndex) = byte
    writeIndex = writeIndex + 1
    this
  }

  def get(i: Int): Byte = array(i)

  def get(dst: Array[Byte]): ChannelBuffer = get(dst, 0, dst.length)

  def get(dst: Array[Byte], offset: Int, length: Int): ChannelBuffer = {
    if (readable() < length) throw new BufferUnderflowException()

    System.arraycopy(array, readIndex, dst, offset, length)
    readIndex = readIndex + length
    this
  }

  def put(src: Array[Byte]): ChannelBuffer = { put(src, 0, src.length) }

  def put(src: Array[Byte], offset: Int, length: Int): ChannelBuffer = {
    System.arraycopy(src, offset, array, writeIndex, length)
    writeIndex = writeIndex + length
    this
  }

  def readableByteBuffer(): ByteBuffer = ByteBuffer.wrap(array, readIndex, readable())

  def writeableByteBuffer(): ByteBuffer = ByteBuffer.wrap(array, writeIndex, writable())

  def setReadIndex(newReadIndex: Int): ChannelBuffer = {
    readIndex = newReadIndex
    this
  }

  def setWriteIndex(newWriteIndex: Int): ChannelBuffer = {
    writeIndex = newWriteIndex
    this
  }

  def clear(): ChannelBuffer = {
    readIndex = start
    writeIndex = start
    this
  }

  /**
   * 压缩已读的空间
   */
  def discardReaded(): ChannelBuffer = {
    val len = readable()
    System.arraycopy(array, readIndex, array, 0, len)
    readIndex = 0
    writeIndex = len
    this
  }

  def bufferArray() = array

  def byteOrder(): ByteOrder

  def isDirect(): Boolean = false

  def hasArray(): Boolean = true

}