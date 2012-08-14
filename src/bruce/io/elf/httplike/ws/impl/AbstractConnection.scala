package bruce.io.elf.httplike.ws.impl

import bruce.io.elf.core.NioSession
import bruce.io.elf.httplike.ws.api.Connection
import bruce.io.elf.httplike.ws.api.ConnectionState
import bruce.io.elf.httplike.ws.api.Frame
import bruce.io.elf.httplike.ws.api.Message
import bruce.io.elf.httplike.ws.api.MessageType
import bruce.io.elf.httplike.ws.api.WSServlet
import bruce.io.elf.httplike.ws.api.Opcode
import bruce.io.elf.httplike.ws.api.Constants
import ConnectionState._
import DecodeState._

abstract class AbstractConnection(val session: NioSession, val servlet: WSServlet) extends Connection {
  var state: ConnectionState = CONNECTING
  var decodeState: DecodeState = WaitNewFrame
  var isCloseFrameSent: Boolean = false // 是否发送关闭帧
  var isRemoteCloseFrameRcv = false // 是否接收到对端的关闭帧

  private var currentMessage: MessageImpl = null

  /**
   * 接收到frame时调用此方法，如果通道在接收到此帧后可以生成一个消息，则返回消息。
   */
  def onFrame(frame: Frame) {
    if (!validFrame(frame)) return

    if (frame.isControl) handleContralFrame(frame)
    else handleDataFrame(frame)
  }

  def handleDataFrame(frame: Frame) {
    if (frame.isWhole) {
      val msg = new MessageImpl(frame)
      servlet.onMessage(this, msg)

    } else if (frame.isFirst) currentMessage = new MessageImpl(frame)

    else {
      currentMessage.addFrame(frame)
      if (frame.isLast) {
        val msg = currentMessage
        currentMessage = null
        servlet.onMessage(this, msg)
      }
    }
  }

  def handleContralFrame(frame: Frame) {
    frame.opcode match {
      case Opcode.Ping =>
        val pong = Frame.pong(frame.data)
        session.write(pong)

      case Opcode.Pong =>

      case Opcode.Close => {
        isRemoteCloseFrameRcv = true

        if (isCloseFrameSent) {
          session.close(true)

        } else {
          val data = frame.data

          val (statusCode, reason) = if (frame.dataLen > 0) {
            // 关闭帧：如果有主体，主体的前2个字节必须是2字节的无符号整数（按网络字节序）
            if (frame.dataLen < 2) servlet.onError(this, new IllegalStateException("close frame has a body, but not contains statusCode ."))

            val statusCode = data(0) & 0xFF << 8 | data(1) & 0xFF
            val reason = new String(data.slice(2, data.length), Constants.DEFAULT_CHARSET)
            (statusCode, reason)
          } else (-1, null)

          close(statusCode, reason)

          servlet.onClose(this, statusCode, reason)
          
          session.close(false)
        }
      }

      case _ =>
    }
  }

  /**
   * 验证帧是否合法
   */
  def validFrame(frame: Frame): Boolean = {
    if (frame.isWhole) {
      if (frame.isData && currentMessage != null) { // 错误状态，消息出现交叉
        servlet.onError(this, new IllegalStateException("two message mixed !"))
        false
      } else true
    } else if (frame.isControl) {
      // 控制消息被分帧了
      servlet.onError(this, new IllegalStateException("control message must not be framed !"))
      false
    } else true
  }

  /**
   * 发送文本数据
   */
  def sentText(text: String) {
    if (text == null) throw new NullPointerException
    sendText(text, false, null)
  }

  /**
   * 发送文本数据，按指定的标记健标记
   */
  def sendText(text: String, maskKey: Array[Byte]) {
    if (text == null) throw new NullPointerException
    if (maskKey == null || maskKey.length == 4) throw new IllegalAccessException("mask key array must be byte array, and array length is 4 !")
    sendText(text, true, maskKey)
  }

  private def sendText(text: String, isMask: Boolean, maskKey: Array[Byte]) {
    val frame = Frame.text(text, maskKey)
    session.write(frame)
  }

}