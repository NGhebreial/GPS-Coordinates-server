package math;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class Point {

    private double latitude;
    private double longitude;
    private double speed;
    private int rowIdx;
    private int colIdx;

    public Point(){
    }

    public Point( double latitude, double longitude, double speed, int rowIdx, int colIdx ){
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.rowIdx = rowIdx;
        this.colIdx = colIdx;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLatitude( double latitude ){
        this.latitude = latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setLongitude( double longitude ){
        this.longitude = longitude;
    }

    public double getSpeed(){
        return speed;
    }

    public void setSpeed( double speed ){
        this.speed = speed;
    }

    public int getRowIdx(){
        return rowIdx;
    }

    public void setRowIdx( int rowIdx ){
        this.rowIdx = rowIdx;
    }

    public int getColIdx(){
        return colIdx;
    }

    public void setColIdx( int colIdx ){
        this.colIdx = colIdx;
    }

    @Override
    public String toString(){
        return "Point{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", rowIdx=" + rowIdx +
                ", colIdx=" + colIdx +
                '}';
    }
}
