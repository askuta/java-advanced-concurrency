package com.epam.concurrency.e09.blockingqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class Producer implements Callable<String> {

	private BlockingQueue<String> queue;

	public Producer(BlockingQueue<String> queue) {
		this.queue = queue;
	}

	@Override
	public String call() throws Exception {
		int count = 0;
		while (count++ < 50) {
			queue.put(Integer.toString(count));
		}

		return "Produced " + (count -1);
	}
}
