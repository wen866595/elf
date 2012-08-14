package bruce.io.elf.httplike.ws.impl

import java.io.InputStream
import bruce.io.elf.core.NioSession
import bruce.io.elf.httplike.ws.api.Frame
import bruce.io.elf.httplike.ws.api.WSServlet
import bruce.io.elf.httplike.Request
import bruce.io.elf.buffer.ChannelBuffer
import bruce.io.elf.httplike.Response

class DefaultServerConnection(session: NioSession, servlet: WSServlet) extends AbstractConnection(session, servlet) {
  var currentFrame: FrameBuilder = null
  var currentFrameReadedBytes = 0

  def handshake(request: Request) {
    val validRes = ValidUtil.validRequestHeaders(request.getHeaders())

    val res =
      if (validRes == null) {
        val res = new Response("HTTP/1.1", 101, "Switching Protocols")
        res.addHeader("Upgrade", "websocket")
        res.addHeader("Connection", "Upgrade")
        val acceptKey = ValidUtil.generateAccept(request.getHeader("Sec-WebSocket-Key"))
        res.addHeader("Sec-WebSocket-Accept", acceptKey)
        res
      } else new Response("HTTP/1.1", 401, validRes)

    session.write(res)

    /*
    println("ready to send ping .")
    val ping = Frame.ping("ping from server .".getBytes())
    session.write(ping)*/
  }

  def close(statusCode: Int, reason: String) {
    if (!isCloseFrameSent) {
      isCloseFrameSent = true
      val res = Frame.close(statusCode, reason)
      session.write(res)
    }
  }

  var request: Request = null
  def getRequest: Request = request

}