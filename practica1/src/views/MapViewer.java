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
    private Semaphore readyState;

    public MapViewer( Semaphore ready ){
        super("Map Preview");

        this.readyState = ready;

        this.painted = 0;

        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        ImageIcon img = new ImageIcon( "res/upm.png" );

        this.panel = (JPanel)this.getContentPane();
        this.panel.setPreferredSize( new Dimension(img.getIconWidth(), img.getIconHeight()) );

        this.image = new JLabel();
        System.out.println("Im with width: " + img.getIconWidth() + " height: " + img.getIconHeight());
        this.image.setIcon( img );
        this.panel.add( image );

        this.image.addMouseMotionListener( new MouseMotionAdapter() {
            @Override
            public void mouseMoved( MouseEvent mouseEvent ){
                if( readyState.availablePermits() == 0 ){
                    readyState.release();
                }
                //drawPointer( mouseEvent.getX(), mouseEvent.getY() );
            }
        });

        this.pack();
        this.setVisible( true );
    }

    public void drawPointer(int x, int y){
        if( this.painted++ == MAX_PAINT_TRACE ){
            this.panel.repaint();
            this.painted = 0;
        }
        this.image.getGraphics().fillOval( x, y, 10, 10 );
    }

}
