package bruce.io.elf.httplike.ws.impl

import bruce.io.elf.buffer.ChannelBuffer

object WSUtil {

  def mask(key: Array[Byte], data: Array[Byte]) {
    for (i <- 0 until data.length) {
      val j = i & 3 // 对4 取模
      data(i) = (data(i) ^ key(j)).toByte
    }
  }

  def getLine(buffer: ChannelBuffer): String = {
    var i = buffer.readIndex
    var find = false
    while (!find && i < buffer.writeIndex) {
      var ch = buffer.get(i)

      if (ch == '\n') find = true
      else i = i + 1
    }

    if (find) {
      var buff = new Array[Byte](i - buffer.readIndex) // 避免拷贝\r
      buffer.get(buff)
      buffer.readIndex = i + 1 // \r\n

      new String(buff, "UTF-8")

    } else null
  }
}