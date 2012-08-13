package bruce.io.elf.httplike.ws.impl

import bruce.io.elf.core.EncodeDecode
import bruce.io.elf.buffer.ChannelBuffer
import bruce.io.elf.core.NioSession
import bruce.io.elf.httplike.Request
import DecodeState._
import bruce.io.elf.httplike.ws.api.Frame
import bruce.io.elf.util.Util
import java.util.Arrays
import bruce.io.elf.httplike.Response

class WSEncodeDecode extends EncodeDecode {

  def decode(session: NioSession, buffer: ChannelBuffer): Any = {
    val wsconnection = session.attribute("wsconnection").asInstanceOf[DefaultServerConnection]

    if (session.attribute("headerEnd") == null) {
      println("parse request header .")
      // do handshake
      if (session.attribute("request") == null) {
        var requestLine = WSUtil.getLine(buffer)
        println("requestLine :" + requestLine)
        if (requestLine != null) {
          val request = new Request(requestLine)
          session.setAttribute("request", request)
        }
      }

      if (session.attribute("request") != null) {
        var headerEnd = false
        var line: String = null
        val request = session.attribute("request").asInstanceOf[Request]
        do {
          line = WSUtil.getLine(buffer)
          if (line != null) {
            line = line.trim()
            if ("".equals(line)) {
              headerEnd = true
              session.setAttribute("headerEnd", headerEnd)
              session.setAttribute("decodeState", WaitNewMessage)
              println("parse request header end .")
            } else request.addHeader(line)
          }
        } while (line != null && !headerEnd)

        if (headerEnd) return request
      }
    }

    if (session.attribute("headerEnd") != null) {
      // decode message
      var continue = false
      do {
        continue = true
        wsconnection.decodeState match {
          case WaitNewFrame => {
            if (buffer.readable() >= 2) {
              val first = buffer.get()
              val second = buffer.get()
              val frame = new FrameBuilder(first, second)
              wsconnection.currentFrame = frame

              wsconnection.decodeState = WaitFrameHeadEnd
              println("WaitFrameHeadEnd . payloadBytes :" + frame.remainHeadLen + ", dataLen :" + frame.dataLenB)
            } else continue = false
          }

          case WaitFrameHeadEnd => {
            val frame = wsconnection.currentFrame

            if (buffer.readable() >= frame.remainHeadLen) {
              finishHead(buffer, frame)
              wsconnection.currentFrameReadedBytes = 0
              wsconnection.decodeState = WaitFrameDataEnd
              println("WaitFrameDataEnd . maskKey :" + Arrays.toString(frame.maskKeyB))

            } else { // not enough data
              continue = false
              wsconnection.decodeState = WaitFrameHeadEnd
            }
          }

          case WaitFrameDataEnd => {
            val frame = wsconnection.currentFrame
            var currentFrameReadedBytes = wsconnection.currentFrameReadedBytes
            var toReadLen = buffer.readable()
            if (toReadLen >= frame.dataLenB) {
              toReadLen = frame.dataLenB.toInt
            }
            buffer.get(frame.dataB, currentFrameReadedBytes, toReadLen)
            currentFrameReadedBytes += frame.dataLenB.toInt

            if (currentFrameReadedBytes == frame.dataLenB) { // frame completed
              wsconnection.currentFrame = null
              wsconnection.currentFrameReadedBytes = 0
              wsconnection.decodeState = WaitNewFrame
              if (frame.isMasked)
                WSUtil.mask(frame.maskKeyB, frame.dataB)

              println("get a new frame " + (frame.finB + ":" + frame.opcodeB) + " . frame data :" + Arrays.toString(frame.dataB))
              return frame.build() // 解析完成后转换为Frame

            } else {
              wsconnection.currentFrameReadedBytes = currentFrameReadedBytes
              continue = false // no more data
            }
          }
        }
      } while (continue)
    }

    null
  }

  private def finishHead(buffer: ChannelBuffer, frame: FrameBuilder) {
    if (frame.isMasked) {
      frame.maskKeyB = new Array[Byte](4)
      buffer.get(frame.maskKeyB)
    }

    if (frame.payloadBytes == 2) frame.dataLenB = buffer.getShort().toLong
    else if (frame.payloadBytes == 8) frame.dataLenB = buffer.getLong()

    // TODO 保存数据的数组使用了   Int
    frame.dataB = new Array[Byte](frame.dataLenB.toInt)
  }

  def encode(session: NioSession, message: Any): ChannelBuffer = {
    message match {
      case res: Response => res.toChannelBuffer
      case frame: Frame => Frame.toChannelBuffer(frame)
      case buffer: ChannelBuffer => buffer
      case _ => null
    }
  }

}