package bruce.io.elf.buffer

import java.nio.ByteOrder

class LittleEndianHeapChannelBuffer(array: Array[Byte], start: Int, end: Int) extends HeapChannelBuffer(array, start, end) {
  
  def this(underHeapArray: Array[Byte]) {
    this(underHeapArray, 0, underHeapArray.length)
  }

  def this(capacity: Int) {
    this(new Array[Byte](capacity))
  }

  def getChar(): Char = {
    val char = (array(readIndex + 1) & 0xff << 8 | array(readIndex) & 0xff).asInstanceOf[Char]
    readIndex = readIndex + 2
    char
  }

  def putChar(ch: Char): ChannelBuffer = {
    array(writeIndex) = ch.asInstanceOf[Byte]
    array(writeIndex + 1) = (ch >>> 8 & 0xff).asInstanceOf[Byte]
    writeIndex = writeIndex + 2
    this
  }

  def getShort(): Short = {
    val short = (array(readIndex + 1) & 0xff << 8 | array(readIndex) & 0xff).asInstanceOf[Short]
    readIndex = readIndex + 2
    short
  }

  def putShort(sh: Short): ChannelBuffer = {
    array(writeIndex) = sh.asInstanceOf[Byte]
    array(writeIndex + 1) = (sh >>> 8 & 0xff).asInstanceOf[Byte]
    writeIndex = writeIndex + 2
    this
  }

  def getInt(): Int = {
    val int = (
      array(readIndex + 3) & 0xff << 24 |
      array(readIndex + 2) & 0xff << 16 |
      array(readIndex + 1) & 0xff << 8 |
      array(readIndex) & 0xff).asInstanceOf[Int]

    readIndex = readIndex + 4
    int
  }

  def putInt(int: Int): ChannelBuffer = {
    array(writeIndex) = (int).asInstanceOf[Byte]
    array(writeIndex + 1) = (int >>> 8 & 0xff).asInstanceOf[Byte]
    array(writeIndex + 2) = (int >>> 16 & 0xff).asInstanceOf[Byte]
    array(writeIndex + 3) = (int >>> 24 & 0xff).asInstanceOf[Byte]

    writeIndex = writeIndex + 4
    this
  }

  def getLong(): Long = {
    val long = (
      array(readIndex + 7) & 0xff << 56 |
      array(readIndex + 6) & 0xff << 48 |
      array(readIndex + 5) & 0xff << 40 |
      array(readIndex + 4) & 0xff << 32 |
      array(readIndex + 3) & 0xff << 24 |
      array(readIndex + 2) & 0xff << 16 |
      array(readIndex + 1) & 0xff << 8 |
      array(readIndex) & 0xff).asInstanceOf[Int]

    readIndex = readIndex + 4
    long
  }

  def putLong(long: Long): ChannelBuffer = {
    array(writeIndex) = (long).asInstanceOf[Byte]
    array(writeIndex + 1) = (long >>> 56 & 0xff).asInstanceOf[Byte]
    array(writeIndex + 2) = (long >>> 48 & 0xff).asInstanceOf[Byte]
    array(writeIndex + 3) = (long >>> 40 & 0xff).asInstanceOf[Byte]
    array(writeIndex + 4) = (long >>> 32 & 0xff).asInstanceOf[Byte]
    array(writeIndex + 5) = (long >>> 24 & 0xff).asInstanceOf[Byte]
    array(writeIndex + 6) = (long >>> 16 & 0xff).asInstanceOf[Byte]
    array(writeIndex + 7) = (long >>> 8 & 0xff).asInstanceOf[Byte]

    this
  }

  def byteOrder() = ByteOrder.LITTLE_ENDIAN
}