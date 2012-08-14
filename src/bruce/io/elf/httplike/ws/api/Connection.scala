package bruce.io.elf.httplike.ws.api

import bruce.io.elf.httplike.Request
import java.io.InputStream

/**
 * 对WebSocket 连接的抽象
 */
trait Connection {

  /**
   * 进行打开握手
   */
  def handshake(request: Request)

  /**
   * 发送文本数据
   */
  def sentText(text: String)

  /**
   * 关闭连接
   */
  def close() { close(-1, null) }

  def close(statusCode: Int, reason: String)
}