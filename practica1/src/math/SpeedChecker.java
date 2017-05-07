package math;

import models.DataPoint;
import utils.Coordinate;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class SpeedChecker {

    private final String DATA_FILE = "practica1/data/speed.txt";
    private final String MAP_FILE = "practica1/data/map.txt";

    private int dataPoints;
    private DataPoint[] data;
    private DataPoint[][] sourceData;
    private ArrayList<ArrayList<Quadrant>> map;

    public SpeedChecker(){
        this.dataPoints = 222;
        this.map = new ArrayList<ArrayList<Quadrant>>();
        this.data = new DataPoint[dataPoints];
        this.sourceData = GridGenerator.generate();
        this.map = this.setMap(this.sourceData);
        this.dumpDataFile( this.DATA_FILE, this.data, this.dataPoints );
    }

    private void dumpDataFile( String path, DataPoint[] store, int cols ){
        try {
            Scanner sc = new Scanner( new BufferedInputStream( new FileInputStream( path ) ) );
            int colIdx = 0;
            while( colIdx < cols && sc.hasNextLine() ){
                double north = sc.nextDouble();
                double south = sc.nextDouble();
                // TODO => Bearing
                double speed = sc.nextInt();
                store[colIdx++] = new DataPoint( north, south, speed, Coordinate.NORTH, 0.0, System.currentTimeMillis() );
            }
            if( colIdx != cols ){
                throw new RuntimeException( "Bad Dump File specification, expect " + cols + " got " + colIdx);
            }
        }catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }

    private ArrayList<ArrayList<Quadrant>> setMap( DataPoint[][] data){
        ArrayList<ArrayList<Quadrant>> matrix = new ArrayList<ArrayList<Quadrant>>();
        for( int i = 0; i < data.length -1; i++ ) {
            ArrayList<Quadrant> row = new ArrayList<Quadrant>();
            for( int j = 0; j < data[0].length -1; j++ ) {
                Quadrant target = new Quadrant(data[i][j], data[i][j+1], data[i+1][j], data[i+1][j+1], new int[]{ i, j });
                row.add(target);
            }
            matrix.add( row );
        }
        return matrix;
    }

    private boolean checkPointInBound( Quadrant square, DataPoint target ){
        DataPoint leftUp = square.getLeftUpCorner();
        DataPoint rigthDown = square.getRightDownCorner();
        return (
            (leftUp.getNorting() >= target.getNorting() && rigthDown.getNorting() < target.getNorting()) &&
            (leftUp.getEasting() <= target.getEasting() && rigthDown.getEasting() > target.getEasting())
        );
    }

    private Quadrant getQuadrantByIndex( int[] index ){
        Quadrant ret = null;
        boolean found = false;
        int i = 0;
        int j = 0;
        for(; i < this.map.size() && !found; i++ ) {
            ArrayList<Quadrant> row = this.map.get( i );
            for(;j < row.size() && !(found = row.get( j ).containsIndex( index )); j++ );
        }
        if( i < this.map.size() && j < this.map.get(i).size() ){
            ret = this.map.get( i ).get( j );
        }
        return ret;
    }

    private Quadrant getQuadrant( DataPoint point ){
        // Find the best match
        Quadrant ret = null;
        boolean found = false;
        int i = 0;
        int j = 0;
        for(; i < this.map.size() && !found; i++ ) {
            ArrayList<Quadrant> row = this.map.get(i);
            for(; j < row.size() && !(found = checkPointInBound(row.get( j ), point)); j++ );
        }
        if( found ){
            ret = this.map.get( i-1 ).get( j );
        }
        return ret;
    }

    public ArrayList<ArrayList<Quadrant>> getMap(){
        return this.map;
    }

    // -1 not found, 0 all right, 1 higher, 2 very high
    public int getTargetSpeed( DataPoint target ){
        // Try grabbing the quadrant for this point
        Quadrant contained = this.getQuadrant( target );
        if( contained != null ){
        }
        return -1;
    }

    public static void main( String[] args ){
        SpeedChecker sp = new SpeedChecker();
        ArrayList<ArrayList<Quadrant>> themap = sp.getMap();
        DataPoint target = themap.get(0).get(0).getLeftUpCorner();
        DataPoint testPoint = new DataPoint( target.getNorting(), target.getEasting(), target.getCoordinate() );
        System.out.println(testPoint);
        System.out.println("Check " + sp.getQuadrant( testPoint ));
    }
}
