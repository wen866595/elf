package bruce.io.elf.core

abstract class Handler {
  def onSessionCreated(session: NioSession)
  def onSessionOpened(session: NioSession)

  def onMessageReceived(session: NioSession, message: Any)
  def onMessageSent(session: NioSession, message: Any)

  def onSessionClosed(session: NioSession)
  def onExceptionCaught(session: NioSession, cause: Throwable)
}