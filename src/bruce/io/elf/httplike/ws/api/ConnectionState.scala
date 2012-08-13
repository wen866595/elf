package bruce.io.elf.httplike.ws.api

object ConnectionState extends Enumeration {
  type ConnectionState = Value
  val CONNECTING, OPEN, CLOSING, CLOSED = Value
}