package com.epam.concurrency.e08.counters;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounterExample {

	private static AtomicInteger counter = new AtomicInteger(0);

	public static void main(String[] args) {
		ExecutorService service = Executors.newFixedThreadPool(8);
		List<Future<?>> futures = new ArrayList<>();
		try {
			for (int i = 0; i < 4; i++) {
				futures.add(service.submit(new Incrementer(counter)));
			}
			for (int i = 0; i < 4; i++) {
				futures.add(service.submit(new Decrementer(counter)));
			}

			futures.forEach(future -> {
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					System.out.println(e.getMessage());
				}
			});

			System.out.println("counter = " + counter.get());
		} finally {
			service.shutdown();
		}
	}
}

class Incrementer implements Runnable {

	private AtomicInteger counter;

	public Incrementer(AtomicInteger counter) {
		this.counter = counter;
	}

	@Override
	public void run() {
		for (int i = 0; i < 1000; i++) {
			counter.incrementAndGet();
		}
	}
}

class Decrementer implements Runnable {

	private AtomicInteger counter;

	public Decrementer(AtomicInteger counter) {
		this.counter = counter;
	}

	@Override
	public void run() {
		for (int i = 0; i < 1000; i++) {
			counter.decrementAndGet();
		}
	}
}
