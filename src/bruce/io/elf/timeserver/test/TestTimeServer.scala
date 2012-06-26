package bruce.io.elf.timeserver.test

import bruce.io.elf.core.DefaultEncodeDecode
import bruce.io.elf.core.ServerControl
import bruce.io.elf.timeserver.TimeServerHandler

object TestTimeServer {
  def main(args: Array[String]) {
    val codec = new DefaultEncodeDecode() // ByteBuffer
    val handler = new TimeServerHandler()
    val control = new ServerControl(handler, codec)

    control.start()
  }
}