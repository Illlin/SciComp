import java.awt.* ;
import javax.swing.* ;

public class HPP {


    final static int NX = 80, NY = 60 ;  // Lattice dimensions
    final static int q = 4 ;  // population
     
    final static int NITER = 10000 ;
    final static int DELAY = 500 ;

    final static double DENSITY = 0.4 ;  // initial state, between 0 and 1.0.

    static Display display = new Display() ;

    static int [] [] fin = new int [NX] [NY] ;
    static int [] [] fout = new int [NX] [NY] ;

    public static void main(String args []) throws Exception {

        // initialize - populate a subblock of grid
        for(int i = 0; i < NX/4 ; i++) { 
            for(int j = 0; j < NY/4 ; j++) { 

                int p = 0;
                for(int d = 0 ; d < q ; d++) {
                    if(Math.random() < DENSITY) {
                        p += 1 << d;
                    }
                }
                
                fin [i] [j] = p;
            }
        }


        display.repaint() ;
        Thread.sleep(DELAY) ;

        for(int iter = 0 ; iter < NITER ; iter++) {

            // Collision

            for(int i = 0; i < NX ; i++) { 
                for(int j = 0; j < NY ; j++) { 
                    int fin_ij = fin [i] [j] ;

                    // Verticle Collision
                    if (fin_ij == 3) {      // 1100
                        fout [i] [j] = 12;  // 0011
                    }
                    // Horisontal Collision
                    else if (fin_ij == 12){ // 0011
                        fout [i] [j] = 3;   // 1100
                    }
                    else {
                        // default, no collisions case:
                        fout [i] [j] = fin_ij ;
                    }
                }
            }

            // Streaming

            for(int i = 0; i < NX ; i++) { 
                int iP1 = (i + 1) % NX ;
                int iM1 = (i - 1 + NX) % NX ;
                for(int j = 0 ; j < NY ; j++) { 
                    int jP1 = (j + 1) % NY ;
                    int jM1 = (j - 1 + NY) % NY ;
                    fin [i] [j] = 
                        (fout [iP1] [j]   & 1) |
                        (fout [iM1] [j]   & 2) |
                        (fout [i]   [jP1] & 4) |
                        (fout [i]   [jM1] & 8) ;
                }
            }

            System.out.println("iter = " + iter) ;
            display.repaint() ;
      
            Thread.sleep(DELAY) ;
        }
    }

    
    static class Display extends JPanel {

        final static int CELL_SIZE = 14 ; 

        public static final int ARROW_START = 2 ;
        public static final int ARROW_END   = 7 ;
        public static final int ARROW_WIDE  = 3 ;

        Display() {

            setPreferredSize(new Dimension(CELL_SIZE * NX, CELL_SIZE * NY)) ;

            JFrame frame = new JFrame("HPP");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(this);
            frame.pack();
            frame.setVisible(true);
        }

        public void paintComponent(Graphics g) {

            g.setColor(Color.WHITE) ;
            g.fillRect(0, 0, CELL_SIZE * NX, CELL_SIZE * NY) ;

            g.setColor(Color.PINK) ;
            //g.setColor(Color.LIGHT_GRAY) ;
            for(int i = 0 ; i < NX ; i++) {
                for(int j = 0 ; j < NY ; j++) {
                    int originX = CELL_SIZE * i + CELL_SIZE/2 ;
                    int originY = CELL_SIZE * j + CELL_SIZE/2 ;
                    g.fillOval(originX - 2, originY - 2, 4, 4) ;
                }
            } 

            g.setColor(Color.BLUE) ;
            int [] tri_x = new int [3], tri_y = new int [3] ;
            for(int i = 0 ; i < NX ; i++) {
                for(int j = 0 ; j < NY ; j++) {
                    int fin_ij = fin [i] [j] ;

                    int originX = CELL_SIZE * i + CELL_SIZE/2 ;
                    int originY = CELL_SIZE * j + CELL_SIZE/2 ;
                    if((fin_ij & 1) == 1) {
                        tri_x [0] = originX - ARROW_START ;
                        tri_x [1] = originX - ARROW_START ;
                        tri_x [2] = originX - ARROW_END ;
                        tri_y [0] = originY - ARROW_WIDE ;
                        tri_y [1] = originY + ARROW_WIDE ;
                        tri_y [2] = originY ;
                        //g.setColor(Color.BLUE) ;
                        g.fillPolygon(tri_x, tri_y, 3) ;
                    }
                    if((fin_ij & 2) == 2) {
                        tri_x [0] = originX + ARROW_START ;
                        tri_x [1] = originX + ARROW_START ;
                        tri_x [2] = originX + ARROW_END ;
                        tri_y [0] = originY - ARROW_WIDE ;
                        tri_y [1] = originY + ARROW_WIDE ;
                        tri_y [2] = originY ;
                        //g.setColor(Color.RED) ;
                        g.fillPolygon(tri_x, tri_y, 3) ;
                    }
                    if((fin_ij & 4) == 4) {
                        tri_x [0] = originX - ARROW_WIDE ;
                        tri_x [1] = originX + ARROW_WIDE ;
                        tri_x [2] = originX  ;
                        tri_y [0] = originY - ARROW_START ;
                        tri_y [1] = originY - ARROW_START ;
                        tri_y [2] = originY - ARROW_END ;
                        //g.setColor(Color.GREEN) ;
                        g.fillPolygon(tri_x, tri_y, 3) ;
                    }
                    if((fin_ij & 8) == 8) {
                        tri_x [0] = originX - ARROW_WIDE ;
                        tri_x [1] = originX + ARROW_WIDE ;
                        tri_x [2] = originX  ;
                        tri_y [0] = originY + ARROW_START ;
                        tri_y [1] = originY + ARROW_START ;
                        tri_y [2] = originY + ARROW_END ;
                        //g.setColor(Color.YELLOW) ;
                        g.fillPolygon(tri_x, tri_y, 3) ;
                    }
                }
            } 
        }
    }
}