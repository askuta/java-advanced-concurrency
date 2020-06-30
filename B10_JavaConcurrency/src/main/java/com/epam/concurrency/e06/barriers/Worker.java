package com.epam.concurrency.e06.barriers;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;

public class Worker implements Callable<List<Integer>> {

	private CyclicBarrier barrier;
	private List<Integer> inputList;

	public Worker(CyclicBarrier barrier, List<Integer> inputList) {
		this.barrier = barrier;
		this.inputList = inputList;
	}

	@Override
	public List<Integer> call() throws Exception {
		List<Integer> result = findEvenNumbers(inputList);
		try {
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e) {
			// Error handling.
		}

		return result;
	}

	private List<Integer> findEvenNumbers(List<Integer> input) {
		return input.stream()
				.filter(number -> 0 == number % 2)
				.collect(Collectors.toList());
	}
}
