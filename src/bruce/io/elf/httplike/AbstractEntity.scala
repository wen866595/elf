package bruce.io.elf.httplike
import bruce.io.elf.buffer.ChannelBuffer

trait AbstractEntity {
  import scala.collection.mutable
  private[httplike] var headers: mutable.Map[String, String] = new mutable.HashMap[String, String]

  def addHeader(name: String, value: String) {
    headers += name -> value
  }

  def addHeader(header: String) {
    val splits = header.split(": ", 2)
    if (splits.length == 2)
      addHeader(splits(0), splits(1))
  }

  def addHeaders(headers: String) {
    for (header <- headers.split("\r\n")) {
      addHeader(header.trim())
    }
  }

  def getHeader(name: String): String = headers.get(name).get

  def getHeaders(): mutable.Map[String, String] = headers

  private[httplike] def header2buffer(buffer: ChannelBuffer) {
    val sb = new StringBuilder(256)
    for ((name, value) <- headers) {
      sb.append(name).append(": ").append(value).append("\r\n")
    }
    buffer.put(sb.toString().getBytes())
  }

  def toChannelBuffer: ChannelBuffer
}