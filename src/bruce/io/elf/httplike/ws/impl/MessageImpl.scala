package bruce.io.elf.httplike.ws.impl

import bruce.io.elf.httplike.ws.api.MessageType._
import bruce.io.elf.httplike.ws.api.Frame
import bruce.io.elf.httplike.ws.api.Message
import bruce.io.elf.buffer.ChannelBuffer

class MessageImpl(frame: Frame) extends Message(frame) {
  private val msgType0: MessageType = frame.opcode match {
    case 0x01                      => Text
    case 0x02                      => Binary
    case 0x08                      => Close
    case 0x09                      => Ping
    case 0x0A                      => Pong
    case x if x > 0x03 && x < 0x08 => ReservedData // 保留的数据帧
    case _                         => ReservedControl
  }

  import scala.collection.mutable
  private var frames = new mutable.ListBuffer[Frame]

  def msgType: MessageType = msgType0

  frames += frame // 添加第一帧
  /** 添加后续帧
   */
  def addFrame(frame: Frame) {
    frames += frame
    handleIfLastFrame(frame)
  }

  def data(): Array[Byte] = {
    val len = frames.foldLeft[Int](0)((a, f) => a + f.dataLen.toInt)
    val buff = ChannelBuffer(len)
    frames.foreach(f => buff.put(f.data))
    buff.bufferArray()
  }

  def getDataString(): String = {
    val buff = data()

    if (buff == null) null
    else new String(buff, "UTF-8")
  }

  handleIfLastFrame(frame) // 构造帧
  /** 如果是最后一帧，则进行处理
   */
  private def handleIfLastFrame(frame: Frame) {

  }

}