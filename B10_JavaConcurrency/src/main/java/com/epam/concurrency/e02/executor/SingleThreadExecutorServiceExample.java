package com.epam.concurrency.e02.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleThreadExecutorServiceExample {

	public static void main(String[] args) {
		Runnable task = () -> System.out.println("Hello World from Thread: "
				+ Thread.currentThread().getName());

		ExecutorService service = Executors.newSingleThreadExecutor();
		for (int i = 0; i < 10; i++) {
			service.execute(task);
		}

		// The program would not stop without shutting down the executor service.
		service.shutdown();
	}
}
