package bruce.io.elf.httplike.ws.impl

import bruce.io.elf.httplike.ws.api.WSServlet
import bruce.io.elf.core.ServerControl
import bruce.io.elf.core.Config
import java.net.InetSocketAddress
import java.net.SocketAddress

class WSServer(val port: Int, wsservlet: WSServlet) {
  val codec = new WSEncodeDecode()
  val handler = new WSHandler(wsservlet)
  val control = new ServerControl(new Config() {
    override val acceptorListenPort: Int = port
  }, handler, codec)

  def start() {
    control.start()
  }
}
