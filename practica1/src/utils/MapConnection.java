package utils;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

public class MapConnection {
	private Point2D.Double upLeft;
	private Point2D.Double upRight;
	private Point2D.Double downLeft;
	private Point2D.Double downRight;
	private static Double fixedDistance = 1500.0; // 300 px * 5 m por pixel
	
	public MapConnection(){
		upLeft = new Point2D.Double( 0.0, 0.0 );
		upRight = new Point2D.Double( 0.0, 0.0 );
		downLeft = new Point2D.Double( 0.0, 0.0 );
		downRight = new Point2D.Double( 0.0, 0.0 );
	}

	public BufferedImage getImage( Double UTMEast, Double UTMNorth, Double degreeLatitude, Double degreeLongitude){
		BufferedImage img = null;
		try {
			upLeft = new Point2D.Double( UTMEast - fixedDistance, UTMNorth + fixedDistance );
			upRight = new Point2D.Double( UTMEast + fixedDistance, UTMNorth + fixedDistance );
			downLeft = new Point2D.Double( UTMEast - fixedDistance, UTMNorth - fixedDistance );
			downRight = new Point2D.Double( UTMEast + fixedDistance, UTMNorth - fixedDistance );
			String url = "https://maps.googleapis.com/maps/api/staticmap?center="+degreeLatitude+","+
			degreeLongitude+"&zoom=18&size=600x600&maptype=hybrid&key=AIzaSyCS7U-46JzWIGUEi-xw0EUHKbnJPC1Zf9E";
			
			img = sendGet(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return img;
	}
	private BufferedImage sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		BufferedImage image = ImageIO.read(obj);
		return image;
	}
	public Point2D.Double getUpLeft() {
		return upLeft;
	}
	public Point2D.Double getUpRight() {
		return upRight;
	}
	public Point2D.Double getDownLeft() {
		return downLeft;
	}
	public Point2D.Double getDownRight() {
		return downRight;
	}
}


