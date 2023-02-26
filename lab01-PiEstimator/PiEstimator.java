
/**
 * Description: <describe your program here>
 *
 * @author <your name here>
 */

public class PiEstimator {
    long numPoints;
    int numTreads;

    //assigns the class variables their values from when it was instantiated
    public PiEstimator (long _numPoints, int _numThreads) {
	   this.numPoints = _numPoints;
       this.numTreads = _numThreads;
    }

    //creates the threads, provide each with a shared array, and id and the number of samples they should run

    public double getPiEstimate (){
        long samples_per_thread = numPoints / numTreads;
        long [] sharedArray = new long [numTreads];
        Thread [] threads = new Thread[numTreads];

        for (int i = 0; i < numTreads; i++) {
            threads[i] = new Thread(new PiThread(samples_per_thread,sharedArray, i)); //get explanation for why two constructors
            threads[i].start();
        }
        for ( Thread thread: threads) {
            try {thread.join();
            } catch (InterruptedException e) {

            }

        }
        double estimate = 0;

        for (int i = 0; i < sharedArray.length; i++) {
            estimate = estimate + sharedArray[i];
        }

        //divides the total estimates by the number of points to get the probablity and multiples by the length of a side of the square to isolate pi
        estimate = estimate/numPoints;
        return estimate*4;
        }

}

