import jdk.incubator.vector.*;

public class VectorTest {
    public static void main(String [] args){

        float [] a = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f}; 
        float [] b = {1.0f, 2.0f, 3.0f , 4.0f, 5.0f, 6.0f, 7.0f, 8.0f}; 

        VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;
        int step = SPECIES.length();
        int boundc = SPECIES.loopBound(a.length);

        var a_arr = FloatVector.fromArray(SPECIES, a, 0); 
        var b_arr = FloatVector.fromArray(SPECIES, b, 0); 
 
       
        var mask = a_arr.compare(VectorOperators.LT, 5);
        while(mask.trueCount() > 0 ) {
            System.out.println("The array" + a_arr);
            mask = a_arr.compare(VectorOperators.LT, 5);
            System.out.println("The mask" + mask);
            a_arr = a_arr.add(1,mask); 
            
        }
    }
}
