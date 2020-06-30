package com.epam.concurrency.e07.latches;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {

	private static final int BATCH_SIZE = 25;
	private static final int BATCH_COUNT = 4;

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		CountDownLatch latch = new CountDownLatch(4);
		ExecutorService service = Executors.newFixedThreadPool(4);

		List<Integer> finalResult = new ArrayList<>();
		List<Future<List<Integer>>> futures = new ArrayList<>();

		for (int count = 0; count < BATCH_COUNT; count++) {
			List<Integer> batch = new ArrayList<>();
			for (int index = 0; index < BATCH_SIZE; index++) {
				batch.add(count * BATCH_SIZE + index);
			}
			Worker worker = new Worker(latch, batch);
			Future<List<Integer>> future = service.submit(worker);
			futures.add(future);
		}

		latch.await(10, TimeUnit.SECONDS); // Blocks until the count reaches 0.

		for (Future<List<Integer>> future : futures) {
			finalResult.addAll(future.get());
		}

		System.out.println(finalResult);

		service.shutdown();
	}
}
