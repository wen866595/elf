package bruce.io.elf.httplike.ws.impl.test

import bruce.io.elf.httplike.ws.impl.WSServer
import bruce.io.elf.httplike.ws.api.WSServlet

object TestWSServer {

  def main(args: Array[String]) {
    val server = new WSServer(8080, new WSServlet())
    server.start()
  }
}