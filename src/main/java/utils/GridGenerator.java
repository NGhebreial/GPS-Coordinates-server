package utils;

import models.DataPoint;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class GridGenerator {

    private int maxNorth;
    private int minNorth;
    private int maxWest;
    private int minWest;
    private int numRows;
    private int numCols;
    private int rowCellSize;
    private int colCellSize;

    public GridGenerator(int maxNorth, int minNorth, int maxWest, int minWest, int numRows, int numCols){
        this.maxNorth = maxNorth;
        this.minNorth = minNorth;
        this.maxWest = maxWest;
        this.minWest = minWest;
        this.numRows = numRows;
        this.numCols = numCols;
        this.rowCellSize = 1;
        this.colCellSize = 1;
    }

    public DataPoint[][] generate(){
        double rows = ((double)(this.maxNorth - this.minNorth) / (double) this.numRows);
        this.rowCellSize = (int)Math.floor(rows);

        if( rows != Math.floor( rows ) ){
            this.numRows++;
        }

        double cols = ((double)(this.maxWest - this.minWest) / (double) this.numCols);
        this.colCellSize = (int)Math.floor(cols);

        if( cols != Math.floor( cols ) ){
            this.numCols++;
        }

        // Todo: Scale decimal diff to rowCellSize
        DataPoint[][] ret = new models.DataPoint[numRows+1][numCols+1];
        System.out.println("Grid Generator "  + " numrows, numcols " + numRows + " " + numCols + " rowSize " + rowCellSize + " collSize " + colCellSize + " maxNorth, minNorth: " + maxNorth + ", " + minNorth + " maxWest, minWest: " + maxWest + ", " + minWest);
        for( int i = 0; i <= numRows; i++ ) {
            int colIncrement = ( i * rowCellSize );
            for( int j = 0; j <= numCols; j++ ) {
                int rowIncrement = ( j * colCellSize );
                int north = maxNorth - colIncrement;
                int south = minWest + rowIncrement;
                ret[i][j] = new DataPoint( (double)north, (double)south, Orientation.UNKNOWN );
            }
        }
        System.out.println( "Last grid " + ret[numRows][numCols].toString());
        return ret;
    }

    public int getWidth(){
        return Math.abs( this.maxNorth - this.minNorth );
    }

    public int getHeight(){
        return Math.abs( this.maxWest - this.minWest );
    }

    public double getCellSize(){
        return (this.rowCellSize + this.colCellSize)/2.0;
    }

}
