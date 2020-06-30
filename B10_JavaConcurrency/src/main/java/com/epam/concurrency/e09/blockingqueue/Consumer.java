package com.epam.concurrency.e09.blockingqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class Consumer implements Callable<String> {

	private BlockingQueue<String> queue;

	public Consumer(BlockingQueue<String> queue) {
		this.queue = queue;
	}

	@Override
	public String call() throws Exception {
		int count = 0;
		while (count++ < 50) {
			queue.take();
		}

		return "Consumed " + (count - 1);
	}
}
