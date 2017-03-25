package views;


import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class MapViewer extends JFrame {

    private JPanel panel;
    private JLabel image;
    private static final int MAX_PAINT_TRACE = 1;
    private int painted;
    
    public MapViewer(){
        this(0,0);        
    }
    
    public MapViewer(int x, int y){
    	super("Map Preview");

        this.painted = 0;

        this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        this.panel = (JPanel)this.getContentPane();

        this.image = new JLabel();
        ImageIcon img = new ImageIcon( "res/upm.png" );
        System.out.println("Im with width: " + img.getIconWidth() + " height: " + img.getIconHeight());
        this.image.setIcon( img );
        this.panel.add( image );

//        this.image.addMouseMotionListener( new MouseMotionAdapter() {
//            @Override
//            public void mouseMoved( MouseEvent mouseEvent ){
//            drawPointer( mouseEvent.getX(), mouseEvent.getY() );
//            }
//        });
        this.pack();
        this.setVisible( true );
        drawPointer(x, y);
    }
    public void drawPointer(int x, int y){
    	//Before paint the
        if( this.painted++ == MAX_PAINT_TRACE ){
            this.panel.repaint();
            this.painted = 0;
        }
        System.out.println("DRAW "+x+" "+y);
        this.image.getGraphics().fillOval( x, y, 10, 10 );
    }

}
