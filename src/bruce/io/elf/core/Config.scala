package bruce.io.elf.core

import java.net.InetSocketAddress
import java.net.SocketAddress

class Config {
  val reactorSize = 2
  val reactorWrokerSize = reactorSize * 2

  val acceptorThreadName = "Acceptor-0"
  val acceptorHostName = "localhost"
  val acceptorListenPort = 8080
  lazy val acceptorListenAddress: SocketAddress = new InetSocketAddress(acceptorHostName, acceptorListenPort)
  val acceptorSelectInternalTime = 1000
  val acceptorBacklog = 100

  val reactorSelectInternalTime = 1000

  val sessionBufferSize = 8 * 1024
}
