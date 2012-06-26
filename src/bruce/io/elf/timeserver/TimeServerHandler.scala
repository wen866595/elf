package bruce.io.elf.timeserver

import bruce.io.elf.core.Handler
import bruce.io.elf.core.NioSession
import bruce.io.elf.buffer.ChannelBuffer

class TimeServerHandler extends Handler {

  def onSessionCreated(session: NioSession) {
    println("onSessionCreated .")
    val time = System.currentTimeMillis().toString()
    
    session.write(ChannelBuffer(time.getBytes()))
    session.close(false)
  }

  def onSessionOpened(session: NioSession) { onSessionCreated(session) }

  def onMessageReceived(session: NioSession, message: Any) {}

  def onMessageSent(session: NioSession, message: Any) { session.close(true) }

  def onSessionClosed(session: NioSession) { println("client closed ."); session.close(false) }

  def onExceptionCaught(session: NioSession, cause: Throwable) { session.close(true) }

}