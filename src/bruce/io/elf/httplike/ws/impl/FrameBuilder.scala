package bruce.io.elf.httplike.ws.impl
import bruce.io.elf.httplike.ws.api.Frame

class FrameBuilder(first: Byte, second: Byte) {
  val finB = (first >>> 7 & 0x01)
  val opcodeB = first & 0x0f
  val maskB = second >>> 7 & 0x01
  val payload = (second & 0x7f).toLong

  val payloadBytes =
    if (payload == 126) 2
    else if (payload == 127) 8
    else 0

  val remainHeadLen = payloadBytes + (if (isMasked) 4 else 0)

  val rsv1: Int = 0 // 1b
  val rsv2: Int = 0 // 1b
  val rsv3: Int = 0 // 1b

  var dataLenB: Long = payload // 7b/16b/64b		// 最大为64位无符号整数，限于JVM最大有符号32位，取long型。

  var maskKeyB: Array[Byte] = null // 4B = 32b

  var dataB: Array[Byte] = null

  def isData: Boolean = opcodeB < 0x08
  def isFirst: Boolean = finB == 0 && opcodeB != 0
  def isLast: Boolean = finB == 0 && opcodeB == 0
  def isMasked: Boolean = maskB == 1
  def isControl: Boolean = opcodeB > 0x07
  def isWhole: Boolean = finB == 1 && opcodeB != 0 // 未分帧的

  def build(): Frame = new Frame {
    val fin: Boolean = finB == 1
    val opcode: Int = opcodeB
    val mask: Boolean = maskB == 1
    val payloadLen: Int = payload.toInt
    val maskKey: Array[Byte] = maskKeyB
    val dataLen: Int = dataLenB.toInt
    val data: Array[Byte] = dataB
  }

}