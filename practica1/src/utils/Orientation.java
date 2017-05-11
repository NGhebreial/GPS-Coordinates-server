package utils;

public enum Orientation {

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

	private final int orientation;

	private Orientation( int orientation ){
		this.orientation = orientation;
	}

	public Orientation byNumber( int num){
	    Orientation[] vals = Orientation.values();
	    if( vals.length > num ){
	        return vals[num];
        }
        return Orientation.UNKNOWN;
    }

    public int getValue(){
	    return this.orientation;
    }

    public int getDiff( int value ){
        int midSize = ( Orientation.values().length-1)/2;
        if( this.orientation == Orientation.UNKNOWN.getValue() || value == Orientation.UNKNOWN.getValue() ){
            return 100;
        }
        if(
            (this.orientation <= midSize && value <= midSize) ||
            (this.orientation > midSize && value > midSize)
        ){
            return Math.abs( value - this.orientation );
        }
        int max = Math.max( value, this.orientation );
        int min = Math.min( value, this.orientation );
        return Math.min( Math.abs( max - min ), Math.abs((min + (midSize*2)) - max));
    }
}
