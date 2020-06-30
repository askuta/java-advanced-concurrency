Concurrency
===========

Threads are expensive to create and expensive to destroy. If you can create a thread any time, it's
difficult to control the number of threads inside your application.

Executors create pools of ready-to-use threads, and use them. Instead of creating threads with
tasks, we pass tasks to the pool and the pool (or one of its threads) will execute it. Creating an
Executor is more efficient than creating a thread on demand.


Executor:

  public interface Executor {
    void execute(Runnable task);
  }


ExecutorService:

  public interface ExecutorService extends Executor {
    // methods
  }


An example for creating a single thread executor:

  ExecutorServide singleThreadExecutor = Executors.newSingleThreadExecutor();
  Runnable task = () -> System.out.println("Hello from a thread!");
  singleThreadExecutor.execute(task);

  The thread will be kept alive as long as the pool. You can stop the thread by calling the
  shutdown method of the service.


An example for creating a fixed thread executor:

  ExecutorService multipleThreadsExecutor = Executors.newFixedThreadPoolExecutor(42);

- a task is added to the waiting queue when no thread is available
- tasks are executed in the order of their submission

- we cannot know if a task is done or not
- we can cancel the execution of a task till it is in the waiting queue; once the execution of a
  task has started, it is not possible to stop it 


A task does not return anything (from a Runnable):
  - neither an object
  - nor an Exception
There is no way we can know if a task is done or not.

Callable is a solution for both problems.


The Callable interface
----------------------

  public interface Callable<V> {
    V call() throws Exception;
  }

The Executor interface itself does not handle Callables. However, the ExecutorService interface has
a submit() method that does:

  <T> Future<T> submit(Callable<T> task);

The Future<T> object is a wrapper of the return value of the task. It returns immediately once you
called submit(). You can access the wrapped value by calling get() on the Future object, however,
it is blocking until the wrapped object is available or an Exception is thrown.

The Future.get() method can raise two Exceptions:
  - InterruptedException: in case the thread or the executor is interrupted
  - ExecutionException: in case the task throws an Exception (the Exception is wrapped into
    ExecutionException)

It is possible to pass a timeout to the get() call, to prevent indefinitely blocking calls.


Executor services
-----------------

  - newSingleThreadExecutor: an executor with only one thread
  - newFixedThreadPool: an executor with ${pool_size} threads
  - newCachedThreadPool: creates threads on demand, keeps unused threads for 60 seconds
    (by default), then terminates them
  - newScheduledThreadPool: creates a pool, returns a ScheduledExecutorService:
    - schedule(task, delay)
    - scheduleAtFixedRate(task, delay, period) --> e.g. execute task in every 5 minute
    - scheduleWithFixedDelay(task, initialDelay, delay) --> e.g. there is a 5 minute break between
      task executions

There are 3 ways to shutdown executor services:
  - shutdown():
    - continue to execute all submitted tasks
    - execute waiting tasks
    - do not accept new tasks
    - then shutdown
    - this is the soft way
  - shutdownNow():
    - halt the running tasks
    - do not execute waiting tasks
    - then shutdown
    - this is the hard way
  - awaitTermination(timeout):
    - shutdown
    - wait for the timeout
    - if there are remaining tasks, then halt everything


Locks and semaphores
--------------------

  - synchronized and volatile: intrinsic locking
    - one thread can be inside the synchronized block, all other threads those want to use the code
      are blocked
    - if the one thread inside the synchronized block is blocked, the others are still blocked
    - there is no way to release the waiting threads
    
    synchronized (key) {...}
    
  - Lock: explicit locking
    - offers the same guarantees than synchronized (expect ordering)
    - adds more functionalities

    Lock lock = new ReentrantLock();
    try {
      lock.lock();
      ...
    } finally {
      lock.unlock();
    }

    - interruptable lock:
      - lock.lockInterruptibly()
      - the thread will wait until it can enter the guarded block of code, but other thread can
        interrupt it by calling its interrupt() method

    - timed lock acquisition:
      - if (lock.tryLock()) {...}
        - if a thread is already executing the guarded block of code, then tryLock() returns false,
          immediately
      - if (lock.tryLock(1, TimeUnit.SECONDS)) {...}
        - if a thread is already executing the guarded block of code, then waits for the given
          amount of time
        - if the thread is still executing the guarded block of code after the given time, then the
          tryLock() returns false
    - fair reentrant lock:
      - Lock lock = new ReentrantLock(true); // fair
      - the execution order of waiting threads is not determined by default
      - fair reentrant lock lets the first thread to execute the guarded code
      - fairness has extra costs


  - the await() call is blocking, can be interrupted:
    - await()
    - await(time, timeUnit)
    - awaitNanos(nanosTimeout)
    - awaitUntil(date)
    - awaitUninterruptibly()
  - these are ways to prevent the blocking of waiting threads with the Condition API
  - a fair Lock generates fair Condition


Read / Write Locks
------------------

  - in some cases what we need is exclusive writes and allow parallel reads
  - the ReadWriteLock is an interface with two methods:
    - readLock(): to get a read lock
    - writeLock(): to get a write lock
  - both are instances of Lock
  - only one thread can hold the write lock
  - when the write lock is held, no one can hold the read lock
  - as many threads as needed can hold the read lock

  ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  Lock readLock = readWriteLock.readLock();
  Lock writeLock = readWriteLock.writeLock();

  - the right pattern is to create a read / write lock
  - then get a read and a write lock as a pair from this read / write lock
  - it can be used to create a thread safe cache

  Map<Long, User> cache = new HashMap<>();
  try {
    readLock.lock();
    return cache.get(key);
  } finally {
    readLock.unlock();
  }


Semaphores
----------

  - it looks like a lock, but allows several threads in the same block of code
  - a Semaphore is non-fair by default but can be mad to fair
    new Semaphore(5, true);
  - the acquire() call is blocking until a permit is available
  - it is possible to call acquire for more than one permit
    semaphore.acquire(2);
    semaphore.release(2);

  Semaphore semaphore = new Semaphore(5);  // permits
  try {
    semaphore.acquire();
    ...
  } finally {
    semaphore.release();
  }

  - by default if a waiting thread is interrupted it will throw an InterruptedException
  - uninterruptibility means that the thread cannot be interrupted
  - it can be only be freed by calling its release() method

  semaphore.acquireUninterruptibility();

  - acquisition can be made immediate and can fail if there is already a thread in the guarded
    block of code

  if (semaphore.tryAcquire()) {
    // guarded code
  } else {
    // I could not enter the guarded code
  }

  - you can also set a timeout before failing to acquire a permit
  - this pattern can also request amore than one permit

  if (semaphore.tryAcquire(1, TimeUnit.SECONDS)) {...}


Wrap-up:
  - it is possible to reduce the number of permits (cannot increase)
  - it is possible to check the waiting threads
    - are there any
    - how many
    - get them as a collection


Barrier
-------

  - to have several tasks wait for each other
  - a given computation is shared among several threads
  - each thread is given a subtask
  - when all threads are done, then a merging operation is run
  - we need to know when all threads have finished their task
  - we need to launch a post-processing at that moment

  CyclicBarrier barrier = new CyclicBarrier(4);

  Callable<List<Integer>> task = () -> {
    Set<Integer> result = findPrimes(inputSet);
    try {
      barrier.await(); // Blocks until everybody is ready.
    } catch(Exception e) {...}
    return result;
  }

  - 4 is the number of tasks that will be launched
  - the await() call blocks until all 4 calls have beeen finished

CyclicBarrier:
  - a tool to synchronize several threads between them, and let them continue when they reach a
    common point
  - a CyclicBarrier closes again once opened, allowing for cyclic computations, can also be reset
  - the threads can wait for each other on time outs


Latches
-------

  - latch: to count down operations and let a task start
  - similar to Barriers but we don't want the barrier to reset, thus blocking everything
  - we need something that, once opened, cannot be closed
  - it is countdown latch

CountDownLatch:
  - a tool to check that different threads did their task
  - and synchronize the beginning of subsequent tasks on the last one to complete
  - once open CountDownLatch cannot be closed again

CyclicBarrier:
  - useful for parallel computations
CountdownLatch:
  - useful for starting an application on the completion of different initializations


CASing
------

  - CASing: Compare And Swap
  - works well when concurrency is not "too" high
  - many tries until it is accepted
  - CASing may create load on the memory and CPU in case the concurrency is very high
  - Concurrent Read/Write:
    - the problem in concurrent programming is the concurrent access to shared memory
    - we used synchronization to handle that
    - in certain cases, we have more tools
    - synchronization has a cost, it is not always essential to use it --> CASing can be used
    - CASing works with three parameters:
      - a location in memory
      - an existing value at that location
      - a new value to replace the existing value
    - if the current value at that address is the expected value, then it is replaced by the new
      value and returns true; if not, it returns false
    - all is executed in a single, atomic assembly instruction

  AtomicLong counter = new AtomicLong(10L);
  long newValue = conuter.incrementAndGet();

  - the Java API tries to apply the incrementation
  - the CASIng tells the calling code if the incrementation failed
  - if it did, the API tries again (and again...)


AtomicBoolean:
  - wrapper around a boolean
  - get(), set()
  - getAndSet(value)
  - compareAndSet(expected, value)

AtomicInteger, AtomicLong:
  - wrapper around an int or long
  - get(), set()
  - getAndSet(value)
  - compareAndSet(expected, value)
  - getAndUpdate(unaryOp), updateAndGet(unaryOp)
  - getAndIncrement(), getAndDecrement()
  - getAndAdd(value), addAndGet(value)
  - getAndAccumulate(value, binOp), accumulateAndGet(value, binOp)

AtomicReference<V>
  - wrapper around an object
  - get(), set()
  - getAndSet(value)
  - getAndUpdate(unaryOp), updateAndGet(unaryOp)
  - getAndAccumulate(value, binOp), accumulateAndGet(value, binOp)
  - compareAndSet(expected, value)


Adders and Accumulators
-----------------------

  - since Java 8
  - all methods are built on the "modify and get" or "get and modify" concepts
  - sometimes we don not need the "get" part at each modification this is why LongAdder and
    LongAccumulator classes were introduced in Java 8
  - without the "get" part, execution can be optimized better
  - LongAdder and LongAccumulator work as an AtomicLong but don't return the updated value, so it
    can distribute the update on different cells and merge the result on a get call
  - these classes are tailored for high concurrency

LongAdder:
  - increment(), decrement()
  - add(long)
  - sum(), longValue(), intValue() --> return content
  - sumThenReset()

LongAccumulator:
  - built on a binary operator
  - accumulate(long)
  - get()
  - intValue(), longValue(), floatValue(), doubleValue()
  - getThenReset()


Concurrent Collections and Maps
-------------------------------

  - you should avoid using Vector and Stack classes
    - they are thread-safe
    - but legacy structures, very poorly implemented


Copy on Write:
  - exists for list and set
  - no locking for read operations
  - write operations create a new structure, then the new structure replaces the previous one
    (copy the internal representation of the current list/set, add the new element and start using
    the new representation)
  - two structures:
    - CopyOnWriteArrayList
    - CopyOnWriteArraySet
  - Copy on Write structures work well when there are many reads and very, very few writes


Queues and Stacks
-----------------

  - Queue and Deque: interfaces
  - ArrayBlockingQueue: a bounded blocking queue built on an array
  - ConcurrentLinkedQueue: an unbounded blocking queue
  - how does a Queue work:
    - FIFO: queue
    - LIFO: stack
  - in the JDK we have the following:
    - Queue: queue
    - Deque: both queue and stack (there is no pure Stack implementation, except the Stack class)


Adding an element to a full BlockingQueue:
  - boolean add(E e);
    fail: IllegalArgumentException
  - boolean offer(E e);
    boolean offer(E e, timeOut, timeUnit);
    fail: return false
  - void put(E e);
    block until a cell becomes available


Deque:
  - addFirst(), offerFirst()
    add element at the head of a queue
  - putFirst()
    add element at the head of a BlockingDeque
  - pollLast(), peekLast()
    can return null
  - removeLast(), getLast()
    can throw an exception


Queue:
  - poll(), peek()
    can return null
  - remove(), element()
    can throw an exception


BlockingQueue:
  - take()
    blocks till an element is available


BlockingDeque:
  - takeLast()
    - can block a thread


Concurrent Maps
---------------

  - there are two implementations:
    - ConcurrentHashMap: since JDK 7 (JDK 8 completely rewrote it)
      - JDK 7, the number of key/value pairs has to be (much) greater than the concurrency level
      - JDK 8:
        - completely reimplemented
        - serialization is complatible with the JDK 7 version in both ways
        - tailored to handle heavy concurrency an millions of key/value pairs
        - parallel methods implemented
    - ConcurrentSkipListMap: since JDK 6, does not rely on synchronization
  - ConcurrentMap defines atomic operations:
    - putIfAbsent(key, value)
    - remove(key, value)
    - replace(key, value)
    - replace(key, existingValue, newValue)


ConcurrentHashMap: since JDK 7 (JDK 8 completely rewrote it)
  - JDK 7:
    - built on an array the holds the buckets
    - synchronizing the whole array would be very inefficient
    - we should allow several threads on different buckets
    - we should allow concurrent reads
    - synchronizing on parts of the array could work and allows a certain level of parallelism;
      ConcurrentHashMap works like this up to JDK 7
      number of segments = concurrency level (16 - 64k)
      the number of key/value pairs has to be (much) greater than the concurrency level
  - JDK 8:
    - completely reimplemented
    - serialization is complatible with the JDK 7 version in both ways
    - tailored to handle heavy concurrency an millions of key/value pairs
    - parallel methods implemented (search(), searchKeys(), searchValues(), searchEntries())

    ConcurrentHashMap<Long, String> map = ...; // JDK 8
    String result = 
      map.search(10000, (key, value) -> value.startsWith("a") ? "a" : null);

      - first parameter: the parallelism threshold --> if there is more than 10000 key/value
        pairs in the map, then parallel search will be executed
      - second parameter: the operation to be applied

    ConcurrentHashMap<Long, List<String>> map = ...; // JDK 8
    String result =
      map.reduce(10000,
                 (key, value) -> value.size(),
                 (value1, value2) -> Integer.max(value1, value2));

      - the first bifunction maps to the element to be reduced
      - the second bifunction reduces two elements together

    ConcurrentHashMap<Long, List<String>> map = ...; // JDK 8
    String result =
      map.forEach(10000, (key, value) -> value.removeIf(s -> s.length() > 20));

      - the biconsumer is applied to all key/value pairs of the map
      - other methods: forEachKeys(), forEachValues(), forEachEntry()


Concurrent Sets:
  - ConcurrentHashMap to create Concurrent Sets:
    Set<String> set = ConcurrentHashMap.<String>newKeySet(); // JDK 8
  - parallel operations are not available


Concurrent Skip Lists
---------------------

  - another concurrent map since JDK 6
  - it is a smart structure used to create linked lists
  - relies on atomic reference operations, not synchronization
  - can be used to create maps and sets
  - Skip List is a linked list with another layer(s) of fast access list(s) on them
  - elements must be sorted in the Skip List

  - a Skip List is used to implement a map
  - the keys are sorted
  - the structure ensures fast access to any key
  - not an array-based structure
  - there are other ways than locking to guard it


ConcurrentSkipListMap:
  - uses the Concurrent Skip List structure
  - all the references are implemented with AtomicReference
  - it is a thread-safe map with no synchronization
  - tailored for high concurrency
  - some methods should not be used (size())


ConcurrentSkipListSet:
  - uses the same structure
  - tailored for high concurrency
  - some methods should not be used (size())


Which structure for which case?
-------------------------------

  - there is no silver bullet
  - if you have very few writes, use copy on write structures
  - if you have low concurrency, you can rely on synchronization
  - in high concurrency, skip lists are usable with many objects, or ConcurrentHashMap
  - high concurrency with few objects is always problematic
