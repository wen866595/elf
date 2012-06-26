package bruce.io.elf.timeserver.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TestClientClose {

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("localhost", 8088);
			InputStream input = socket.getInputStream();
			Thread.sleep(2000);
			socket.close();
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
