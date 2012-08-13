package bruce.io.elf.httplike.ws.api

import bruce.io.elf.httplike.Request
import bruce.io.elf.httplike.Response
import bruce.io.elf.httplike.ws.impl.ValidUtil

/**
 * 供用户使用的接口
 */
class WSServlet {

  /**
   * 连接打开后回调
   */
  def onOpen(connection: Connection, request: Request) { println("websocket connection is opened .") }

  import MessageType._
  /**
   * 接收到消息时回调
   */
  def onMessage(conncection: Connection, message: Message) {
    message.msgType match {
      case Binary =>
      case Text => {
        println("get message :" + message.getDataString())
        conncection.sentText("response:" + message.getDataString())
      }
      case _ =>
    }

  }

  /**
   * 接收到关闭消息时回调
   */
  def onClose(connection: Connection, closeCode: Int, closeReason: String) {
    println("close websocket connection .")
  }

  /**
   * 出错时回调
   */
  def onError(connection: Connection, exception: Throwable) {}
}