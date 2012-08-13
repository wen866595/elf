package bruce.io.elf.httplike.ws.impl

import bruce.io.elf.core.Handler
import bruce.io.elf.core.NioSession
import bruce.io.elf.httplike.Request
import bruce.io.elf.httplike.ws.api.Message
import bruce.io.elf.httplike.ws.api.WSServlet
import bruce.io.elf.httplike.ws.api.Frame
import java.text.SimpleDateFormat
import java.util.Date

class WSHandler extends Handler {
  val wsservlet = new WSServlet()

  def onSessionCreated(session: NioSession) {
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")

    session.setAttribute("wsconnection", new DefaultServerConnection(session, wsservlet))
    session.focusRead()

    println("session created ." + sdf.format(new Date()))
  }

  def onSessionOpened(session: NioSession) {
    println("session opened .")
  }

  def onMessageReceived(session: NioSession, message: Any) {
    val wsconnection = session.attribute("wsconnection").asInstanceOf[DefaultServerConnection]
    message match {
      case req: Request => { wsconnection.handshake(req) }
      case frame: Frame => { wsconnection.onFrame(frame) }
      case _ => println("message received, but do nonthing .") // do nonthing
    }
  }

  def onMessageSent(session: NioSession, message: Any) { println("message sent .") }

  def onSessionClosed(session: NioSession) { println("session closed .") }

  def onExceptionCaught(session: NioSession, cause: Throwable) {
    wsservlet.onError(session.attribute("wsconnection").asInstanceOf[DefaultServerConnection], cause)
  }

}