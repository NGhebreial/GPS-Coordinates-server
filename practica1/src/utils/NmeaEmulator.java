package utils;

import math.CoordsCalculator;
import math.SpeedChecker;
import models.DataPoint;
import models.Quadrant;
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

    private static final int imUpX = 446220;
    private static final int imUpY = 4471000;
    private static final int imDownX = 446400;
    private static final int imDownY = 4470780;

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
        ArrayList<ArrayList<Quadrant>> map = this.speedChecker.getMap();
        for( ArrayList<Quadrant> row : map ){
            for( Quadrant quadrant : row ){
                ArrayList<DataPoint> targets = quadrant.getTargets();
                if( targets.size() > 0 ){
                    for( DataPoint point : targets ){
                        this.points.add( fakePoint( point ) );
                    }
                }
            }
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
        return coord;
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
        System.out.println("Fake " + 1);
        res.setOrientation( randomOrientation(point.getOrientation()) );
        System.out.println("Fake " + 2);
        res.setSpeed( randomSpeed(point.getSpeed()) );
        System.out.println("Fake " + 3);
        res.setNorting( randomCoordinate( point.getNorting(), northThreshold ) );
        System.out.println("Fake " + 4);
        res.setEasting( randomCoordinate( point.getEasting(), eastThreshold ) );
        System.out.println("Fake " + 5);
        return res;
    }

    @Override
    public void run(){

    }

    public void doMap( final Semaphore semaphore ){
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run(){
                map = new MapViewer( semaphore );
            }
        });
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

    public static void main( String[] args ){

        NmeaEmulator emulator = new NmeaEmulator();
        Semaphore semaphore = emulator.getMutex();
        emulator.doMap( semaphore );
        try {
            semaphore.acquire();
            Thread.sleep( 1000 );
            MapViewer map = emulator.getMap();
            CoordsCalculator coordsCalculator = emulator.initCoords();
            ArrayList<DataPoint> points = emulator.getPoints();
            for( DataPoint point : points ){
                HashMap<String, Integer> toPaint = coordsCalculator.translatetoInt( point.getNorting(), true, point.getEasting(), false );
                System.out.println("Point at north: " + point.getNorting() + " east: " + point.getEasting() + " x " + toPaint.get( "x" ) + " y " + toPaint.get( "y" ));
                map.drawPointer( toPaint.get( "x" ), toPaint.get( "y" ) );
                break;
            }
        }catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
