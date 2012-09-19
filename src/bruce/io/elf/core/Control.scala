package bruce.io.elf.core

import java.nio.channels.SocketChannel
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.Executors

abstract class Control(val config: Config, val handler: Handler, val codec: EncodeDecode) {
  private val reactorSize = config.reactorSize
  private val workers = Executors.newFixedThreadPool(config.reactorWrokerSize)
  private val reactorRunner = Executors.newFixedThreadPool(reactorSize)
  private val reactors = new Array[ReadWriteReactor](reactorSize)

  private val reactorIndex = new AtomicInteger(0)

  def initReactors() {
    for (i <- 0 until reactorSize) {
      reactors(i) = new ReadWriteReactor(i, workers, this)
      reactorRunner.execute(reactors(i))
    }
  }

  def shutDown(immediatly: Boolean) {
    if (immediatly) {
      reactors.foreach(_.shutDown(true))
      reactorRunner.shutdownNow()
      workers.shutdownNow()
    } else {
      reactors.foreach(_.shutDown(false))
      reactorRunner.shutdown()
      workers.shutdown()
    }
  }

  def register(selectable: SocketChannel) {
    val i = getNextReactorIndex()
    val reactor = reactors(i)
    reactor.reigster(selectable)
  }

  private def getNextReactorIndex(): Int = {
    var i = reactorIndex.addAndGet(1)
    if (i >= reactorSize) {

      i = i % reactorSize
      reactorIndex.set(i)
    }
    i
  }
}