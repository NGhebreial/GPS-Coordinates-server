package utils;

import math.CoordsCalculator;
import math.SpeedChecker;
import models.DataPoint;
import views.MapViewer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class NmeaEmulator extends Thread {

    private SpeedChecker speedChecker;
    private ArrayList<DataPoint> points;
    private final double northThreshold;
    private final double eastThreshold;
    private final double speedThreshold;

    private MapViewer map;
    private Semaphore mutex;

    private static final int imUpX = 4471000;
    private static final int imUpY = 446220;
    private static final int imDownX = 4470780;
    private static final int imDownY = 446400;

    private static final int sleepTime = 200;

    private MessageBox box;

    public NmeaEmulator(){
        this.speedChecker = new SpeedChecker();
        this.points = new ArrayList<DataPoint>();
        this.northThreshold = 1.5;
        this.eastThreshold = 1.75;
        this.speedThreshold = 7.5;
        this.generateRandomData();
        this.mutex = new Semaphore( 0 );
    }

    public void generateRandomData(){
        int cols = 222;
        DataPoint[] dataPoints = new DataPoint[cols];
        this.speedChecker.loadDataFile("practica1/data/speed.txt", dataPoints, cols);
        for( DataPoint p : dataPoints ){
            this.points.add( fakePoint( p ) );
        }

    }

    public Orientation randomOrientation( Orientation current ){
        int ret = -1;
        int lowBound = (current.getValue() -1) % Orientation.values().length -1;
        int upBound = (current.getValue() +1) % Orientation.values().length -1;
        /*while( !(lowBound <= ret && ret <= upBound) ){
            ret = (int)((Math.random() * 0.5));
            System.out.println("Random orientation " + ret + " low bound " + lowBound + " hig " + upBound);
        }*/
        ret = current.getValue();
        return current.byNumber( ret );
    }

    public double randomCoordinate( double coordinate, double threshold ){
        double coord = -1;
        double lowBound = Math.min(coordinate - threshold, coordinate + threshold);
        double upBound = Math.max(coordinate - threshold, coordinate + threshold);
        while( !(lowBound <= coord && coord <= upBound) ){
            double incr = ((Math.random() + 1.0) * 1.5);
            coord = lowBound + incr;
        }
        return Math.round(coord * 100.0)/100.0;
    }

    public double randomSpeed( double current ){
        double ret = -1;
        double lowBound = current - speedThreshold;
        double upBound = current + speedThreshold;
        while( !(lowBound <= ret && ret <= upBound) ){
            double next = (Math.random() + 1.0) * 10;
            ret = current + ((next - lowBound)/(upBound - lowBound));
        }
        return ret;
    }

    public DataPoint fakePoint( DataPoint point ){
        // Randomize speed, location and orientation
        DataPoint res = new DataPoint();
        res.setBearing( point.getBearing() );
        res.setOrientation( randomOrientation(point.getOrientation()) );
        res.setSpeed( randomSpeed(point.getSpeed()) );
        res.setNorting( randomCoordinate( point.getNorting(), northThreshold ) );
        res.setEasting( randomCoordinate( point.getEasting(), eastThreshold ) );
        return res;
    }

    @Override
    public void run(){
        System.out.println("Emulator run");
        int i = 0;
        while( i < this.points.size() ){
            try {
                Thread.sleep( sleepTime );
                DataPoint p = this.points.get(i++);
                box.call( p );
            }catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }

    public void setBox( MessageBox box ){
        this.box = box;
    }

    public Semaphore getMutex(){
        return this.mutex;
    }

    public CoordsCalculator initCoords(){
        return new CoordsCalculator(imUpX, imUpY, imDownX, imDownY, map.getImWidth(), map.getHeight());
    }

    public ArrayList<DataPoint> getPoints(){
        return this.points;
    }

    public MapViewer getMap(){
        return this.map;
    }

    public void doMap( final Semaphore semaphore ){
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run(){
                map = new MapViewer( semaphore );
            }
        });
    }

    public static void main( String[] args ){

        NmeaEmulator emulator = new NmeaEmulator();
        Semaphore semaphore = emulator.getMutex();
        emulator.doMap( semaphore );
        try {
            semaphore.acquire();
            Thread.sleep( 5000 );
            MapViewer map = emulator.getMap();
            CoordsCalculator coordsCalculator = emulator.initCoords();
            ArrayList<DataPoint> points = emulator.getPoints();
            for( DataPoint point : points ){
                HashMap<String, Integer> toPaint = coordsCalculator.translatetoInt( point.getNorting(), true, point.getEasting(), true );
                System.out.println("Point at north: " + point.getNorting() + " east: " + point.getEasting() + " x " + toPaint.get( "x" ) + " y " + toPaint.get( "y" ));
                map.drawPointer( toPaint.get( "x" ), toPaint.get( "y" ) );
            }
            HashMap<String, Integer> toPaint = coordsCalculator.translatetoInt(imUpX, true, imUpY, true );
            map.drawPointer( toPaint.get( "x" ), toPaint.get( "y" ) );
            toPaint = coordsCalculator.translatetoInt(imDownX, true, imDownY, true );
            map.drawPointer( toPaint.get( "x" ), toPaint.get( "y" ) );
        }catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
