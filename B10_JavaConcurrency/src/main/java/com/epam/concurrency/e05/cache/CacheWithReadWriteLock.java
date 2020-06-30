package com.epam.concurrency.e05.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheWithReadWriteLock {

	private final Map<Long, String> cache = new HashMap<>();
	private final ReadWriteLock lock;
	private final Lock readLock;
	private final Lock writeLock;

	public CacheWithReadWriteLock() {
		lock = new ReentrantReadWriteLock();
		readLock = lock.readLock();
		writeLock = lock.writeLock();
	}

	public String put(Long key, String value) {
		writeLock.lock();
		try {
			return cache.put(key, value);
		} finally {
			writeLock.unlock();
		}
	}

	public String get(Long key) {
		readLock.lock();
		try {
			return cache.get(key);
		} finally {
			readLock.unlock();
		}
	}
}
