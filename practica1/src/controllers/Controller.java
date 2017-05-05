package controllers;

import gpgga.GpggaBox;
import gpgga.GpggaReceiver;
import math.CoordsCalculator;
import math.SpeedCalculator;
import utils.MessageBox;
import math.UTMConverter;
import models.DataPoint;
import views.MapViewer;

import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class Controller extends Thread {

	private MapViewer map;
	private GpggaReceiver<GpggaBox> receiver;

	private GpggaBox box;

	private CoordsCalculator coordsCalc;

	private Semaphore semaphore;
	
	private SpeedCalculator speed;

	public Controller(String addr, int port){
		semaphore = new Semaphore( 0 );
	    doMap(semaphore);
		box = new GpggaBox();
		receiver = new GpggaReceiver<GpggaBox>(addr, port, box);
		speed = new SpeedCalculator();
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
				DataPoint calculateSpeed = speed.calculateSpeed(utm);
				System.out.println(calculateSpeed);
				coordsCalc = initCoords();
				HashMap<String, Integer> coodsCalculated = coordsCalc.translatetoInt(utm.getUMTNorting(), true, utm.getUMTEasting(), utm.isWestLongitude());
				map.drawPointer(coodsCalculated.get("x"), coodsCalculated.get("y"));
			}
		});
        try {
            semaphore.acquire();
            receiver.start();
        }catch( InterruptedException e ) {
            e.printStackTrace();
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
