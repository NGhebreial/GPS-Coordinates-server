package entry;

import utils.GpggaMessage;
import utils.UTMConverter;
import views.MapViewer;

import javax.swing.*;

public class main {

    public static void startUI(){
        MapViewer map = new MapViewer();
        // map.drawPointer( 0, 0 );
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
        	UTMConverter.setup(
                    40.0,
                    23.429,
                    03.0,
                    37.619,
                    true
            );
            System.out.println("Calculated: Norting -> " + UTMConverter.getUMTNorting() + " Easting -> " + UTMConverter.getUMTEasting());
        }
    }

    public static void main( String[] args ){
        test();
        // doMap();
        // GpggaReceiver receiver = new GpggaReceiver("192.168.1.134", 9090, new GpggaBox());
        // receiver.start();
        // try {
        //     receiver.join();
        // }catch( InterruptedException e ) {
        //     e.printStackTrace();
        // }
    }
}
