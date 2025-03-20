package synchronization;

/*

In Java, the wait() and notify() methods are part of the Object class and are used to facilitate inter-thread communication.
They allow one thread to temporarily release the lock and give other threads a chance to work, which is often used in scenarios
where threads need to cooperate and synchronize.

Here’s an overview of these methods:

1. wait(): When a thread calls wait(), it temporarily releases the monitor (lock) it holds and enters the waiting state until
another thread sends a signal. The thread remains waiting until one of the following happens:

Another thread calls notify() or notifyAll() on the same object.
The thread is interrupted.

2. notify(): When a thread calls notify(), it wakes up one of the threads that are currently waiting on the object.
The awakened thread doesn't immediately resume execution; it has to wait for the lock to be available.

3. notifyAll(): Similar to notify(), but it wakes up all threads that are currently waiting on the object.

Key Points:
1. Both wait(), notify(), and notifyAll() must be called from within a synchronized block or method. This is because these methods
rely on acquiring the lock (monitor) of the object they are called on.
2. Typically, wait() and notify() are used in situations where threads need to coordinate their actions, such as a producer-consumer
problem.

 */

public class WaitNotifyDemo {

    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        Thread one = new Thread(() -> {
            try {
                one();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread two = new Thread(() -> {
            try {
                two();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        one.start();
        two.start();
    }

    private static void one() throws InterruptedException {
        synchronized (LOCK) {
            System.out.println("Hello from method one...");
            LOCK.wait();
            System.out.println("Back Again in the method one");
        }
    }

    private static void two() throws InterruptedException {
        synchronized (LOCK) {
            System.out.println("Hello from method two...");
            LOCK.notify(); // Remaining code lines in the block are executed
            System.out.println("Hello from method two even after notify...");
        }
    }
}
