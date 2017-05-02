package utils;

import java.awt.geom.Point2D;
import java.util.HashMap;

import static utils.CoordsCalculator.COORDS_SETTINGS.*;

public class CoordsCalculator {

	// North
	private double imUpLat;
	// West
	private double imLeftLon;
	// South
	private double imDownLat;
	// East
	private double imRightLon;

	private double imWidth;
	private double imHeight;

	public enum COORDS_SETTINGS {
		UP_LAT_KEY
		, LEFT_LON_KEY
		, DOWN_LAT_KEY
		, RIGHT_LON_KEY
		, IM_WIDTH_KEY
		, IM_HEIGHT_KEY
	}

	public CoordsCalculator(){
		UTMConverter utm = new UTMConverter();
		// Default UPM
		utm.setup(40.392132,0.0, 3.638561, 0.0, true );
		this.imUpLat = utm.getUMTNorting();
		this.imLeftLon = utm.getUMTEasting();
		utm.setup(40.386382, 0.0, 3.620250, 0.0, true);
		this.imDownLat = utm.getUMTNorting();
		this.imRightLon = utm.getUMTEasting();
		this.imWidth = 1362;
		this.imHeight = 644;
	}

	public CoordsCalculator(double imUpLat, double imLeftLon, double imDownLat, double imRightLon){
	    this(imUpLat, imLeftLon, imDownLat, imRightLon, 1362, 644);
	}

	public CoordsCalculator(double imUpLat, double imLeftLon, double imDownLat, double imRightLon, int imWidth, int imHeight){
		this.imUpLat = imUpLat;
		this.imLeftLon = imLeftLon;
		this.imDownLat = imDownLat;
		this.imRightLon = imRightLon;
		this.imWidth = imWidth;
		this.imHeight = imHeight;
	}

	public CoordsCalculator(HashMap<COORDS_SETTINGS, Double> imCoords, HashMap<COORDS_SETTINGS, Integer> imCfg){
		this.imUpLat = imCoords.get(UP_LAT_KEY);
		this.imLeftLon = imCoords.get(LEFT_LON_KEY);
		this.imDownLat = imCoords.get(DOWN_LAT_KEY);
		this.imRightLon = imCoords.get(RIGHT_LON_KEY);
		this.imWidth = imCfg.get(IM_WIDTH_KEY);
		this.imHeight = imCfg.get(IM_HEIGHT_KEY);
	}

	public Point2D.Double translate( double lat, boolean isNorth, double lon, boolean isWest){
		// Calculate upperRight and loweLeft corners
		lat = (isNorth ? 1 : -1) * lat;
		lon = (isWest ? 1 : -1) * lon;
		double px = this.imWidth * ((lon - this.imLeftLon) / (this.imRightLon - this.imLeftLon));
		double py = this.imHeight * ((lat - this.imUpLat) / (this.imDownLat - this.imUpLat));

		return new Point2D.Double( px, py );
	}
	/***/
	public HashMap<String, Integer> translatetoInt( double lat, boolean isNorth, double lon, boolean isWest){
		// Calculate upperRight and loweLeft corners
		Point2D.Double data = translate(lat, isNorth, lon, isWest);
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("x", (int)data.getX());
		map.put("y", (int)data.getY());
		return map;
	}
}
