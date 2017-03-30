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

  private int imWidth;
  private int imHeight;

  // One pixel 100 meters
  private int imPixelRatio;

  public enum COORDS_SETTINGS {
          UP_LAT_KEY
        , LEFT_LON_KEY
        , DOWN_LAT_KEY
        , RIGHT_LON_KEY
    , IM_WIDTH_KEY
        , IM_HEIGHT_KEY
    , IM_PIXEL_RATIO_KEY
  }

  public CoordsCalculator(){
    this.imUpLat = 40.392132;
        this.imLeftLon = -3.638561;
        this.imDownLat = 40.386382;
        this.imRightLon = -3.620250;
    this.imWidth = 1362;
    this.imHeight = 644;
    this.imPixelRatio = 100;
  }

  public CoordsCalculator(double imUpLat, double imLeftLon, double imDownLat, double imRightLon){
    this.imUpLat = imUpLat;
    this.imLeftLon = imLeftLon;
        this.imDownLat = imDownLat;
        this.imRightLon = imRightLon;
        this.imWidth = 1362;
        this.imHeight = 644;
        this.imPixelRatio = 100;
  }

  public CoordsCalculator(HashMap<COORDS_SETTINGS, Double> imCoords, HashMap<COORDS_SETTINGS, Integer> imCfg){
    this.imUpLat = imCoords.get(UP_LAT_KEY);
        this.imLeftLon = imCoords.get(LEFT_LON_KEY);
        this.imDownLat = imCoords.get(DOWN_LAT_KEY);
    this.imRightLon = imCoords.get(RIGHT_LON_KEY);
    this.imWidth = imCfg.get(IM_WIDTH_KEY);
    this.imHeight = imCfg.get(IM_HEIGHT_KEY);
    this.imPixelRatio = imCfg.get(IM_PIXEL_RATIO_KEY);
  }

  public Point2D.Double translate( double lat, boolean isNorth, double lon, boolean isWest){
    // Calculate upperRight and loweLeft corners
    lat = (isNorth ? 1 : -1) * lat;
    lon = (isWest ? 1 : -1) * lon;
    double px = this.imWidth * ((lon - this.imLeftLon) / (this.imRightLon - this.imLeftLon));
        double py = this.imHeight * ((lat - this.imUpLat) / (this.imDownLat - this.imUpLat));
        return new Point2D.Double( px, py );
  }
}
