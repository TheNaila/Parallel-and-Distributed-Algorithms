import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ThreadLocalRandom;

public class Sorting {
    // replace with your 
    public static final String TEAM_NAME = "Daily Mammoth";
    
    /**
     * Sorts an array of doubles in increasing order. This method is a
     * single-threaded baseline implementation.
     *
     * @param data   the array of doubles to be sorted
     */
    public static void baselineSort (float[] data) {
	Arrays.sort(data, 0, data.length);
    }

    /**
     * Sorts an array of doubles in increasing order. This method is a
     * multi-threaded optimized sorting algorithm. For large arrays (e.g., arrays of size at least 1 million) it should be significantly faster than baselineSort.
     *
     * @param data   the array of doubles to be sorted
     */
    public static void parallelSort (float[] data) {

        // replace this with your method!
        ForkJoinPool pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
        SortTask initialTask = new SortTask(data, 0, data.length - 1);
        pool.invoke(initialTask);
	
    }

    /**
     * Determines if an array of doubles is sorted in increasing order.
     *
     * @param   data  the array to check for sortedness
     * @return        `true` if the array is sorted, and `false` otherwise
     */
    public static boolean isSorted (float[] data) {
        double prev = data[0];

        for (int i = 1; i < data.length; ++i) {
            if (data[i] < prev) {
            return false;
            }

            prev = data[i];
        }

        return true;
    }
}

class SortTask extends RecursiveAction {
    float[] A;
    int low;
    int high;
    public SortTask(float[] A, int low, int high) {
        this.A = A;
        this.low = low;
        this.high = high;
    }
    @Override
    protected void compute() {
        if(low < high) {
            int part = partition();
            SortTask left = new SortTask(A, low, part-1);
            SortTask right = new SortTask(A, part + 1, high);
            left.fork();
            right.compute();
            left.join();
        }
    }

    public int partition() {
        float pivot = A[low];
        int leftWall = low;
        for(int j = low + 1; j <= high; j++) {
            if(A[j] <= pivot) {
                leftWall++;
                float temp = A[leftWall];
                A[leftWall] = A[j];
                A[j] = temp;
            }
        }

        //swap leftwall and pivot
        A[low] = A[leftWall];
        A[leftWall] = pivot;

        return leftWall;
    }
}
