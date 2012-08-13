package bruce.io.elf.httplike
import bruce.io.elf.buffer.ChannelBuffer

class Response(httpVersion: String, code: Int, reason: String) extends AbstractEntity {
  def toChannelBuffer: ChannelBuffer = {
    val buffer = ChannelBuffer(1024)
    buffer.put((httpVersion + " " + code + " " + reason + "\r\n").getBytes())
    header2buffer(buffer)
    buffer.put("\r\n".getBytes())
    buffer
  }
}