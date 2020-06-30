package com.epam.concurrency.e04.locks;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Consumer implements Callable<String> {

	private final List<Integer> buffer;
	private final Lock lock;
	private final Condition isEmpty;
	private final Condition isFull;

	public Consumer(List<Integer> buffer, Lock lock, Condition isEmpty, Condition isFull) {
		this.buffer = buffer;
		this.lock = lock;
		this.isEmpty = isEmpty;
		this.isFull = isFull;
	}

	@Override
	public String call() throws Exception {
		int count = 0;
		while (count++ < 50) {
			try {
				lock.lock();
				while (buffer.size() == 0) {
					// wait
					isEmpty.await();
/*
					// Can handle timeouts or producers don't produce issues.
					if (isEmpty.await(100, TimeUnit.MILLISECONDS)) {
						throw new TimeoutException("Consumer time out");
					}
*/
				}
				buffer.remove(buffer.size() - 1);
				// signal
				isFull.signalAll();
			} finally {
				lock.unlock();
			}
		}

		return "Consumed " + (count - 1);
	}
}
