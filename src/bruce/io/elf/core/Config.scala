package bruce.io.elf.core

object Config {
	def ReactorSize = 2
	
	
	def Acceptor_ThreadName = "Acceptor-0"
	def Acceptor_SelectInternalTime = 1000
	def Acceptor_Backlog = 100
	
	def Reactor_SelectInternalTime = 1000
	
	
	def Session_BufferSize = 8 * 1024
}