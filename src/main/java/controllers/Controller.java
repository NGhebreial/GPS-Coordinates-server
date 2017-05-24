package controllers;

import gpgga.GpggaBox;
import gpgga.GpggaEmulator;
import gpgga.GpggaMessage;
import gpgga.GpggaReceiver;
import math.CoordsCalculator;
import math.SpeedCalculator;
import math.SpeedChecker;
import math.UTMConverter;
import models.DataPoint;
import server.CoordsServer;
import utils.MessageBox;
import views.MapViewer;
import views.SpeedViewer;

import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class Controller extends Thread {

	private GpggaReceiver<GpggaBox> receiver;
	private GpggaBox box;
	private GpggaEmulator emulator;

	private SpeedCalculator speed;
	private SpeedChecker speedChecker;
	private CoordsServer server;
	private CoordsCalculator coordsCalc;

	private MapViewer map;
	private SpeedViewer speedViewer;
	private Semaphore semaphoreMap;
	private Semaphore semaphoreSpeed;

	private boolean debug;

  public Controller(String addr, int port, int socketPort, boolean dev){
      semaphoreMap = new Semaphore( 0 );
      semaphoreSpeed = new Semaphore( 0 );
      debug = dev;
      doMap(semaphoreMap);
      doSpeedViewer(semaphoreSpeed);
      box = new GpggaBox();
      receiver = new GpggaReceiver<GpggaBox>(addr, port, box);
      speed = new SpeedCalculator();
      server = new CoordsServer( socketPort );
	  emulator = new GpggaEmulator( true, true );
	  isSpeedCheckerReady();
  }

  public CoordsCalculator initCoords(){
		UTMConverter utm = new UTMConverter();
		utm.setup(40.387835, 0.0, 3.633741, 0.0, true);
		//utm.setup(40.392132, 0.0, 3.638561, 0.0, true);
		double imUpX = utm.getUTMNorting();
		double imUpY = utm.getUTMEasting();
        utm.setup(40.385732, 0.0, 3.630539, 0.0, true);
		//utm.setup(40.386382, 0.0, 3.620250, 0.0, true);
		double imDownX = utm.getUTMNorting();
		double imDownY = utm.getUTMEasting();

		return new CoordsCalculator(imUpX, imUpY, imDownX, imDownY, map.getImWidth(), map.getHeight());
	}

	public void setupBox(){
		box.setChain(new MessageBox() {

			@Override
			public void call(Object data) {
				//Received data in UTM format
				UTMConverter utm = (UTMConverter) data;
				DataPoint point = new DataPoint();
				point.setNorting( utm.getUTMNorting() );
				point.setEasting( utm.getUTMEasting() );
				refreshSpeedView(point);
				//Refresh map
				coordsCalc = initCoords();
				HashMap<String, Integer> coodsCalculated = coordsCalc.translatetoInt(utm.getUTMNorting(), true, utm.getUTMEasting(), utm.isWestLongitude());
				map.drawPointer(coodsCalculated.get("x"), coodsCalculated.get("y"));
			}
		});
		box.setRawChain( new MessageBox() {
			@Override
			public void call( Object data ){
				server.apendToBuffer( (GpggaMessage) data );
			}
		});
		emulator.setBox( new MessageBox() {
			@Override
			public void call( Object data ){
				box.call( ((GpggaMessage) data).getRawMsg() );
			}
		});
	}

	@Override
	public void run() {
		if( !debug ){
			receiver.connect();
		}
		setupBox();
		try {
			semaphoreSpeed.acquire();
			semaphoreMap.acquire();
			server.start();
			if( debug ){
				emulator.start();
			}else{
				receiver.start();
			}
		}catch( InterruptedException e ) {
			e.printStackTrace();
		}
	}
	
	private void refreshSpeedView(DataPoint utm){
		if( isSpeedCheckerReady() ){
			DataPoint calculateSpeed = speed.calculateSpeed(utm);
			//Refresh speed view
			// System.out.println("calculateSpeed.getSpeed() "+calculateSpeed.getSpeed());
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
				map = new MapViewer( semaphore );
			}
		});
	}
	
	private void doSpeedViewer( final Semaphore semaphore ){
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run(){
				speedViewer = new SpeedViewer( semaphore );
			}
		});
	}
}
