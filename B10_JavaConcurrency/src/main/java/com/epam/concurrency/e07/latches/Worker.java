package com.epam.concurrency.e07.latches;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class Worker implements Callable<List<Integer>> {

	private CountDownLatch latch;
	private List<Integer> inputList;

	public Worker(CountDownLatch latch, List<Integer> inputList) {
		this.latch = latch;
		this.inputList = inputList;
	}

	@Override
	public List<Integer> call() throws Exception {
		List<Integer> result = findEvenNumbers(inputList);
		latch.countDown();

		return result;
	}

	private List<Integer> findEvenNumbers(List<Integer> input) {
		return input.stream()
				.filter(number -> 0 == number % 2)
				.collect(Collectors.toList());
	}
}
