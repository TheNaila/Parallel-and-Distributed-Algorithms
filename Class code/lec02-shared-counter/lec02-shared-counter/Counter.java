/*
 * Counter.java a simple class to keep track of a count.
 */

public class Counter {
    private long count = 0;

    // return the current counter value
    public long getCount () { return count; }

    // increment the counter
    public void increment () { ++count; }

    // reset the counter value to 0
    public void reset () { count = 0; }
}
