package bruce.io.elf.httplike.ws.api

object MessageType extends Enumeration {
	type MessageType = Value
	val Text, Binary, Close, Ping, Pong, ReservedData, ReservedControl = Value
}