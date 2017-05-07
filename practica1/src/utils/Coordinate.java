package utils;

public enum Coordinate {

	NORTH(0),
    NORTHEAST(1),
    EAST(2),
    SOUTHEAST(3),
    SOUTH(4),
    SOUTHWEST(5),
    WEST(6),
    NORTHWEST(7),
    UNKNOWN(8)
    ;
	private final int coordinate;
	private Coordinate(int coordinate){
		this.coordinate=coordinate;
	}
	public Coordinate byNumber(int num){
	    Coordinate[] vals = Coordinate.values();
	    if( vals.length > num ){
	        return vals[num];
        }
        return Coordinate.UNKNOWN;
    }
    public int getValue(){
	    return this.coordinate;
    }

    public int getDiff( int value ){
        int midSize = (Coordinate.values().length-1)/2;
        if(
            (this.coordinate <= midSize && value <= midSize) ||
            (this.coordinate > midSize && value > midSize)
        ){
            return Math.abs( value - this.coordinate );
        }
        int max = Math.max( value, this.coordinate );
        int min = Math.min( value, this.coordinate );
        return Math.min( Math.abs( max - min ), Math.abs((min + (midSize*2)) - max));
    }
}
