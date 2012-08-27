package bruce.io.elf.core

class ServerControl(override val handler: Handler, override val codec: EncodeDecode) extends Control(handler, codec) {
  val acceptor = new AcceptorReactor(this)

  def start() {
    super.initReactors()
    acceptor.start()
    waitAcceptorStarted()
  }

  private def waitAcceptorStarted() { while (!acceptor.isStarted) true }
}