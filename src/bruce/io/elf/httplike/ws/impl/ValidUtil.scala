package bruce.io.elf.httplike.ws.impl

import java.security.MessageDigest

object ValidUtil {
  import scala.collection.mutable

  val WS_GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"

  def generateAccept(key: String): String = {
    val sk = key.trim()

    val base = sk + WS_GUID

    val md = MessageDigest.getInstance("SHA-1")
    val digest = md.digest(base.getBytes())

    new sun.misc.BASE64Encoder().encode(digest)
  }

  def validResponse(resHeaders: mutable.Map[String, String], reqHeaders: mutable.Map[String, String]): Boolean = {
    val upgrade = resHeaders.getOrElse("Upgrade", "")
    if ("websocket".equals(upgrade))
      return false

    val connection = resHeaders.getOrElse("Connection", "")
    if (!connection.contains("Upgrade"))
      return false

    val reqSubProtocol = reqHeaders.getOrElse("Sec-WebSocket-Protocol", "")
    val resSubProtocol = resHeaders.getOrElse("Sec-WebSocket-Protocol", "")
    // @TODO

    val resAccept = resHeaders.getOrElse("Sec-WebSocket-Accept", "")
    val reqKey = reqHeaders.get("Sec-WebSocket-Key").get
    val reqAccept = generateAccept(reqKey)
    if (!reqAccept.equals(resAccept))
      return false

    true
  }

  def validStatusCode(statusCode: String): Boolean = "101".equals(statusCode)

  def validRequestHeaders(headers: mutable.Map[String, String]): String = {
    val host = headers.getOrElse("Host", null)
    if (host == null)
      return "Host is missing ."

    val upgrade = headers.getOrElse("Upgrade", "")
    if (!"websocket".equals(upgrade))
      return "'Upgrade' header field must be 'websocket' ."

    val connection = headers.getOrElse("Connection", "")
    if (!connection.contains("Upgrade"))
      return "'Connection' header field must contains 'Upgrade' ."

    val secWebsocketKey = headers.getOrElse("Sec-WebSocket-Key", null)
    if (secWebsocketKey == null)
      return "'Sec-WebSocket-Key' header field must be nonce ."

    val secWebSocketVersion = headers.getOrElse("Sec-WebSocket-Version", "")
    if (!"13".equals(secWebSocketVersion))
      return "'Sec-WebSocket-Version' header field must be '13'"

    null
  }

  def validHttpVersion(httpVersion: String): Boolean = {
    "HTTP/1.1".compareTo(httpVersion) <= 0
  }

  def validMethod(method: String): Boolean = {
    "GET".equals(method)
  }

  def main(args: Array[String]) {
    val accept = generateAccept("dGhlIHNhbXBsZSBub25jZQ==")
    println(accept)
    println("s3pPLMBiTxaQ9kYGzzhZRbK+xOo=".equals(accept))
  }
}