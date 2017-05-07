package math;

import models.DataPoint;
import utils.Coordinate;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class GridGenerator {

    public static DataPoint[][] generate(int maxNorth, int minNorth, int maxWest, int minWest, int numOfRows, int numOfCols){
        double rows = ((double)(maxNorth - minNorth) / (double) numOfRows);
        int rowCellSize = (int)Math.floor(rows);

        int numRows = numOfRows;
        if( rows != Math.floor( rows ) ){
            numRows++;
        }

        double cols = ((double)(maxWest - minWest) / (double) numOfCols);
        int colCellSize = (int)Math.floor(cols);

        int numCols = numOfCols;
        if( cols != Math.floor( cols ) ){
            numCols++;
        }

        // Todo: Scale decimal diff to rowCellSize
        DataPoint[][] ret = new models.DataPoint[numRows][numCols];
        System.out.println("Grid Generator "  + " numrows, numcols " + numRows + " " + numCols + " rowSize " + rowCellSize + " collSize " + colCellSize + " maxNorth, minNorth: " + maxNorth + ", " + minNorth + " maxWest, minWest: " + maxWest + ", " + minWest);
        for( int i = 0; i < numRows; i++ ) {
            int colIncrement = ( i * rowCellSize );
            for( int j = 0; j < numCols; j++ ) {
                int rowIncrement = ( j * colCellSize );
                int north = maxNorth - colIncrement;
                int south = minWest + rowIncrement;
                ret[i][j] = new DataPoint( (double)north, (double)south, Coordinate.UNKNOWN );
            }
            if( i == numRows -1){
                System.out.println( "Last grid " + ret[i][numCols-1].toString());
            }
        }
        return ret;
    }

}
