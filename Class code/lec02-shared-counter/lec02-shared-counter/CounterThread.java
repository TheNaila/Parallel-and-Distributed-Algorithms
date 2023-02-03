/*
 * CounterThread.java a Runnable object that increments a Counter a
 * prescribed number of times.
 */

public class CounterThread implements Runnable {
    private Counter counter;
    private long times;        // number of times to increment counter

    public CounterThread (Counter counter, long times) {
	this.counter = counter;
	this.times = times;
    }

    public void run () {
	for (long i = 0; i < times; i++) {
	    counter.increment();
	}
    }
}
