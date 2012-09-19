package bruce.io.elf.core

import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

class AcceptorReactor(control: Control) extends Thread(control.config.acceptorThreadName) {
  private var acceptSelector: Selector = null
  private var serverSocketChannel: ServerSocketChannel = null

  private[this] var isStop = false
  var isStarted = false

  override def run() {
    registerServerChannel()
    isStarted = true
    doSelect()
  }

  private def registerServerChannel() {
    acceptSelector = Selector.open()
    serverSocketChannel = ServerSocketChannel.open()
    serverSocketChannel.configureBlocking(false)
    serverSocketChannel.bind(control.config.acceptorListenAddress, control.config.acceptorBacklog)

    serverSocketChannel.register(acceptSelector, SelectionKey.OP_ACCEPT)
  }

  private def doSelect() {
    var next = control.config.acceptorSelectInternalTime
    while (!isStop) {
      val selectedSize = acceptSelector.select(next)
      val selectedSet = acceptSelector.selectedKeys()
      if (selectedSet.size() > 0) {
        dispatchSelectedSet(selectedSet)
      }
    }
  }

  private def dispatchSelectedSet(set: java.util.Set[SelectionKey]) {
    val iter = set.iterator()
    while (iter.hasNext()) {
      val key = iter.next()
      iter.remove()
      if (key.isValid())
        doAccept()
    }
  }

  private def doAccept() {
    do {
      val newSocketChannel = serverSocketChannel.accept()
      if (newSocketChannel == null) return

      newSocketChannel.configureBlocking(false)
      control.register(newSocketChannel)
    } while (true)
  }

  def shutDown() { isStop = true }
}
