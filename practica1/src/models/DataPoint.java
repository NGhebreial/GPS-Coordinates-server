package models;

import math.UTMConverter;
import utils.Coordinate;

public class DataPoint {
	
	private UTMConverter utm;
	private Double speed;
	private Coordinate coordinate;
	private Double bearing;
	private Long miliseconds;
	
	public DataPoint(){		
	}
	
	public DataPoint(UTMConverter utm, Double speed, Coordinate coordinate, Double bearing, Long miliseconds){
		this.utm = utm;
		this.speed = speed;
		this.coordinate = coordinate;
		this.bearing = bearing;
		this.miliseconds = miliseconds;
	}

	public UTMConverter getUtm() {
		return utm;
	}

	public void setUtm(UTMConverter utm) {
		this.utm = utm;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double d) {
		this.speed = d;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public Double getBearing() {
		return bearing;
	}

	public void setBearing(Double bearing) {
		this.bearing = bearing;
	}

	public Long getMiliseconds() {
		return miliseconds;
	}

	public void setMiliseconds(Long miliseconds) {
		this.miliseconds = miliseconds;
	}

	@Override
	public String toString() {
		return "DataPoint [utm=" + utm + ", speed=" + speed + ", coordinate=" + coordinate + ", bearing=" + bearing
				+ ", miliseconds=" + miliseconds + "]";
	}	
	
}
