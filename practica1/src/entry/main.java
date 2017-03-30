package entry;

import utils.Controller;
import utils.CoordsCalculator;
import utils.GpggaMessage;
import utils.UTMConverter;
import views.MapViewer;

import javax.swing.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class main {

    private static MapViewer map;

    public static void startUI(){
        map = new MapViewer();
    }
    public static void doMap(){
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run(){
                startUI();
            }
        });
    }

    public static void test(){//"$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47"
        String test = "$GPGGA,123519,4807.038,N,01131.000,E,1,08,0.9,545.4,M,46.9,M,,*47";
        GpggaMessage message = new GpggaMessage( test );
        if( !message.isFixedData() ){
            System.out.println("No fixed data!!");
        }else{
        	UTMConverter utm= new UTMConverter(40.0,
                    23.429,
                    03.0,
                    37.619,
                    true);
            System.out.println("Calculated: Norting -> " + utm.getUMTNorting() + " Easting -> " + utm.getUMTEasting());
            double north = utm.getUMTNorting();
            double east = utm.getUMTEasting();
            
        }
    }

    public static CoordsCalculator initCoords(){
    	UTMConverter utm= new UTMConverter(40.392132, 0.0, -3.638561, 0.0, true);
        double imUpX = utm.getUMTNorting();
        double imUpY = utm.getUMTEasting();

        utm= new UTMConverter(40.386382, 0.0, -3.620250, 0.0, true);
        double imDownX = utm.getUMTNorting();
        double imDownY = utm.getUMTEasting();

        CoordsCalculator coords = new CoordsCalculator(imUpX, imUpY, imDownX, imDownY);
        return coords;
    }

    public static double[] getLatitTestPoints(){
        // Test Points
        double[] latit = {
                // From NMEA sample
                48.1173,
                // Left up corner, shall be 0
                40.392132,
                // Right down corner, shall be 0
                40.386382,
                // Random point 1
                40.387960,
                // Random point 2
                40.389340
        };
        return latit;
    }

    public static double[] getLongitTestPoints(){
        double[] longit = {
                // From NMEA sample
                -3.18333,
                // Left up corner, shall be 0
                -3.638561,
                // Right down corner, shall be 0
                -3.620250,
                // Random point 1
                -3.62692,
                // Random point 2
                -3.629157
        };
        return longit;
    }

    public static List<Point2D> initTestPoints(double[] latit, double[] longit){
        List<Point2D> pointList = new ArrayList<Point2D>();

        for (int i = 0 ; i < latit.length ; i++) {
        	UTMConverter utm= new UTMConverter( latit[i], 0.0, longit[i], 0.0, true );
            pointList.add( new Point2D.Double( utm.getUMTNorting(), utm.getUMTEasting() ));
        }

        return pointList;
    }

    public static void main( String[] args ){
        // test();
    	Controller c = new Controller("192.168.43.1", 9090);
    	c.start();
    	try {
			c.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        try {
//            double[] latit = getLatitTestPoints();
//            double[] longit = getLongitTestPoints();
//            List<Point2D> pointList = initTestPoints(latit, longit);
//            CoordsCalculator coords = initCoords();
//
//            for (int i = 0 ; i < pointList.size(); i++) {
//                Point2D.Double point = coords.translate(pointList.get( i ).getX(), true, pointList.get( i ).getY(), true);
//                System.out.println( "[" + ( i + 1 ) + "]\t(" + latit[ i ] + "," + longit[ i ] + ") -> " +
//                        (int) ( point.getX() ) + "," + (int) ( point.getY() )
//                );
//                map.drawPointer((int)point.getX(), (int)point.getY());
//            }
//            ready.release();
//        }catch( InterruptedException e ) {
//            e.printStackTrace();
//        }
        // GpggaReceiver receiver = new GpggaReceiver("192.168.1.134", 9090, new GpggaBox());
        // receiver.start();
        // try {
        //     receiver.join();
        // }catch( InterruptedException e ) {
        //     e.printStackTrace();
        // }
    }
}
