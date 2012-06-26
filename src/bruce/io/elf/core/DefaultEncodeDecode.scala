package bruce.io.elf.core

import bruce.io.elf.buffer.ChannelBuffer

class DefaultEncodeDecode extends EncodeDecode {

  def encode(session: NioSession, data: Any): ChannelBuffer = { data.asInstanceOf[ChannelBuffer] }

  def decode(session: NioSession, dataBuffer: ChannelBuffer): Any = { dataBuffer }

}