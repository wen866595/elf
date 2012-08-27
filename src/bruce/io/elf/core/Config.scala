package bruce.io.elf.core

object Config {
  def reactorSize = 2
  def reactorWrokerSize = reactorSize * 2

  def acceptorThreadName = "Acceptor-0"
    def acceptorHostName = "localhost"
  def acceptorListenPort = 8080
  def acceptorSelectInternalTime = 1000
  def acceptorBacklog = 100

  def reactorSelectInternalTime = 1000

  def sessionBufferSize = 8 * 1024
}