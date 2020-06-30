package com.epam.concurrency.e03.callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CallableExample {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Callable<String> task = () -> {
			Thread.sleep(300);
			return "Hello World from Thread: " + Thread.currentThread().getName();
		};

		ExecutorService service = Executors.newFixedThreadPool(4);
		try {
			for (int i = 0; i < 10; i++) {
				Future<String> future = service.submit(task);
				System.out.println("I get: " + future.get(300, TimeUnit.MILLISECONDS));
			}
		} catch (TimeoutException te) {
			te.printStackTrace();
		} finally {
			// The program would not stop without shutting down the executor service.
			service.shutdown();
		}
	}
}
