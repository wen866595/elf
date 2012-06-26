package bruce.io.elf.core

import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.util.concurrent.Executors
import java.nio.channels.SelectionKey
import java.util.concurrent.ExecutorService
import java.nio.channels.SelectableChannel

class ReadWriteReactor(id: Int, workers: ExecutorService, control: Control) extends Runnable {

  private val SelectInternalTime = 1000
  private val registerQueue = new java.util.concurrent.ConcurrentLinkedQueue[SocketChannel]()
  private val managedSession = new java.util.concurrent.ConcurrentHashMap[NioSession, SelectionKey]

  private var selector: Selector = null
  private var isStop = false

  override def run() {
    setup()
    selectLoop()
  }

  private def setup() { selector = Selector.open() }

  private def selectLoop() {
    var nextTime = 1L
    this.synchronized {

      while (!isStop) {
        val start = System.currentTimeMillis()

        doRegister()
        doSelect(nextTime) // SelectedKeys不是线程安全的

        val end = System.currentTimeMillis()
        val used = end - start
        nextTime = if (used > SelectInternalTime) 0 else SelectInternalTime - used + 1
      }

      selector.close()
    }
  }

  private def doSelect(waitTime: Long): Int = {
    val size = selector.select(waitTime)
    if (size > 0) {
      val set = selector.selectedKeys()
      dispatchReadWritEvents(set)
    }
    size
  }

  private def dispatchReadWritEvents(events: java.util.Set[SelectionKey]) {
    val iter = events.iterator()
    while (iter.hasNext()) {
      val key = iter.next()
      iter.remove()
      dispatchEvent(key)
    }
  }

  private def dispatchEvent(key: SelectionKey) {
    if (!key.isValid()) return

    val session = key.attachment().asInstanceOf[NioSession]
    if (session == null) return

    if (key.isValid() && key.isReadable()) {
      suspendRead(session)
      workers.execute(new Runnable() {
        override def run() { if (key.isValid() && key.isReadable()) { session.onReadable() } }
      })
    }

    if (key.isValid() && key.isWritable()) {
      suspendWrite(session)
      workers.execute(new Runnable() {
        override def run() { if (key.isValid() && key.isWritable()) { session.onWriteable() } }
      })
    }
  }

  def unregister(session: NioSession) {
    val key = managedSession.get(session)
    if (key != null) {
      managedSession.remove(session)
      key.cancel()
      key.attach(null)
      wakeup()
    }
  }

  def reigster(channel: SocketChannel) {
    registerQueue.offer(channel)
    wakeup()
  }

  // SocketChannel.register 可能会在Selector上阻塞，所以要集中注册，避免锁
  private def doRegister() {
    var sc: SocketChannel = null
    do {
      sc = registerQueue.poll()
      if (sc != null && !isStop) {
        val key = sc.register(selector, 0)
        val newSession = new NioSession(control, this, sc)
        key.attach(newSession)
        managedSession.put(newSession, key)

        workers.execute(new Runnable() {
          override def run() { control.handler.onSessionCreated(newSession) }
        })
      }

    } while (sc != null)
  }

  def focusRead(session: NioSession) { focusOps(session, SelectionKey.OP_READ) }
  def focusWrite(session: NioSession) { focusOps(session, SelectionKey.OP_WRITE) }

  private def focusOps(session: NioSession, ops: Int) {
    val key = managedSession.get(session)
    if (key != null && key.isValid()) {
      key.interestOps(ops)
      wakeup()
    }
  }

  def enableRead(session: NioSession) { enableOps(session, SelectionKey.OP_READ) }
  def enableWrite(session: NioSession) { enableOps(session, SelectionKey.OP_WRITE) }

  def enableOps(session: NioSession, ops: Int) {
    val key = managedSession.get(session)
    if (key != null && key.isValid()) {
      key.interestOps(key.interestOps() + ops)
      wakeup()
    }
  }

  def suspendRead(session: NioSession) { suspendOps(session, SelectionKey.OP_READ) }
  def suspendWrite(session: NioSession) { suspendOps(session, SelectionKey.OP_WRITE) }

  private def suspendOps(session: NioSession, ops: Int) {
    val key = managedSession.get(session)
    if (key != null && key.isValid()) {
      key.interestOps(key.interestOps() ^ ops ^ 0)
      wakeup()
    }
  }

  private def wakeup() { selector.wakeup() }

  def shutDown(immediatly: Boolean) {
    isStop = true
    wakeup()
  }

}