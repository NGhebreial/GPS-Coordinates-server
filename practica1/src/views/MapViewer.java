package views;


import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;

public class MapViewer extends JFrame {

    private JPanel panel;
    private JLabel image;
    private static final int MAX_PAINT_TRACE = Integer.MAX_VALUE-1;
    private int painted;

    private int imWidth;
    private int imHeight;

    public MapViewer( Semaphore semaphore ){
        super("Map Preview");

        this.painted = 0;

        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        ImageIcon img = new ImageIcon( "res/insia.png" );
        
        this.imWidth = img.getIconWidth();
        this.imHeight = img.getIconHeight();
        
        this.panel = (JPanel)this.getContentPane();
        
        this.panel.setPreferredSize( new Dimension(imWidth, imHeight) );

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        this.setLocation((int) ( 0  ),
                (int) (dimension.getHeight() / 2 - imHeight/ 2));
        
        this.image = new JLabel();
        System.out.println("Im with width: " + imWidth + " height: " + imHeight);
        
        this.image.setIcon( img );
        this.panel.add( image );

        this.pack();
        this.setVisible( true );
        semaphore.release();
    }

    public void drawPointer(int x, int y){
        if( this.painted++ == MAX_PAINT_TRACE ){
            this.panel.repaint();
            this.painted = 0;
        }
        this.image.getGraphics().fillOval( x, y, 12, 12 );
        System.out.println("Painting at " + x + ", " + y); 
    }

    public int getImWidth(){
        return imWidth;
    }

    public int getImHeight(){
        return imHeight;
    }

}
