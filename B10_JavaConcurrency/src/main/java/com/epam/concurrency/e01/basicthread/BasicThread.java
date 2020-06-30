package com.epam.concurrency.e01.basicthread;

public class BasicThread {

	public static void main(String[] args) {
		Runnable task = () -> System.out.println("Hello World from Thread: "
				+ Thread.currentThread().getName());
		Thread thread = new Thread(task);
		thread.start();

		for (int i = 0; i < 10; i++) {
			new Thread(task).start();
		}
	}
}
