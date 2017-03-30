package views;


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.concurrent.Semaphore;

public class MapViewer extends JFrame {

    private JPanel panel;
    private JLabel image;
    private static final int MAX_PAINT_TRACE = 160;
    private int painted;

    public MapViewer(){
        super("Map Preview");

        this.painted = 0;

        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        ImageIcon img = new ImageIcon( "res/upm.png" );

        this.panel = (JPanel)this.getContentPane();
        this.panel.setPreferredSize( new Dimension(img.getIconWidth(), img.getIconHeight()) );

        this.image = new JLabel();
        System.out.println("Im with width: " + img.getIconWidth() + " height: " + img.getIconHeight());
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
        this.image.getGraphics().fillOval( x, y, 100, 100 );
    }

}
