package com.epam.concurrency.e08.counters;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import sun.misc.Unsafe;

public class MyAtomicCounter extends AtomicInteger {

	private static final long serialVersionUID = 1L;

	private static MyAtomicCounter counter = new MyAtomicCounter(0);

	// DO NOT USE Unsafe IN PRODUCTION CODE!
	private static Unsafe unsafe = null;
	static {
		Field unsafeField;
		try {
			unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
			unsafeField.setAccessible(true);
			unsafe = (Unsafe) unsafeField.get(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AtomicInteger counterIncrement = new AtomicInteger(0);

	public MyAtomicCounter(int counter) {
		super(counter);

	}

	public int myIncrementAndGet() {
		long valueOffset = 0L;
		try {
			valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}

		int v;
		do {
			v = unsafe.getIntVolatile(this, valueOffset);
			counterIncrement.incrementAndGet();
		} while (!unsafe.compareAndSwapInt(this, valueOffset, v, v + 1));

		return v;
	}

	public int getIncrements() {
		return this.counterIncrement.get();
	}

	public static void main(String[] args) {
		ExecutorService service = Executors.newFixedThreadPool(20);
		List<Future<?>> futures = new ArrayList<>();
		try {
			for (int i = 0; i < 10; i++) {
				futures.add(service.submit(new MyIncrementer(counter)));
			}
			for (int i = 0; i < 10; i++) {
				futures.add(service.submit(new MyDecrementer(counter)));
			}

			futures.forEach(future -> {
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					System.out.println(e.getMessage());
				}
			});

			System.out.println("counter = " + counter.get());
			System.out.println("# increments = " + counter.getIncrements());
		} finally {
			service.shutdown();
		}
	}
}

class MyIncrementer implements Runnable {

	private MyAtomicCounter counter;

	public MyIncrementer(MyAtomicCounter counter) {
		this.counter = counter;
	}

	@Override
	public void run() {
		for (int i = 0; i < 1000; i++) {
			counter.myIncrementAndGet();
		}
	}
}

class MyDecrementer implements Runnable {

	private MyAtomicCounter counter;

	public MyDecrementer(MyAtomicCounter counter) {
		this.counter = counter;
	}

	@Override
	public void run() {
		for (int i = 0; i < 1000; i++) {
			counter.decrementAndGet();
		}
	}
}
