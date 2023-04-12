import java.util.Arrays;

public class MandelbrotTester {
    // basic parameters
    private static final int SIZE = 10;    // the size of the grid computed
    private static final int WARMUP = 10;     // number of warmup iterations
    private static final int NUM_TESTS = 1;  // number of test iterations


    //////////////////////////////////////////////////////////////
    // parameters for interesting regions of the Mandelbrot set //
    //////////////////////////////////////////////////////////////

    public static final float[] FULL_SET = { -2.0F, 1.0F, -1.5F, 1.5F, 100F, 100F};
    public static final float[] ELEPHANT_VALLEY = { 0.25F, 0.35F, -0.06F, 0.04F, 200F, 100F };
    public static final float[] SEA_HORSE_VALLEY = { -1.0F, -0.5F, -0.1F, 0.4F, 200F, 100F };
    public static final float[] EXPLOSION = { -0.555F, -0.54F, 0.49F, 0.505F, 1000F, 100F };
    public static final float[] ZOOOOM = { -0.3486F, -0.3483F, -0.6067F, -0.6064F, 5000F, 100F };


    //////////////////////////////////////////////////////////////
    // regions for correctness and performance tests            //
    //////////////////////////////////////////////////////////////

    public static final float[][] CORR_TESTS = { FULL_SET, SEA_HORSE_VALLEY };
    public static final float[][] PERF_TESTS = { FULL_SET, ELEPHANT_VALLEY, SEA_HORSE_VALLEY, EXPLOSION, ZOOOOM };
    public static final String[] PERF_TEST_NAMES = { "full set", "elephant valley", "sea horse valley", "explosion", "zoooom"};

    private static Mandelbrot mandelbrot = new Mandelbrot(FULL_SET);
        
    private static float[][] besc;
    private static float[][] vesc;

    // check that baseline and optimized procedures return the same escape times
    public static void correctnessTest() {
	for (float[] test : CORR_TESTS) {
	    mandelbrot.setAll(test);
	    mandelbrot.escapeTimesBaseline(besc);
	    mandelbrot.escapeTimesOptimized(vesc);
	    for (int i = 0; i < SIZE; i++) {
		for (int j = 0; j < SIZE; j++) {
		    if (besc[i][j] != vesc[i][j]) {
			System.out.println("Correctness test failed.\n" +
					   "params " + Arrays.toString(test) + "\n" +
					   "i = " + i + ", j = " + j + "\n" +
					   "besc = " + besc[i][j] + ", " +
					   "vesc = " + vesc[i][j]);
			return;
		    }
		}
	    }   
	}

	System.out.println("Correcntess test passed.");
    }

    // test the running time for the baseline implementation for given params
    public static final long performanceTestBaseline(float[][] besc, float[] params) {
	
	mandelbrot.setAll(params);
	
	for (int i = 0; i < WARMUP; i++) {
	    mandelbrot.escapeTimesBaseline(besc);
	}

	long start = System.nanoTime();

	for (int i = 0; i < NUM_TESTS; i++) {
	    mandelbrot.escapeTimesBaseline(besc);
	}

	return System.nanoTime() - start;	
    }

    // test the running time for the optimized implementation for given params
    public static final long performanceTestOptimized(float[][] vesc, float[] params) {
	for (int i = 0; i < WARMUP; i++) {
	    mandelbrot.escapeTimesOptimized(vesc);
	}

	long start = System.nanoTime();

	for (int i = 0; i < NUM_TESTS; i++) {
	    mandelbrot.escapeTimesOptimized(vesc);
	}

	return System.nanoTime() - start;
	
    }

    
    public static void main(String[] args) {
	
	besc = new float[SIZE][SIZE];
	vesc = new float[SIZE][SIZE];

	//////////////////////////////////////////////////
	// correctness test
	//////////////////////////////////////////////////

	System.out.println("Running correctness tests...");

	correctnessTest();

	//////////////////////////////////////////////////
	// performance tests
	//////////////////////////////////////////////////

	System.out.println("Running performance tests...");
	System.out.printf("|--------------------|---------------|----------------|-------------|\n" +
			  "|       region       | baseline (ms) | optimized (ms) | improvement |\n" +
			  "|--------------------|---------------|----------------|-------------|\n");
	
	for (int i = 0; i < PERF_TESTS.length; i++) {
	    long baselineTime = performanceTestBaseline(besc, PERF_TESTS[i]);
	    long optimizedTime = performanceTestOptimized(vesc, PERF_TESTS[i]);
	    System.out.printf("| %18s | %11d   | %12d   | %9.2f   |\n", PERF_TEST_NAMES[i], baselineTime / 1_000_000, optimizedTime / 1_000_000, ((float) baselineTime) / optimizedTime);
	}
	System.out.printf("|--------------------|---------------|----------------|-------------|\n");

    }
}
