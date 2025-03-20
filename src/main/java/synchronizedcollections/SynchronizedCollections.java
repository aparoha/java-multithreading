package synchronizedcollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
Ways to make collections thread safe
1. Use Collections.synchronize()
Downsides
-> Coarse grained locking
-> Limited functionality
-> No fail fast iterators
-> Performance overhead
2. Use concurrent collections
 */

public class SynchronizedCollections {
    public static void main(String[] args) {
        //List<Integer> list = new ArrayList<>();
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        Thread one = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                list.add(i);
            }
        });

        Thread two = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                list.add(i);
            }
        });

        one.start();
        two.start();

        try {
            one.join();
            two.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Size of array : " + list.size());
    }
}
