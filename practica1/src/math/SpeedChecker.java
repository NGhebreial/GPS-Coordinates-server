package math;

import models.DataPoint;
import models.Quadrant;
import utils.GridGenerator;
import utils.Orientation;

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

    private int dataPoints;
    private DataPoint[] data;
    private DataPoint[][] sourceData;
    private ArrayList<ArrayList<Quadrant>> map;
    private Quadrant boundingBox;

    private GridGenerator grid;

    // Map data
    private static final int maxNorth = 4471000;
    private static final int minNorth = 4470780;
    private static final int maxWest = 446400;
    private static final int minWest = 446220;
    private static final int numRows = 11;
    private static final int numCols = 9;

    public SpeedChecker(){
        this.dataPoints = 222;
        this.map = new ArrayList<ArrayList<Quadrant>>();
        this.data = new DataPoint[dataPoints];
        this.grid = new GridGenerator(maxNorth, minNorth, maxWest, minWest, numRows, numCols);
        this.sourceData = grid.generate();
        this.map = this.setMap(this.sourceData);
        this.loadDataFile( this.DATA_FILE, this.data, this.dataPoints );
        this.setBoundingBox();
        this.matchDataWithGrid(this.data);
    }

    private void setBoundingBox(){
        ArrayList<Quadrant> firstRow = this.map.get( 0 );
        ArrayList<Quadrant> lastRow = this.map.get( this.map.size() -1 );
        DataPoint leftUp = firstRow.get( 0 ).getLeftUpCorner();
        DataPoint rightUp = firstRow.get( firstRow.size() -1 ).getRightUpCorner();
        DataPoint leftDown = lastRow.get( 0 ).getLeftDownCorner();
        DataPoint rightDown = lastRow.get( lastRow.size() -1 ).getRightDownCorner();
        this.boundingBox = new Quadrant( leftUp, rightUp, leftDown, rightDown, new int[]{0, firstRow.size() -1} );
    }

    public Quadrant getBoundingBox(){
        return this.boundingBox;
    }

    private void matchDataWithGrid( DataPoint[] data ){
        int matched = 0;
        for(DataPoint dataPoint: data){
            // Find the quadrant, load data on it
            Quadrant target = this.getQuadrant( dataPoint );
            if( target == null ){
                System.out.println("Matched " + matched + " out of " + data.length + " points");
                System.out.println("Max map quadrant " + this.map.get(this.map.size() -1).get(this.map.get(0).size()-1).getLeftDownCorner().toString());
                throw new Error( "Cannot match data point with grid map " + dataPoint.toString());
            }else{
                ++matched;
                target.addTarget( dataPoint );
            }
        }
    }

    public void loadDataFile( String path, DataPoint[] store, int cols ){
        try {
            Scanner sc = new Scanner( new BufferedInputStream( new FileInputStream( path ) ) );
            int colIdx = 0;
            while( colIdx < cols && sc.hasNextLine() ){
                double north = sc.nextDouble();
                double south = sc.nextDouble();
                double speed = sc.nextInt();
                Orientation orientation = Orientation.values()[sc.nextInt()];
                store[colIdx++] = new DataPoint( north, south, speed, orientation, 0.0, System.currentTimeMillis() );
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
            for(j = 0;j < row.size() && !(found = row.get( j ).containsIndex( index )); j++ );
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
        // First against the hole map box
        if( this.checkPointInBound( this.boundingBox, point ) ){
            for( ; i < this.map.size() && !found; i++ ) {
                ArrayList<Quadrant> row = this.map.get( i );
                for( j = 0; j < row.size() && !( found = checkPointInBound( row.get( j ), point ) ); j++ ) ;
            }
            if( found ){
                ret = this.map.get( i - 1 ).get( j );
            }
        }
        return ret;
    }

    public ArrayList<ArrayList<Quadrant>> getMap(){
        return this.map;
    }

    // TODO => Check max and min threshold
    private int distanceMatch( DataPoint target, DataPoint[] matches, ArrayList<DataPoint> inspection, ArrayList<DataPoint> distances ){
        // Speed match
        double cellSize = grid.getCellSize();
        double minThreshold = 1.0;
        double maxThreshold = cellSize/4.0;
        int firstIndex = 0;
        for( int i = 0; i < matches.length; i++ ){
            double currentDistance = target.getDistanceTo( matches[i] );
            if( currentDistance < maxThreshold ){
                if( inspection.size() == 0 ){
                    firstIndex = i;
                }
                inspection.add( matches[i] );
                if( currentDistance < minThreshold ){
                    if( distances.size() == 0 ){
                        firstIndex = i;
                    }
                    distances.add( matches[i] );
                }
            }
        }
        return firstIndex;
    }

    /**
     * Speed model determination
     *  1. Minimum distance by coordinates
     *  2. Next index on matches (is next-to-be point)
     */
    // -1 not found, 0 all right, 1 higher, 2 much higher
    public double getTargetSpeed( DataPoint target ){
        double ret = -1.0;
        // Try grabbing the quadrant for this point
        Quadrant contained = this.getQuadrant( target );
        if( contained != null ){
            // Maximum orientation difference: 1
            // TODO => Calibrate with real sensor data and math on SpeedCalculator
            DataPoint[] matches = contained.getTargetsByOrientation( target, 1 );
            if( matches.length > 0){
                ArrayList<DataPoint> inspection = new ArrayList<DataPoint>( matches.length );
                ArrayList<DataPoint> testMatches = new ArrayList<DataPoint>( matches.length );
                int firstMatch = distanceMatch( target, matches, inspection, testMatches );
                if( inspection.size() == 1 ){
                    // End here
                    ret = inspection.get( 0 ).getSpeed();
                }else if( testMatches.size() == 1 ){
                    // End here
                    ret = testMatches.get( 0 ).getSpeed();
                }else{
                    if( matches.length +1 > firstMatch ){
                        ret = matches[firstMatch +1].getSpeed();
                    }else{
                        ret = matches[firstMatch].getSpeed();
                    }
                }
            }
        }
        return ret;
    }

}
