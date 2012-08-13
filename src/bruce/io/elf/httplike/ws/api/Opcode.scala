package bruce.io.elf.httplike.ws.api

object Opcode {
  val Continue: Int = 0x00
  val Text: Int = 0x01
  val Binary: Int = 0x02
  val Close: Int = 0x08
  val Ping: Int = 0x09
  val Pong: Int = 0x0A
}