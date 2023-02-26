import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math;

public class PiThread implements Runnable {
    private long [] myArray;
    private int myId;
    private long mySamples;
    public PiThread(long mySamples, long [] myArray, int id){
        this.myId = id;
        this.mySamples = mySamples;
        this.myArray = myArray;
    }

    @Override
    public void run() {
       Double pointx;
       Double pointy;
       int radius = 1;
       int hits = 0;
// simmulates random dart throwing by creating a coordiate pair grid of possible values and randomly selecting a pair
        for (int i = 0; i < mySamples; i++) {
            pointx = ThreadLocalRandom.current().nextDouble(-1,1);
            pointy = ThreadLocalRandom.current().nextDouble(-1,1);
//applies the distance formula to determine if the point is within the circle if the formula yeilds a value less than or equal to the radius of the circle, and outside the circle otherwise
            if(Math.sqrt((pointx*pointx) + (pointy*pointy)) <= 1) hits++;
        }

        myArray[myId] = hits;
    }
}
