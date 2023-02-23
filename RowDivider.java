

public class RowDivider implements Runnable{
    SquareMatrix squareMatrix; 
    int row, size;
    float[][] matrix;
    float[][] optimizedShortcuts;

    public RowDivider(SquareMatrix squareMatrix, int row, int size, float [][] optimizedShortcuts){
        this.squareMatrix = squareMatrix;
        this.row = row; 
        this.size = size; 
        this.matrix = squareMatrix.getMatrix(); 
        this.optimizedShortcuts = optimizedShortcuts; 
    }

    public void run() {
        
        for (int column = 0; column < matrix.length; column++) {
            float min = Float.MAX_VALUE;
            for (int k = 0; k < matrix.length; k++) {
                    float x = matrix[row][k];
                    float y = matrix[k][column];
                    float z = x + y;
                    if (z < min) {
                    min = z;
    
                }
            }
            optimizedShortcuts[row][column] = min; 
            
        }
        
    }
}

