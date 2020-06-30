package com.epam.concurrency.e05.cache;

import java.util.Random;
import java.util.concurrent.Callable;

public class Producer implements Callable<String> {

	private final CacheWithReadWriteLock cache;

	private final Random random;

	public Producer(CacheWithReadWriteLock cache) {
		this.cache = cache;
		random = new Random();
	}

	@Override
	public String call() throws InterruptedException {
		while (true) {
			long key = random.nextInt(1000);
			cache.put(key, Long.toString(key));
			if (cache.get(key) == null) {
				System.out.println("Key " + key + " has not been put in the map.");
			} else {
				System.out.println("put: " + key);
			}
		}
	}
}
