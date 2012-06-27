package bruce.io.elf.buffer

import java.nio.ByteOrder
import java.nio.ByteBuffer

class BigEndianChannelBuffer(array: Array[Byte]) extends HeapChannelBuffer(array) {

  def this(capacity: Int) {
    this(new Array[Byte](capacity))
  }

  def getChar(): Char = {
    val char = ((array(readIndex) & 0xff << 8) | (array(readIndex + 1) & 0xff)).asInstanceOf[Char]
    readIndex = readIndex + 2
    char
  }

  def putChar(ch: Char): ChannelBuffer = {
    array(writeIndex) = (ch >>> 8).asInstanceOf[Byte]
    array(writeIndex + 1) = (ch).asInstanceOf[Byte]
    writeIndex = writeIndex + 2
    this
  }

  def getShort(): Short = {
    val short = ((array(readIndex) & 0xff << 8) | (array(readIndex + 1) & 0xff)).asInstanceOf[Short]
    readIndex = readIndex + 2
    short
  }

  def putShort(sh: Short): ChannelBuffer = {
    array(writeIndex) = (sh >>> 8).asInstanceOf[Byte]
    array(writeIndex + 1) = (sh).asInstanceOf[Byte]
    writeIndex = writeIndex + 2
    this
  }

  def getInt(): Int = {
    val int = array(readIndex) & 0xff << 24 |
      array(readIndex + 1) & 0xff << 16 |
      array(readIndex + 2) & 0xff << 8 |
      array(readIndex + 3) & 0xff

    readIndex = readIndex + 4
    int
  }

  def putInt(int: Int): ChannelBuffer = {
    array(writeIndex) = (int >>> 24).asInstanceOf[Byte]
    array(writeIndex + 1) = (int >>> 16).asInstanceOf[Byte]
    array(writeIndex + 2) = (int >>> 8).asInstanceOf[Byte]
    array(writeIndex + 3) = (int).asInstanceOf[Byte]

    writeIndex = writeIndex + 4
    this
  }

  def getLong(): Long = {
    val long = array(readIndex) & 0xff << 56 |
      array(readIndex + 1) & 0xff << 48 |
      array(readIndex + 2) & 0xff << 40 |
      array(readIndex + 3) & 0xff << 32 |
      array(readIndex + 4) & 0xff << 24 |
      array(readIndex + 5) & 0xff << 16 |
      array(readIndex + 6) & 0xff << 8 |
      array(readIndex + 7) & 0xff

    readIndex = readIndex + 8
    long
  }

  def putLong(long: Long): ChannelBuffer = {
    array(writeIndex) = (long >>> 56).asInstanceOf[Byte]
    array(writeIndex + 1) = (long >>> 48).asInstanceOf[Byte]
    array(writeIndex + 2) = (long >>> 40).asInstanceOf[Byte]
    array(writeIndex + 3) = (long >>> 32).asInstanceOf[Byte]
    array(writeIndex + 4) = (long >>> 24).asInstanceOf[Byte]
    array(writeIndex + 5) = (long >>> 16).asInstanceOf[Byte]
    array(writeIndex + 6) = (long >>> 8).asInstanceOf[Byte]
    array(writeIndex + 7) = (long).asInstanceOf[Byte]

    writeIndex = writeIndex + 8
    this
  }

  override def byteOrder = ByteOrder.BIG_ENDIAN

}