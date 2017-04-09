package views;


import javax.swing.*;
import java.awt.*;

public class MapViewer extends JFrame {

    private JPanel panel;
    private JLabel image;
    private static final int MAX_PAINT_TRACE = 160;
    private int painted;

    private int imWidth;
    private int imHeight;

    public MapViewer(){
        super("Map Preview");

        this.painted = 0;

        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        ImageIcon img = new ImageIcon( "res/upm.png" );

        this.panel = (JPanel)this.getContentPane();
        this.panel.setPreferredSize( new Dimension(img.getIconWidth(), img.getIconHeight()) );

        this.image = new JLabel();
        System.out.println("Im with width: " + img.getIconWidth() + " height: " + img.getIconHeight());
        this.imWidth = img.getIconWidth();
        this.imHeight = img.getIconHeight();
        this.image.setIcon( img );
        this.panel.add( image );


        this.pack();
        this.setVisible( true );
    }

    public void drawPointer(int x, int y){
        if( this.painted++ == MAX_PAINT_TRACE ){
            this.panel.repaint();
            this.painted = 0;
        }
        this.image.getGraphics().fillOval( x, y, 15, 15 );
        System.out.println("Painting at " + x + ", " + y);
    }

    public int getImWidth(){
        return imWidth;
    }

    public int getImHeight(){
        return imHeight;
    }

}
