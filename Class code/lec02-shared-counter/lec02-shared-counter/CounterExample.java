public class CounterExample {
    public static int NUM_THREADS = 1;
    public static long TOTAL_INCREMENTS = 100_000_000;
    public static long TIMES_PER_THREAD = TOTAL_INCREMENTS / NUM_THREADS;

    public static void main (String[] args) {
	
	// create a Counter instance
	// this will be shared between the threads
	Counter counter = new Counter();

	// create and initialize an array of Threads
	Thread[] threads = new Thread[NUM_THREADS];
	for (int i = 0; i < NUM_THREADS; i++) {
	    
	    // each Thread has its own instance of CounterThread
	    threads[i] = new Thread(new CounterThread(counter, TIMES_PER_THREAD));
	    
	}

	System.out.println("Starting to count...");
	long start = System.nanoTime();

	// start all threads
	for (Thread t : threads)
	    t.start();

	// wait for all threads to complete before continuing
	for (Thread t : threads) {
	    try {
		t.join();
	    }
	    catch (InterruptedException e) {
		// ignore the case that a thread coule be interrupted
	    }
	}

	long stop = System.nanoTime();

	System.out.println("Finished counting!\n" +
			   "That took " + (stop - start) / 1_000_000 + "ms.\n" +
			   "Expected final count: " + NUM_THREADS * TIMES_PER_THREAD + "\n" +
			   "Actual final count: " + counter.getCount());
    }
}
