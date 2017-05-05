package math;

import models.DataPoint;
import utils.Coordinate;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class GridGenerator {

    private static final int maxNorth = 4471000;
    private static final int minNorth = 4470780;
    private static final int maxWest = 446400;
    private static final int minWest = 446240;
    private static final int numCols = 44;
    private static final int numRows = 36;

    public static DataPoint[][] generate(){
        DataPoint[][] ret = new models.DataPoint[numRows][numCols];
        int rowCellSize = ( maxNorth - minNorth ) / numRows;
        int colCellSize = ( maxWest - minWest ) / numCols;
        System.out.println("Grid Generator " + " rowSize " + rowCellSize + " collSize " + colCellSize);
        for( int i = 0; i < numRows; i++ ) {
            int colIncrement = ( i * rowCellSize );
            for( int j = 0; j < numCols; j++ ) {
                int rowIncrement = ( j * colCellSize );
                int north = maxNorth - colIncrement;
                int south = minWest + rowIncrement;
                ret[i][j] = new DataPoint( (double)north, (double)south, Coordinate.NORTH );
            }
        }
        return ret;
    }

}
