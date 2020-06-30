package com.epam.concurrency.e04.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerWithLocks {

	public static void main(String[] args) throws InterruptedException {
		List<Integer> buffer = new ArrayList<>();
		Lock lock = new ReentrantLock();
		Condition isEmpty = lock.newCondition();
		Condition isFull = lock.newCondition();

		List<Producer> producers = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			producers.add(new Producer(buffer, lock, isEmpty, isFull));
		}

		List<Consumer> consumers = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
			consumers.add(new Consumer(buffer, lock, isEmpty, isFull));
		}

		System.out.println("Producers and Consumers launched.");

		List<Callable<String>> producersAndConsumers = new ArrayList<>();
		producersAndConsumers.addAll(producers);
		producersAndConsumers.addAll(consumers);

		ExecutorService executorService = Executors.newFixedThreadPool(8);
		try {
			List<Future<String>> futures = executorService.invokeAll(producersAndConsumers);
			futures.forEach(future -> {
				try {
					System.out.println(future.get());
				} catch (InterruptedException | ExecutionException e) {
					System.out.println("Exception: " + e.getMessage());
				}
			});
		} finally {
			executorService.shutdown();
			System.out.println("Executor service shut down.");
		}
	}
}
