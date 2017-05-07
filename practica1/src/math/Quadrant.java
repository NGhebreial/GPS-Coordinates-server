package math;

import models.DataPoint;

import java.util.ArrayList;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class Quadrant {

    private DataPoint leftUpCorner;
    private DataPoint rightUpCorner;
    private DataPoint leftDownCorner;
    private DataPoint rightDownCorner;
    private ArrayList<int[]> indexes;
    private ArrayList<DataPoint> targets;

    public Quadrant( DataPoint leftUpCorner, DataPoint rightUpCorner, DataPoint leftDownCorner, DataPoint rightDownCorner, int[] index ){
        this.leftUpCorner = leftUpCorner;
        this.rightUpCorner = rightUpCorner;
        this.leftDownCorner = leftDownCorner;
        this.rightDownCorner = rightDownCorner;
        this.indexes = new ArrayList<int[]>();
        this.indexes.add( 0, index );
        this.indexes.add( 1, new int[]{ index[ 0 ], index[ 1 ] + 1 } );
        this.indexes.add( 2, new int[]{ index[ 0 ] + 1, index[ 1 ] } );
        this.indexes.add( 3, new int[]{ index[ 0 ] + 1, index[ 1 ] + 1 } );
        this.targets = new ArrayList<DataPoint>();
    }

    public DataPoint getLeftUpCorner(){
        return leftUpCorner;
    }

    public DataPoint getRightUpCorner(){
        return rightUpCorner;
    }

    public DataPoint getLeftDownCorner(){
        return leftDownCorner;
    }

    public DataPoint getRightDownCorner(){
        return rightDownCorner;
    }

    public boolean containsIndex( int[] index ){
        int i = 0;
        for( ;
             i < this.indexes.size() &&
             this.indexes.get( i )[ 0 ] == index[ 0 ] &&
             this.indexes.get( i )[ 1 ] == index[ 1 ];
             i++
        );
        return i < this.indexes.size();
    }

    public ArrayList<int[]> getIndexes(){
        return this.indexes;
    }

    public DataPoint[] asArray(){
        return new DataPoint[]{ this.leftUpCorner, this.rightUpCorner, this.leftDownCorner, this.rightDownCorner };
    }

    public void addTarget(DataPoint target){
        this.targets.add( target );
    }

    public DataPoint[] getTargetsByOrientation( DataPoint query, int maxDiff ){
        ArrayList<DataPoint> ret = new ArrayList<DataPoint>();
        // Get targets based on a maximum difference in orientation
        for(DataPoint target : targets){
            if( target.getCoordinate().getDiff(query.getCoordinate().getValue()) <= maxDiff ){
                ret.add( target );
            }
        }
        return (DataPoint[]) ret.toArray();
    }

    @Override
    public String toString(){
        return "Quadrant{" +
                "leftUpCorner=" + leftUpCorner +
                ", rightUpCorner=" + rightUpCorner +
                ", leftDownCorner=" + leftDownCorner +
                ", rightDownCorner=" + rightDownCorner +
                ", indexes=" + indexes +
                '}';
    }
}
