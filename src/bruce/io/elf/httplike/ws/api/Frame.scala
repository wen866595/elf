package bruce.io.elf.httplike.ws.api

import bruce.io.elf.buffer.ChannelBuffer
import java.nio.ByteOrder

trait Frame {
  /**
   * fin == 1:
   */
  val fin: Boolean

  val rsv1: Boolean = false
  val rsv2: Boolean = false
  val rsv3: Boolean = false

  val opcode: Int

  /**
   * mask == 1
   */
  val mask: Boolean

  /**
   * 7b payloadLen
   */
  val payloadLen: Int

  /**
   * 标记键，如果有，必须是长度为4的字节数组
   */
  val maskKey: Array[Byte]

  /**
   * 有效数据的长度
   */
  val dataLen: Int

  /**
   * 有效数据
   */
  val data: Array[Byte]

  /**
   * 判断帧是否是完整的消息。
   * return true: 完整的消息, false: 消息的一部分
   */
  def isWhole: Boolean = fin && opcode != 0

  /**
   * 是否是所属消息的第一帧。
   */
  def isFirst: Boolean = !fin && opcode != 0

  /**
   * 是否是所属消息的最后帧。
   */
  def isLast: Boolean = !fin && opcode == 0

  /**
   * 是否标记了。
   */
  def isMasked: Boolean = mask

  def isData: Boolean = opcode < 0x08
  def isControl: Boolean = opcode > 0x07

  require(opcode >= 0x00 && opcode <= 0x02 || opcode >= 0x08 && opcode <= 0x0A, "currently, opcode must be [0x00-0x02] or [0x08-0x0A] .")
  require(payloadLen >= 0 && payloadLen <= 127, "payloadLen must be [0 - 127]")
  require(if (mask) maskKey != null && maskKey.length == 4 else true, "maskKey array's length must be 4 .")
}

object Frame {
  val emptyByteArray = new Array[Byte](0)

  def text(string: String): Frame = { text(string, null) }

  def text(string: String, maskKeyArr: Array[Byte]): Frame = {
    if (string == null || string.trim().length() == 0) throw new IllegalArgumentException("text data must not be blank !")

    apply(Opcode.Text, string.getBytes("UTF-8"), maskKeyArr)
  }

  def binary(dataArr: Array[Byte]): Frame = { binary(dataArr, null) }

  def binary(dataArr: Array[Byte], maskKeyArr: Array[Byte]): Frame = {
    apply(Opcode.Binary, dataArr, maskKeyArr)
  }

  def ping(): Frame = { ping(null) }

  def ping(data: Array[Byte]): Frame = { ping(data, null) }

  def ping(data: Array[Byte], maskKey: Array[Byte]): Frame = { apply(Opcode.Ping, data, maskKey) }

  def pong(): Frame = { pong(null) }

  def pong(data: Array[Byte]): Frame = { pong(data, null) }

  def pong(data: Array[Byte], maskKey: Array[Byte]): Frame = { apply(Opcode.Pong, data, maskKey) }

  def close(): Frame = { close(-1, null) }

  def close(code: Int, reason: String): Frame = { close(code, reason, null) }

  def close(code: Int, reason: String, maskKey: Array[Byte]): Frame = {
    var data: Array[Byte] = null
    if (reason != null) {
      data = new Array[Byte](2)
      data(0) = (code & 0xFF00 >> 8).toByte
      data(1) = (code & 0xFF).toByte

      val rb = reason.getBytes("UTF-8")
      data = data ++ rb

    } else data = emptyByteArray

    apply(Opcode.Close, data, maskKey)
  }

  private def apply(opcod: Int, dataArr: Array[Byte], maskKeyArr: Array[Byte]): Frame = {
    if (maskKeyArr != null && maskKeyArr.length != 4) throw new IllegalArgumentException("maskKeyArr's length must be 4 !")

    val isMask = maskKeyArr != null
    val payload = dataArr.length

    new Frame {
      val fin = true
      val opcode = opcod
      val mask = isMask
      val payloadLen = if (payload < 126) payload else if (payload <= Constants.MAX_UNSIGNED_16B_INT) 126 else 127
      val maskKey = if (isMask) maskKeyArr else emptyByteArray
      val dataLen = payload
      val data = if (dataArr == null) emptyByteArray else dataArr
    }
  }

  def toChannelBuffer(frame: Frame): ChannelBuffer = {
    val first =
      (if (frame.fin) 0x80 else 0x00) |
        (if (frame.rsv1) 0x40 else 0x00) |
        (if (frame.rsv2) 0x20 else 0x00) |
        (if (frame.rsv3) 0x10 else 0x00) |
        (frame.opcode & 0xff)

    val second = (if (frame.mask) 0x80 else 0x00) |
      (frame.payloadLen & 0xFF)

    var totalLen = 2
    if (frame.mask) totalLen += 4

    if (frame.payloadLen == 126) totalLen += 2
    else if (frame.payloadLen == 127) totalLen += 8

    if (frame.data != null) totalLen += frame.data.length

    val buffer = ChannelBuffer(totalLen, ByteOrder.BIG_ENDIAN)
    buffer.put(first.toByte).put(second.toByte)

    if (frame.payloadLen == 126) buffer.putShort(frame.dataLen.toShort)
    else if (frame.payloadLen == 127) buffer.putLong(frame.dataLen)

    if (frame.mask) buffer.put(frame.maskKey)

    if (frame.data != null) buffer.put(frame.data)

    buffer
  }
}
