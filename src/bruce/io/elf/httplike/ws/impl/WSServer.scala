package bruce.io.elf.httplike.ws.impl

import bruce.io.elf.httplike.ws.api.WSServlet
import bruce.io.elf.core.ServerControl

class WSServer(wsservlet: WSServlet) {
  val codec = new WSEncodeDecode()
  val handler = new WSHandler(wsservlet)
  val control = new ServerControl(handler, codec)

  def start() {
    control.start()
  }
}
