package com.epam.concurrency.e06.barriers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FriendsBarrierExample {

	public static void main(String[] args) {
		ExecutorService service = Executors.newFixedThreadPool(4);

		// CyclicBarrier barrier = new CyclicBarrier(4);
		CyclicBarrier barrier = new CyclicBarrier(4, () -> System.out.println("Barrier opening."));
		List<Future<String>> futures = new ArrayList<>();

		try {
			for (int i = 0; i < 4; i++) {
				Friend friend = new Friend(barrier);
				futures.add(service.submit(friend));
			}

			futures.forEach(future -> {
				try {
					// future.get();
					// Waits only for $timeout time for threads.
					// Threads probably keep running after $timeout time.
					future.get(1000, TimeUnit.MILLISECONDS);
				} catch (InterruptedException | ExecutionException e) {
					System.out.println(e.getMessage());
				} catch (TimeoutException e) {
					System.out.println(e.getMessage());
					// Stop the long-running task:
					future.cancel(true);
				}
			});
		} finally {
			service.shutdown();
		}
	}
}

class Friend implements Callable<String> {

	private CyclicBarrier barrier;

	public Friend(CyclicBarrier barrier) {
		this.barrier = barrier;
	}

	@Override
	public String call() throws Exception {
		try {
			Random random = new Random();
			Thread.sleep((random.nextInt(20) * 100 + 100));
			System.out.println("I just arrived, waiting for the others...");

			// barrier.await();
			barrier.await(10, TimeUnit.SECONDS);
			System.out.println("Let's go to the cinema!");

			return "ok";
		} catch (InterruptedException e) {
			System.out.println("Interrupted");
		}

		return "nok";
	}
}
