package bruce.io.elf.timeserver.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class TestTimeServerClient {

	private static final AtomicInteger successCount = new AtomicInteger(0);

	private static long totalTime = 0;

	static class Task implements Runnable {
		@Override
		public void run() {
			Socket socket = null;
			try {
				long start = System.currentTimeMillis();
				socket = new Socket("localhost", 8088);
				InputStream input = socket.getInputStream();

				ByteArrayOutputStream baos = new ByteArrayOutputStream(64);

				int b = -1;
				while ((b = input.read()) != -1) {
					baos.write(b);
				}

				long end = System.currentTimeMillis();
				long serverTime = Long.parseLong(baos.toString());
				/*System.out.println("used time :" + (end - start)
						+ ", response time: " + (end - serverTime)
						+ ", request time :" + (serverTime - start));*/

				totalTime += (end - start);
				successCount.addAndGet(1);

			} catch (UnsupportedEncodingException e) {
				System.err.println(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} finally {
				if (socket != null)
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}

		}
	}

	public static void main(String[] args) throws InterruptedException {
		int taskSize = 200000;
		// thread(taskSize);

		int poolSize = 100;
		if(poolSize > taskSize) poolSize = taskSize;
		
		pool(taskSize, poolSize);
	}

	private static void pool(int taskSize, int poolSize)
			throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(taskSize);
		ExecutorService pool = Executors.newFixedThreadPool(poolSize);

		final Task task = new Task();

		long start = System.currentTimeMillis();
		for (int i = 0; i < taskSize; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					task.run();
					latch.countDown();
				}
			});
		}

		latch.await();
		long end = System.currentTimeMillis();
		System.out.println("successCount :" + successCount.get());
		System.out.println("use time :" + (end - start) + ", totalTime :"
				+ totalTime + ", avg :" + (totalTime / taskSize));
		pool.shutdownNow();
	}

	private static void thread(int taskSize) throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(taskSize);

		final Task task = new Task();

		long start = System.currentTimeMillis();
		for (int i = 0; i < taskSize; i++) {
			new Thread() {
				public void run() {
					try {
						task.run();
					} catch (Exception e) {
					}
					latch.countDown();
				}
			}.start();
		}

		latch.await();
		long end = System.currentTimeMillis();
		System.out.println("successCount :" + successCount.get());
		System.out.println("use time :" + (end - start));
	}

}
