package bruce.io.elf.core

import bruce.io.elf.buffer.ChannelBuffer

trait EncodeDecode {
  def decode(session: NioSession, buffer: ChannelBuffer): Any

  def encode(session: NioSession, message: Any): ChannelBuffer
}