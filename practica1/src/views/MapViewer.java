package views;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
        ImageIcon img = new ImageIcon( "./res/insia_speed.png" );
        this.imWidth = img.getIconWidth();
        this.imHeight = img.getIconHeight();
        
        this.panel = (JPanel)this.getContentPane();
        
        this.panel.setPreferredSize( new Dimension(imWidth, imHeight) );
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

        this.setLocation(0, (int) (dimension.getHeight() / 2 - imHeight/ 2) );
        
        this.image = new JLabel();
        System.out.println("Im with width: " + imWidth + " height: " + imHeight);
        this.image.setIcon( img );
        this.panel.add( image );       
        this.pack();
        this.setVisible( true ); 
        semaphore.release();
    }
    
    public void refreshImage(BufferedImage imgURL){
    	ImageIcon img = new ImageIcon( imgURL );
        this.imWidth = img.getIconWidth();
        this.imHeight = img.getIconHeight();
        
        this.panel = (JPanel)this.getContentPane();
        
        this.panel.setPreferredSize( new Dimension(imWidth, imHeight) );
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.pack();
        this.setVisible( true ); 
        this.setLocation(0, (int) (dimension.getHeight() / 2 - imHeight/ 2) );
        System.out.println("Im with width: " + imWidth + " height: " + imHeight);
        this.image.setIcon( img );
    }

    public void drawPointer(int x, int y){

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
