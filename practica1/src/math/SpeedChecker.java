package math;

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

    private int cols;
    private int rows;
    private int dataPoints;
    private Point[] data;
    private Point[][] sourceData;
    private ArrayList<ArrayList<Point[]>> map;

    public SpeedChecker(){
        this.cols = 9;
        this.rows = 11;
        this.dataPoints = 222;
        this.map = new ArrayList<ArrayList<Point[]>>( this.rows );
        this.data = new Point[dataPoints];
        this.sourceData = GridGenerator.generate();
        this.map = this.setMap(this.sourceData);
        this.dumpDataFile( this.DATA_FILE, this.data, this.dataPoints );
    }

    private void dumpDataFile(String path, Point[] store, int cols){
        try {
            Scanner sc = new Scanner( new BufferedInputStream( new FileInputStream( path ) ) );
            int colIdx = 0;
            while( colIdx < cols && sc.hasNextLine() ){
                double lat = sc.nextDouble();
                double lon = sc.nextDouble();
                double speed = sc.nextInt();
                store[colIdx++] = new Point( lat, lon, speed, 0, colIdx );
            }
            if( colIdx != cols ){
                throw new RuntimeException( "Bad Dump File specification, expect " + cols + " got " + colIdx);
            }
        }catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }

    private Point[][] dumpMapFile(String path, int rows, int cols){
        Point[][] ret = new Point[rows][cols];
        try {
            Scanner sc = new Scanner( new BufferedInputStream( new FileInputStream( path ) ) );
            int colIdx = 0;
            int rowIdx = 0;
            while( rowIdx < rows && sc.hasNextLine() ){
                double lat = sc.nextDouble();
                double lon = sc.nextDouble();
                double speed = sc.nextInt();
                ret[rowIdx][colIdx] = new Point( lat, lon, speed, rowIdx, colIdx );
                colIdx++;
                if( colIdx == cols ){
                    rowIdx++;
                    if( rowIdx < rows ){
                        // Do not break the index so we can check later
                        colIdx = 0;
                    }
                }
            }
            if( colIdx != cols || rowIdx != rows ){
                throw new RuntimeException( "Bad Dump File specification, expect " + cols + " columns by " + rows + " rows, got " + colIdx + " by " + rowIdx );
            }
        }catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        return ret;
    }

    private ArrayList<ArrayList<Point[]>> setMap(Point[][] data){
        ArrayList<ArrayList<Point[]>> matrix = new ArrayList<ArrayList<Point[]>>();
        for( int i = 0; i < data.length -1; i++ ) {
            ArrayList<Point[]> row = new ArrayList<Point[]>();
            for( int j = 0; j < data[0].length -1; j++ ) {
                Point[] target = new Point[]{ data[i][j], data[i][j+1], data[i+1][j], data[i+1][j+1] };
                row.add(target);
            }
            matrix.add( row );
        }
        return matrix;
    }

    public ArrayList<ArrayList<Point[]>> getMap(){
        return this.map;
    }

    private boolean checkPointInBound(Point[] square, Point target){
        Point leftUp = square[0];
        Point rigthDown = square[3];
        return (
            (leftUp.getLatitude() >= target.getLatitude() && rigthDown.getLatitude() < target.getLatitude()) &&
            (leftUp.getLongitude() <= target.getLongitude() && rigthDown.getLongitude() > target.getLongitude())
        );
    }

    public Point getIndex(Point point){
        Point ret = new Point(point.getLatitude(), point.getLongitude(), point.getSpeed(), 0, 0);
        // Find the best match
        boolean found = false;
        int i = 0;
        int j = 0;
        for(; i < this.map.size() && !found; i++ ) {
            ArrayList<Point[]> row = this.map.get(i);
            // for(; j < row.size() && !(found = checkPointInBound(row.get( j ), ret)); j++ );
            for(; j < row.size() && !(found = checkPointInBound(row.get( j ), ret)); j++ );
        }
        if( found ){
            return this.sourceData[i-1][j];
        }else{
            return null;
        }
    }

    public static void main( String[] args ){
        SpeedChecker sp = new SpeedChecker();
        ArrayList<ArrayList<Point[]>> themap = sp.getMap();
        Point testPoint = new Point( themap.get(0).get(0)[0].getLatitude(), themap.get(0).get(0)[0].getLongitude(), 0, 0, 0 );
        System.out.println(testPoint);
        System.out.println("Check " + sp.getIndex( testPoint ));
    }
}
