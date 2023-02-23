public class ShortcutTester {
    // use these values for testing on your personal computer
    public static final int NUM_TESTS = 4;
    public static final int[] TEST_SIZES = {128, 256, 512, 1024, 2048};
    

    // try these values for testing on the HPC cluster
    // public static final int NUM_TESTS = 1;
    // public static final int[] TEST_SIZES = {128, 256, 512, 1024, 2048, 4096};

    public static void runTest (int size) {
	long avg = 0;
	long bavg = 0;
	long iterPerUS;
	long start, stop, bstart, bstop;
	boolean passed = true;

	for (int i = 0; i < NUM_TESTS; ++i) {
	    // generate a random square matrix of size size
	    SquareMatrix sm = new SquareMatrix(size);

	    // generate baseline shortcut matrix
	    bstart = System.nanoTime();
	    SquareMatrix baseline = sm.getShortcutMatrixBaseline();
	    bstop = System.nanoTime();

	    // generated optimized shortcut matrix, and time it
	    start = System.nanoTime();
	    SquareMatrix optimized = sm.getShortcutMatrixOptimized();
	    stop = System.nanoTime();



	    // verify that baseline and optimized solution are the same
	    if (!optimized.equals(baseline)) {
		passed = false;
	    }

	    bavg += (bstop - bstart);
	    avg += (stop - start);
	}

	
	avg /= NUM_TESTS;
	bavg /= NUM_TESTS;

	// runtime improvement over baseline
	double improvement = (double) bavg / avg;

	// iterations per microsecond
	iterPerUS = (long) size * size * size / (avg / 1000);

	String isPassed = (passed) ? "yes" : " no";

	System.out.printf("| %4d |           %6d |      %6.2f |        %9d |     %s |\n", size, avg / 1_000_000, improvement, iterPerUS, isPassed);
    }
    
    public static void main (String[] args) {
	System.out.printf("|------|------------------|-------------|------------------|---------|\n" +
			  "| size | avg runtime (ms) | improvement | iteration per us | passed? |\n" +
			  "|------|------------------|-------------|------------------|---------|\n");

	for (int size : TEST_SIZES) {
	    runTest(size);
	}

	System.out.printf("|------|------------------|-------------|------------------|---------|\n");
    }
}
