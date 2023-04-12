/*
 * Note that this program uses the Java Vector API, which is only
 * available as an incubator feature in Java 16+. In order to compile
 * and run the program you need to use flag '--add-modules
 * jdk.incubator.vector'. For example:
 *
 * > javac --add-modules jdk.incubator.vector Mandelbrot.java
 *
 * In order to run any program that uses the Mandelbrot class, you
 * similarly have to add the same flag to the java command, e.g.:
 *
 * > java --add-modules jdk.incubator.vector MandelbrotTester
 */

import jdk.incubator.vector.*;
import java.util.Arrays;

// A class for computing the Mandelbrot set and escape times for
// points in the complex plane
public class Mandelbrot {

    // the maximum number of iterations of the system to consider
    private float maxIter = 100.0F;

    // the squared distance to the origin for escape
    private float maxSquareModulus = 100.0F;

    // coordinates of the region to be computed
    private float xMin, xMax, yMin, yMax;

    static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

    public Mandelbrot() {

    }

    public Mandelbrot(float maxIter, float maxSquareModulus) {
        this.maxIter = maxIter; // if I get pass this bound, I am in the mandelbrot set __> bounded
        this.maxSquareModulus = maxSquareModulus; // if I am passed this value, I am not in mandelbrot set --->
                                                  // unbounded
    }

    public Mandelbrot(float[] params) {
        setAll(params);
    }

    public float getMaxIter() {
        return maxIter;
    }

    // set the region to be considered from points
    public void setRegion(float xMin, float xMax, float yMin, float yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    // set the region to be considered from an array of coordinates
    public void setRegion(float[] coords) {
        this.xMin = coords[0];
        this.xMax = coords[1];
        this.yMin = coords[2];
        this.yMax = coords[3];
    }

    public void setIterAndModulus(float maxIter, float maxSquaredModulus) {
        this.maxIter = maxIter;
        this.maxSquareModulus = maxSquareModulus;
    }

    // set all parameters; the first four values in params are
    // interpreted as coordinates of the region, while params[4] and
    // params[5] are the maximum iterations and maximum squared
    // modulus, respectively
    public void setAll(float[] params) {
        setRegion(params);
        setIterAndModulus(params[4], params[5]);
    }

    // a baseline implementation of computing escape times for the
    // current region
    // esc is a 2d array to record the escape times,
    // where the first index records rows of the region, and the
    // second index is the column number
    public void escapeTimesBaseline(float[][] esc) {
        float xStep = (xMax - xMin) / esc[0].length;
        float yStep = (yMax - yMin) / esc.length;

        for (int i = 0; i < esc.length; i++) {
            for (int j = 0; j < esc[0].length; j++) {
                int iter = 0;
                float cx = xMin + j * xStep; // column
                float cy = yMin + i * yStep; // row

                float zx = 0;
                float zy = 0;

                while (iter < maxIter && zx * zx + zy * zy < maxSquareModulus) {
                    float z = zx * zx - zy * zy + cx;
                    zy = 2 * zx * zy + cy;
                    zx = z;

                    iter++;
                }

                esc[i][j] = iter;
            }
        }
        /*
         * right now this is only computing whether or not one point is in the set
         */
    }

    // an optimized implementation of escapeTimesBaseline that uses
    // vector operations
    public void escapeTimesOptimized(float[][] esc) {
        float xStep = (xMax - xMin) / esc[0].length;
        float yStep = (yMax - yMin) / esc.length;
        // create array of one piece of complex number
        float[] cx_arr = new float[esc[0].length];

        for (int j = 0; j < cx_arr.length; j++) {
            float cx = xMin + j * xStep;
            cx_arr[j] = cx; // avoid making these arrrays
        }

        // create another array of one piece of complex number
        float[] cy_arr = new float[esc.length];

        for (int i = 0; i < cy_arr.length; i++) {
            float cy = yMin + i * yStep;
            cy_arr[i] = cy;
        
        }
       
        VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
        int step = SPECIES.length();
        int boundc = SPECIES.loopBound(cx_arr.length);
    

        for (int i = 0; i < esc.length; i++) {
            for (int j = 0; j < boundc; j += step) { // ! grab the left-overs
                var va = FloatVector.broadcast(SPECIES, cy_arr[i]); // row
                var vb = FloatVector.fromArray(SPECIES, cx_arr, j); // column
                var zx = FloatVector.broadcast(SPECIES, 0);
                var zy = FloatVector.broadcast(SPECIES, 0);
                var z = FloatVector.broadcast(SPECIES, 0);
        
                var vIter = FloatVector.broadcast(SPECIES, 0);

                var vfMask = vIter.compare(VectorOperators.LT, maxIter); // comparing each lane to maxIter

                var vs1 = zx.mul(zx);
                var vs2 = zy.mul(zy);
                var vs3 = vs1.add(vs2);
                var vsMask = vs3.compare(VectorOperators.LT, maxSquareModulus);

            
                while (vsMask.trueCount() > 0 && vfMask.trueCount() > 0) {
                    
                    var vcMask = vfMask.and(vsMask);
                    vIter = vIter.add(1, vcMask); // updating each lane if the mask was true
                    z = vs1.sub(vs2, vcMask).add(vb, vcMask); //!check substract 
                    zy = zx.mul(2, vcMask).mul(zy, vcMask).add(va, vcMask);
                    zx.blend(z, vcMask); /// !how to use a mask here
                    vfMask = vIter.compare(VectorOperators.LT, maxIter); // comparing each lane to maxIter
                    vs1 = zx.mul(zx);
                    vs2 = zy.mul(zy);
                    vs3 = vs1.add(vs2);
                    vsMask = vs3.compare(VectorOperators.LT, maxSquareModulus);
                
                }
                System.out.println(vIter);
                float [] res = vIter.toArray(); 
                
            //     for (int index = 0; index < step; index++) {
            //         esc[i][j + index] = res[index]; 
            //     }

            // }
            // for (int j = boundc; j < esc[0].length; j++) { // ! grab the left-overs
            //     int iter = 0;
            //     float zx_s = 0;
            //     float zy_s = 0;

            //     while (iter < maxIter && zx_s * zx_s + zy_s * zy_s < maxSquareModulus) {
            //         float z_s = zx_s * zx_s - zy_s * zy_s + cx_arr[j];
            //         zy_s = 2 * zx_s * zy_s + cy_arr[i];
            //         zx_s = z_s;

            //         iter++;
            //     }

            //     esc[i][j] = iter;
                }


        }

        

        


        ////////////////////////////////////////////////////////////
        // COMPLETE THIS METHOD
        ////////////////////////////////////////////////////////////

    }
}
