package bruce.io.elf.httplike
import bruce.io.elf.buffer.ChannelBuffer

class Request(requestLine: String) extends AbstractEntity {
  private val splist = requestLine.split(" ", 3)
  val method = splist(0)
  val uri = splist(1)
  val httpVersion = splist(2)
  
  def toChannelBuffer: ChannelBuffer = {
    val buffer = ChannelBuffer(1024)
    buffer.put(requestLine.getBytes())
    header2buffer(buffer)
    buffer
  }

}