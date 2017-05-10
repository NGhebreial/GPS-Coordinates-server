package controllers;

import gpgga.GpggaBox;
import gpgga.GpggaReceiver;
import math.CoordsCalculator;
import math.SpeedCalculator;
import math.SpeedChecker;
import utils.Orientation;
import utils.MessageBox;
import math.UTMConverter;
import models.DataPoint;
import views.MapViewer;
import views.SpeedViewer;

import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class Controller extends Thread {

	private MapViewer map;
	private GpggaReceiver<GpggaBox> receiver;

	private GpggaBox box;

	private CoordsCalculator coordsCalc;

	private Semaphore semaphoreMap;
	private Semaphore semaphoreSpeed;
	
	private SpeedCalculator speed;
	
	private SpeedViewer speedViewer;
	private SpeedChecker speedChecker;

	public Controller(String addr, int port){
		semaphoreMap = new Semaphore( 0 );
		semaphoreSpeed = new Semaphore( 0 );
	    doMap(semaphoreMap);
	    doSpeedViewer(semaphoreSpeed);
		box = new GpggaBox();
		receiver = new GpggaReceiver<GpggaBox>(addr, port, box);
		speed = new SpeedCalculator();
		isSpeedCheckerReady();
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
				refreshSpeedView(utm);
				//Refresh map
				coordsCalc = initCoords();
				HashMap<String, Integer> coodsCalculated = coordsCalc.translatetoInt(utm.getUMTNorting(), true, utm.getUMTEasting(), utm.isWestLongitude());
				map.drawPointer(coodsCalculated.get("x"), coodsCalculated.get("y"));
			}
		});
        try {
        	semaphoreSpeed.acquire();
            semaphoreMap.acquire();
            receiver.start();
        }catch( InterruptedException e ) {
            e.printStackTrace();
        }
	}
	
	private void refreshSpeedView(UTMConverter utm){
		if( isSpeedCheckerReady() ){
			DataPoint calculateSpeed = speed.calculateSpeed(utm);
			//Refresh speed view
			System.out.println("calculateSpeed.getSpeed() "+calculateSpeed.getSpeed());
			if( calculateSpeed.getSpeed() > 0.0 ){
				double recomendedSpeed = speedChecker.getTargetSpeed(calculateSpeed);
				if ( recomendedSpeed != -1 ){
					speedViewer.refreshInfo(calculateSpeed.getSpeed(), recomendedSpeed, calculateSpeed.getOrientation());
				}
			}
		}
		
	}
	
	private boolean isSpeedCheckerReady(){
		boolean isReady = true;
		if ( speedChecker == null ){
			try{
				speedChecker = new SpeedChecker();
			}
			catch(Error e){
				speedChecker = null;
				isReady = false;
				e.printStackTrace();
			}
		}		
		return isReady;
	}

	private void doMap( final Semaphore semaphore ){
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run(){
				map = new MapViewer(semaphore);
			}
		});
	}
	
	private void doSpeedViewer(final Semaphore semaphore ){
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run(){
				speedViewer = new SpeedViewer( semaphore );
			}
		});
	}
}
