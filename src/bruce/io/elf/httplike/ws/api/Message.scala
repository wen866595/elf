package bruce.io.elf.httplike.ws.api

import java.io.InputStream
import bruce.io.elf.httplike.ws.api.MessageType._

/** WebSocket消息
 */
abstract class Message(firstFrame: Frame) {
  /** 是否是控制消息
   */
  def isControl: Boolean = firstFrame.isControl

  /** 消息类型
   */
  def msgType: MessageType
  
  /**
   * 返回最原始的数据
   */
  def data: Array[Byte]

  /** 返回流，用于二进制数据，只有是非控制消息时才有效
   */
  def getDataStream(): InputStream = null

  /** 返回字符串，用于文本数据，只有是非控制消息时才有效
   */
  def getDataString(): String
}
