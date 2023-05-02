import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

public class ParallelPrimes {
    //public static final int MAX_VALUE = 100;
    public static final int MAX_VALUE = Integer.MAX_VALUE;
    public static final int N_PRIMES = 105_097_565;
    public static final int ROOT_MAX = (int) Math.sqrt(MAX_VALUE);
    public static final int MAX_SMALL_PRIME = 1 << 20;
    public static final String TEAM_NAME = "Daily Mammoth";

    // Use the sieve of Eratosthenes to compute all prime numbers up
    // to max. The largest allowed value of max is MAX_SMALL_PRIME.
    public static int[] getSmallPrimesUpTo(int max) {

        // check that the value max is in bounds, and throw an
        // exception if not
        if (max > MAX_SMALL_PRIME) {
            throw new RuntimeException(
                    "The value " + max + "exceeds the maximum small prime value (" + MAX_SMALL_PRIME + ")");
        }

        // isPrime[i] will be true if and only if i is
        // prime. Initially set isPrime[i] to true for all i >= 2.
        boolean[] isPrime = new boolean[max];

        for (int i = 2; i < max; i++) {
            isPrime[i] = true;
        }

        // Apply the sieve of Eratosthenes to find primes. The
        // procedure iterates over values i = 2, 3,.... If isPrime[i]
        // == true, then i is a prime. When a prime value i is found,
        // set isPrime[j] = false for all multiples j of i. The
        // procedure terminates once we've examined all values i up to
        // Math.sqrt(max).
        // !is it okay to use ceiling here?
        int rootMax = (int) Math.ceil(Math.sqrt(max));
        for (int i = 2; i < rootMax; i++) {
            if (isPrime[i]) {
                for (int j = 2 * i; j < max; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        // Count the number of primes we've found, and put them
        // sequentially in an appropriately sized array.
        int count = trueCount(isPrime);

        int[] primes = new int[count];
        int pIndex = 0;

        for (int i = 2; i < max; i++) {
            if (isPrime[i]) {
                primes[pIndex] = i;
                pIndex++;
            }
        }

        return primes;
    }

    // Count the number of true values in an array of boolean values,
    // arr
    public static int trueCount(boolean[] arr) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i])
                count++;
        }

        return count;
    }

    // Returns an array of all prime numbers up to ROOT_MAX
    public static int[] getSmallPrimes() {
        return getSmallPrimesUpTo(ROOT_MAX);
    }

    // Compute a block of prime values between start and start +
    // isPrime.length. Specifically, after calling this method
    // isPrime[i] will be true if and only if start + i is a prime
    // number, assuming smallPrimes contains all prime numbers of to
    // sqrt(start + isPrime.length).

    private static void primeBlock(boolean[] isPrime, int[] smallPrimes, int start) {

        // initialize isPrime to be all true
        for (int i = 0; i < isPrime.length; i++) {
            isPrime[i] = true;
        }

        for (int p : smallPrimes) {

            // find the next number >= start that is a multiple of p
            int i = (start % p == 0) ? start : p * (1 + start / p);
            i -= start;

            while (i < isPrime.length) {
                isPrime[i] = false;
                i += p;
            }
        }
    }

    // Compute the first primes.length prime numbers and write them
    // sequentially into the array primes.
    public static void optimizedPrimes(int[] primes) {

        // & trying to use the smallPrimes array to find the rest of the primes in
        // max_value

        // compute small prime values
        int[] smallPrimes = getSmallPrimes(); // & array of small primes up to root_max

        for (int count = 0; count < smallPrimes.length; count++) {
            primes[count] = smallPrimes[count]; // & putting the root_max primes into the input array
        }

        // Apply the sieve of Eratosthenes to find primes. This
        // procedure partitions the sieving task up into several
        // blocks, where each block isPrime stores boolean values
        // associated with ROOT_MAX consecutive numbers. Note that
        // partitioning the problem in this way is necessary because
        // we cannot create a boolean array of size MAX_VALUE.

        int nThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        // & next whole number value
        double chunck_size = Math.ceil((MAX_VALUE - ROOT_MAX) / ((double) nThreads));

        boolean[][] isPrime = new boolean[nThreads][(int) chunck_size]; // & each thread has a chunck

        int ID = 0;

        // !need to in later steps account for going passed max_val
        // !curBlock is the starting index for each chunk


        List<Future<boolean[]>> list = new ArrayList<Future<boolean[]>>();
  
        for (double curBlock = ROOT_MAX; curBlock < MAX_VALUE; curBlock += chunck_size) {

            PrimeTask task = new PrimeTask(isPrime[ID], smallPrimes, (int) curBlock, ID);
            Future<boolean[]> res = pool.submit(task);
            list.add(res);

            ID++;

        }
        // !be sure of the order

        int primes_ind = smallPrimes.length;

        for (Future<boolean[]> future : list) {
            try {
                
                boolean[] result = future.get(); // ! how to just get the boolean array itself
                int index = 0;
                 // index for primes array //!check
                int offset = ((list.indexOf(future)) * (int) chunck_size) + ROOT_MAX; // start of chunck

                int max = Math.min(offset + isPrime[0].length, MAX_VALUE); 

                for (int i = offset; i < max; i++) {
                    if(result[index]){
                        primes[primes_ind] = i; 
                        primes_ind++;
                    }
                   
                    
                    index++; 

                }

            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        pool.shutdown();
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {

        }
        System.out.println("end");

    }

}

class PrimeTask implements Callable<boolean[]> {
    int[] smallPrimes;
    boolean[] isPrime;
    int start;
    int index;

    public PrimeTask(boolean[] isPrime, int[] smallPrimes, int start, int index) {
        this.isPrime = isPrime;
        this.smallPrimes = smallPrimes;
        this.start = start;
        this.index = index;

        for (int i = 0; i < isPrime.length; i++) {
            isPrime[i] = true;
        }
    }

    @Override
    public boolean[] call() {

        for (int p : smallPrimes) {

            int i = (start % p == 0) ? start : p * (1 + start / p);
            i -= start;

            while (i < isPrime.length) {
                isPrime[i] = false;
                i += p;
            }

        }
        return isPrime;
    }
}