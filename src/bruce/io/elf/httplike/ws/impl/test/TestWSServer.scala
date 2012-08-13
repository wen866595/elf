package bruce.io.elf.httplike.ws.impl.test

import bruce.io.elf.httplike.ws.impl.WSEncodeDecode
import bruce.io.elf.core.ServerControl
import bruce.io.elf.httplike.ws.impl.WSHandler

object TestWSServer {

  def main(args: Array[String]) {
    val codec = new WSEncodeDecode()
    val handler = new WSHandler()
    val control = new ServerControl(handler, codec)

    control.start()
  }
}