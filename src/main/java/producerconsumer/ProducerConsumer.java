package producerconsumer;

import java.util.ArrayList;
import java.util.List;

/*

The producer and consumer problem is one of the small collection of standard, well-known problems in concurrent programming. A finite-size buffer and two classes of threads, producers and consumers, put items into the buffer (producers) and take items out of the buffer (consumers).

A producer cannot put something in the buffer until the buffer has space available. A consumer cannot take something out of the buffer until the producer has written to the buffer.

 */

public class ProducerConsumer {
    public static void main(String[] args) {
        Worker worker = new Worker(0, 5);

        Thread producer = new Thread(() -> {
            try {
                worker.produce();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                worker.consume();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        producer.start();
        consumer.start();
    }
}

class Worker {
    private int counter = 0;
    private final Integer minSize;
    private final Integer maxSize;
    private final List<Integer> container;
    private final Object lock = new Object();

    public Worker(Integer minSize, Integer maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.container = new ArrayList<>();
    }

    public void produce() throws InterruptedException {
        synchronized (lock) {
            while (true) {
                if (container.size() == maxSize) {
                    System.out.println("Container full, waiting for items to be removed...");
                    lock.wait();
                } else {
                    System.out.println(counter + " Added to the container");
                    container.add(counter++);
                    lock.notify();
                }
                Thread.sleep(500);
            }
        }
    }

    public void consume() throws InterruptedException {
        synchronized (lock) {
            while (true) {
                if (container.size() == minSize) {
                    System.out.println("Container empty, waiting for items to be added...");
                    lock.wait();
                } else {
                    System.out.println(container.remove(0) + " Removed from the container");
                    lock.notify();
                }
                Thread.sleep(500);
            }
        }
    }
}
