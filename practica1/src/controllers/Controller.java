package controllers;

import gpgga.GpggaBox;
import gpgga.GpggaReceiver;
import math.CoordsCalculator;
import math.SpeedCalculator;
import math.SpeedChecker;
import math.UTMConverter;
import models.DataPoint;
import models.Quadrant;
import utils.MapConnection;
import utils.MessageBox;
import views.MapViewer;
import views.SpeedViewer;

import javax.swing.*;

import java.awt.image.BufferedImage;
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


	private MapConnection mapConnection;

	public Controller(String addr, int port){
		semaphoreMap = new Semaphore( 0 );
		semaphoreSpeed = new Semaphore( 0 );
		doMap(semaphoreMap);
		doSpeedViewer(semaphoreSpeed);
		box = new GpggaBox();
		receiver = new GpggaReceiver<GpggaBox>(addr, port, box);
		speed = new SpeedCalculator();
		mapConnection = new MapConnection();
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

	public CoordsCalculator initInsiaMapCoords(){
		Quadrant bound = speedChecker.getBoundingBox();
		DataPoint leftUp = bound.getLeftUpCorner();
		DataPoint rightDown = bound.getRightDownCorner();
		return new CoordsCalculator(leftUp.getNorting(), leftUp.getEasting(), rightDown.getNorting(), rightDown.getEasting(), map.getImWidth(), map.getHeight());
	}

	public void realRun(){
		box.setChain(new MessageBox() {

			@Override
			public void call(Object data) {
				//Received data in UTM format
				UTMConverter utm = (UTMConverter) data;
				DataPoint point = new DataPoint();
				Double utmNorth = utm.getUTMNorting();
				Double utmEast = utm.getUTMEasting();
				point.setNorting( utmNorth );
				point.setEasting( utmEast );
				refreshSpeedView(point);
				refreshImageMap(utm, utmNorth, utmEast);
				//Refresh map
				coordsCalc = new CoordsCalculator(mapConnection.getUpLeft().getY(), mapConnection.getUpLeft().getX(), 
						mapConnection.getDownLeft().getY(), mapConnection.getUpRight().getX(), 
						map.getImWidth(), map.getHeight());
				HashMap<String, Integer> coodsCalculated = coordsCalc.translatetoInt(utmNorth, true, utmEast, utm.isWestLongitude());
				
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


	@Override
	public void run() {
		realRun();
	}

	private void refreshImageMap(UTMConverter utm, Double utmNorth, Double utmEast){
		if ( ! areCoordsInBound(utmNorth, utmEast) ){
			Double degreeLatitude = utm.getDegreeLatitude() + (utm.getMinLatitude() / 60.0);
			Double degreeLongitude = utm.getDegreeLongitude() + (utm.getMinLongitude() / 60.0);
			degreeLongitude = utm.isWestLongitude()? -degreeLongitude: degreeLongitude;
			BufferedImage imgURL = mapConnection.getImage(utmEast, utmNorth, degreeLatitude, degreeLongitude);
			if ( imgURL != null ){
				map.refreshImage(imgURL);
			}
		}		
	}

	private boolean areCoordsInBound(Double north, Double east){
		boolean isInBound = true;
		if ( mapConnection.getUpLeft().getY() < north || 
				mapConnection.getDownLeft().getY() > north ||
				mapConnection.getUpLeft().getX() > east ||
				mapConnection.getUpRight().getX() < east){
			isInBound = false;
		}
		System.out.println("Yup "+mapConnection.getUpLeft().getY());
		System.out.println(mapConnection.getUpLeft().getY() < north);
		
		System.out.println("Ydown "+mapConnection.getDownLeft().getY());
		System.out.println(mapConnection.getDownLeft().getY() > north);
		
		System.out.println("Xleft "+mapConnection.getUpLeft().getX());
		System.out.println(mapConnection.getUpLeft().getX() > east);
		
		System.out.println("Xrigth "+mapConnection.getUpRight().getX());
		System.out.println(mapConnection.getUpRight().getX() < east);
		System.out.println("is in bound? "+isInBound);
		return isInBound;
	}

	private void refreshSpeedView(DataPoint utm){
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
