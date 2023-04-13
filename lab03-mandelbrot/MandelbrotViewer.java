import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JFrame;

public class MandelbrotViewer extends JPanel{
    public static final int BOX_WIDTH = 1024;
    public static final int BOX_HEIGHT = 1024;

    // various regions you can visualize
    public static final float[] FULL_SET = { -2.0F, 1.0F, -1.5F, 1.5F, 100F, 100F};
    public static final float[] ELEPHANT_VALLEY = { 0.25F, 0.35F, -0.06F, 0.04F, 200F, 100F };
    public static final float[] SEA_HORSE_VALLEY = { -1.0F, -0.5F, -0.1F, 0.4F, 200F, 100F };
    public static final float[] EXPLOSION = { -0.555F, -0.54F, 0.49F, 0.505F, 1000F, 100F };
    public static final float[] ZOOOOM = { -0.3486F, -0.3483F, -0.6067F, -0.6064F, 5000F, 100F };

    
    //////////////////////////////////////////////////////////////
    // SET THIS TO DRAW DIFFERENT REGIONS OF THE MANDELBROT SET //
    //////////////////////////////////////////////////////////////

    public static final float[] CURRENT_REGION = FULL_SET;

    

    
    public static Mandelbrot mandelbrot = new Mandelbrot(CURRENT_REGION);
    
    private float[][] esc = new float[BOX_HEIGHT][BOX_WIDTH];
    private Color[][] bitmap = new Color[BOX_HEIGHT][BOX_WIDTH];


    public MandelbrotViewer(){
        this.setPreferredSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));
    }

    // determine a Color for a (normalized) escape time val (a number
    // between 0.0 and 1.1)
    private Color colorMap (float val) {
	if (val == 0)
	    return new Color(0, 0, 0);

	val = (float) ((Math.exp(val*val) - 1)  / (Math.E - 1));
	
	float r = 0.0F;
	float g = 0.0F;
	float b = 0.0F;
	
	if (val <= 0.25) {
	    r = 0.5F - 2 * val;
	    g = 0.5F - 2 * val;
	    b = 1.0F - val;
	} else if (val <= 0.75) {
	    r = 1.0F - 2 * (0.75F - val);
	    g = 0.9F - 1.8F * (0.75F - val);
	    b = 1.5F * (0.75F - val);
	} else {
	    r = 4.0F * (1.0F - val);
	    g = 3.6F * (1.0F - val);
	    b = 0;		
	}
	
	return new Color(r, g, b);
    }

    // iterate over the pixels of the image and determine the color
    // for each pixel
    private void updateBitmap (float[] region) {
	
	mandelbrot.setAll(region);
	
	// switch to testing the optimized version
	mandelbrot.escapeTimesBaseline(esc);
	//mandelbrot.escapeTimesOptimized(esc);
	
	for (int i = 0; i < BOX_HEIGHT; i++) {
	    for (int j = 0; j < BOX_WIDTH; j++) {		
		bitmap[i][j] = colorMap((mandelbrot.getMaxIter() - esc[i][j]) / mandelbrot.getMaxIter());
	    }
	}	      
    }
        
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

	updateBitmap(CURRENT_REGION);

	for (int i = 0; i < BOX_HEIGHT; i++) {
	    for (int j = 0; j < BOX_WIDTH; j++) {
		g.setColor(bitmap[i][j]);
		g.fillRect(j, BOX_HEIGHT - i, 1, 1);
	    }
	}
    }
    
    public static void main(String args[]){
        JFrame frame = new JFrame("Mandelbrot Set!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new MandelbrotViewer());
        frame.pack();
        frame.setVisible(true);
    }
}
