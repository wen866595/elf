package bruce.io.elf.httplike.ws.impl

object DecodeState extends Enumeration {
  type DecodeState = Value
  val WaitNewMessage, WaitNewFrame, WaitFrameHeadEnd, WaitFrameDataEnd = Value
}