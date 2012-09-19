package bruce.io.elf.core

class ServerControl(config: Config, override val handler: Handler, override val codec: EncodeDecode) extends Control(config, handler, codec) {
  val acceptor = new AcceptorReactor(this)

  def start() {
    super.initReactors()
    acceptor.start()
    waitAcceptorStarted()
  }

  private def waitAcceptorStarted() { while (!acceptor.isStarted) true }
}