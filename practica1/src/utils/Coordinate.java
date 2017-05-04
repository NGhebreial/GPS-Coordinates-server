package utils;

public enum Coordinate {
	
	NORTH(0),
	NORTHEAST(1),
	EAST(2),
	SOUTH(3),
	SOUTHWEST(4),
	WEST(5),
	NORTHWEST(6),	
	SOUTHEAST(7)
	;
	private final int coordinate;
	private Coordinate(int coordinate){
		this.coordinate=coordinate;
	}
}
