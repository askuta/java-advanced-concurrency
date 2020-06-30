package com.epam.concurrency.e09.blockingqueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProducerConsumerExample {

	public static void main(String[] args) throws InterruptedException {
		BlockingQueue<String> queue = new ArrayBlockingQueue<>(50);

		List<Callable<String>> producersAndConsumers = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			producersAndConsumers.add(new Producer(queue));
		}
		for (int i = 0; i < 2; i++) {
			producersAndConsumers.add(new Consumer(queue));
		}

		ExecutorService service = Executors.newFixedThreadPool(4);
		try {
			List<Future<String>> futures = service.invokeAll(producersAndConsumers);
			futures.forEach(future -> {
				try {
					System.out.println(future.get());
				} catch (InterruptedException | ExecutionException e) {
					System.out.println("Exception: " + e.getMessage());
				}
			});
		} finally {
			service.shutdown();
			System.out.println("Executor service shut down.");
		}
	}
}
