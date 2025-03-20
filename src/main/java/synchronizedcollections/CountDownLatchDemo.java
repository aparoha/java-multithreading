package synchronizedcollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/*

1. A CountDownLatch has a counter field, which you can decrement as we require. We can then use it to block a calling thread
until it’s been counted down to zero
2. We could instantiate the CountDownLatch with the same value for the counter as a number of threads we want to work across.
Then, we could just call countdown() after each thread finishes, guaranteeing that a dependent thread calling await() will block
until the worker threads are finished.

1. countDown() Method:
Purpose: The countDown() method is used to decrement the latch’s count. Each time countDown() is called, it reduces the latch's count by one. When the count reaches zero, all threads waiting on the latch are released and can continue execution.
Usage: It is typically called by worker threads (or any thread) that is responsible for completing a task or an event. It signals that the task or event is done and the latch count should decrease.
Example: If you have 5 threads, each thread will call countDown() after completing its task, signaling that the thread is finished.

2. await() Method:
Purpose: The await() method is called by threads that need to wait for the latch’s count to reach zero before they can proceed. It blocks the calling thread until the latch’s count becomes zero, meaning all necessary tasks or events have completed.
Usage: It is called by the main thread (or other threads) to wait for other threads to complete their tasks. Once the latch count reaches zero, all threads waiting on await() are released and can proceed with their execution.
Example: If you have 5 threads, the main thread or some other thread will call await(), blocking itself until all 5 worker threads call countDown().

3. Use case 1 - Waiting for a Pool of Threads to Complete
4. Use case 2

In scenarios where you have a pool of threads that need to wait until a certain event or condition is met before starting,
you can use a CountDownLatch to synchronize the start of all threads in the pool. This is useful when you want multiple
threads to start processing concurrently, but you need to make sure they all begin at the same time.

For example, let's say you have a pool of threads that need to wait for some preparation or initial setup to complete.
Once everything is ready, the threads can start working simultaneously.

1. Starting Multiple Threads Simultaneously
Use Case: When you have multiple threads performing independent tasks, and you want to ensure they all start at the same time.
Example: Suppose you need to start several workers that perform different operations, and they should begin working only after certain setup or initialization tasks are complete.

2. Waiting for All Threads to Complete
Use Case: You want the main thread to wait for multiple worker threads to complete before continuing.
Example: This can be useful in scenarios where you need to collect results from all worker threads before proceeding with further processing.

3. Ensuring All Threads Are Ready Before Starting
Use Case: In some cases, you need all threads to be ready before they can start executing their task. A CountDownLatch can be used to block all threads until they are all ready to begin.
Example: A distributed computation where each thread must first initialize and then wait for others before they start their actual processing.

4. Implementing a Barrier for Parallel Tasks
Use Case: A barrier is a synchronization point in parallel computing where threads must wait until all have reached the barrier point before proceeding.
Example: A simulation that involves multiple phases, and all threads must complete a phase before starting the next one. This can be implemented using CountDownLatch as a simple barrier.

5. Parallel Data Processing
Use Case: In scenarios where data needs to be processed in parallel (like in big data processing or distributed systems), and you want to ensure that all processing threads finish before proceeding.
Example: A scenario where multiple tasks process chunks of data, and the main thread waits for all data processing threads to complete before aggregating the results.

6. Distributed Systems and Coordination
Use Case: In distributed systems, where you might need to ensure that all nodes or tasks are ready before beginning a computation or data transfer.
Example: A scenario where multiple distributed nodes (or services) must complete some setup (e.g., database connection or data loading) before starting a distributed task.

7. Coordinating Test Execution
Use Case: When running multiple tests in parallel, you might want to make sure all threads wait for a specific setup phase to complete before starting the test itself.
Example: Running multiple unit tests or parallel benchmarks, where each thread must wait for some setup (e.g., data initialization) before executing its respective test.

8. Custom Initialization in Multi-Threaded Applications
Use Case: When you need to initialize a resource (like a shared database connection, configuration file, or system service) before all threads in an application can access it. All threads will wait until the initialization is complete.
Example: In multi-threaded server applications, the server might wait for certain resources to be initialized before accepting requests from clients.

9. Countdown for Event-Driven Systems
Use Case: You can use a CountDownLatch to trigger events across different parts of a system. For instance, you can count down after specific events have occurred, allowing multiple threads to continue based on those events.
Example: A multi-phase event system, where several parts of an application wait for an event to occur, and they all proceed once the event is triggered.
 */
class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        List<String> outputScraper = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch countDownLatch = new CountDownLatch(5);
        List<Thread> workers = Stream
                .generate(() -> new Thread(new Worker(outputScraper, countDownLatch)))
                .limit(5)
                .collect(toList());

        workers.forEach(Thread::start);
        countDownLatch.await();
        outputScraper.add("Latch released");
        System.out.println(outputScraper);

        // Number of worker threads in the pool
        int numThreads = 5;

        // CountdownLatch to coordinate when all threads should start
        CountDownLatch startLatch = new CountDownLatch(1); // Wait for one event (starting signal)

        // Executor service to create a thread pool
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        // Submit tasks to the thread pool
        for (int i = 0; i < numThreads; i++) {
            executor.submit(new CoordinateThreadsWaitingToStart(startLatch, i));
        }

        // Simulate some preparation work (could be setup, initialization, etc.)
        System.out.println("Preparation completed, now starting all threads...");

        // After the preparation is complete, count down the latch to release all threads
        startLatch.countDown();

        // Shutdown the executor after all tasks are done
        executor.shutdown();
    }
}

class Worker implements Runnable {
    private List<String> outputScraper;
    private CountDownLatch countDownLatch;

    public Worker(List<String> outputScraper, CountDownLatch countDownLatch) {
        this.outputScraper = outputScraper;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        System.out.println("Doing some work................");
        outputScraper.add("Counted down");
        countDownLatch.countDown();
    }
}

class CoordinateThreadsWaitingToStart implements Runnable {
    private final CountDownLatch startLatch;
    private final int threadId;

    public CoordinateThreadsWaitingToStart(CountDownLatch startLatch, int threadId) {
        this.startLatch = startLatch;
        this.threadId = threadId;
    }

    @Override
    public void run() {
        try {
            // Wait for the signal to start (when latch is counted down to 0)
            System.out.println("Thread " + threadId + " is waiting to start...");
            startLatch.await();

            // Simulating work after the latch count reaches zero
            System.out.println("Thread " + threadId + " has started working.");
            Thread.sleep(1000);  // Simulating work
            System.out.println("Thread " + threadId + " has finished.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
