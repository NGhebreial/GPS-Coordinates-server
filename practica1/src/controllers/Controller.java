package controllers;

import gpgga.GpggaBox;
import gpgga.GpggaReceiver;
import math.CoordsCalculator;
import math.Point;
import math.SpeedChecker;
import math.UTMConverter;
import utils.MessageBox;
import views.MapViewer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class Controller extends Thread {

	private MapViewer map;
	private GpggaReceiver<GpggaBox> receiver;

	private GpggaBox box;

	private CoordsCalculator coordsCalc;

	private Semaphore semaphore;

	public Controller(String addr, int port){
		semaphore = new Semaphore( 0 );
	    doMap(semaphore);
		box = new GpggaBox();
		// receiver = new GpggaReceiver<GpggaBox>(addr, port, box);
	}
	
	public CoordsCalculator initCoords(){
		UTMConverter utm = new UTMConverter();
		utm.setup(40.387835, 0.0, 3.633741, 0.0, true);
		//utm.setup(40.392132, 0.0, 3.638561, 0.0, true);
		double imUpX = utm.getUMTNorting();
		double imUpY = utm.getUMTEasting();
        utm.setup(40.385732, 0.0, 3.630539, 0.0, true);
		//utm.setup(40.386382, 0.0, 3.620250, 0.0, true);
		double imDownX = utm.getUMTNorting();
		double imDownY = utm.getUMTEasting();

		return new CoordsCalculator(imUpX, imUpY, imDownX, imDownY, map.getImWidth(), map.getHeight());
	}
	
	@Override
	public void run() {
		box.setChain(new MessageBox() {

			@Override
			public void call(Object data) {
				//Received data in UTM format
				UTMConverter utm = (UTMConverter) data;
				coordsCalc = initCoords();
				HashMap<String, Integer> coodsCalculated = coordsCalc.translatetoInt(utm.getUMTNorting(), true, utm.getUMTEasting(), utm.isWestLongitude());
				map.drawPointer(coodsCalculated.get("x"), coodsCalculated.get("y"));
			}
		});
        try {
            System.out.println("Acquire");
            semaphore.acquire();
            System.out.println("Released");
            // receiver.start();
            doPaint();
        }catch( InterruptedException e ) {
            e.printStackTrace();
        }
	}

	private void doPaint(){
        try {
            Thread.sleep( 1000 );
        }catch( InterruptedException e ) {
            e.printStackTrace();
        }
        SpeedChecker sp = new SpeedChecker();
        ArrayList<ArrayList<Point[]>> mapPoints = sp.getMap();
        CoordsCalculator coords = initCoords();
        for( ArrayList<Point[]> row : mapPoints ){
            System.out.println("Row ");
            for( Point[] points: row ){
                for( int i = 0; i < points.length; i++ ) {
                    HashMap<String, Integer> coodsCalculated = coords.translatetoInt(
                            points[i].getLatitude(), true,
                            points[i].getLongitude(), true
                    );
                    map.drawPointer(coodsCalculated.get("x"), coodsCalculated.get("y"));
                }
            }
        }
    }

	private void doMap( final Semaphore semaphore){
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run(){
				map = new MapViewer(semaphore);
			}
		});
	}
}
