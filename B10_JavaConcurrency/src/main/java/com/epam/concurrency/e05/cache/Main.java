package com.epam.concurrency.e05.cache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	public static void main(String[] args) {
		CacheWithReadWriteLock cache = new CacheWithReadWriteLock();

		ExecutorService executorService = Executors.newFixedThreadPool(4);
		try {
			for (int i = 0; i < 4; i++) {
				executorService.submit(new Producer(cache));
			}
		} finally {
			executorService.shutdown();
		}
	}
}
