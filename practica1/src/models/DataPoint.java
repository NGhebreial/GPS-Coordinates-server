package models;

import utils.Coordinate;

public class DataPoint {
	
	private Double norting;
	private Double easting;
	private Double speed;
	private Coordinate coordinate;
	private Double bearing;
	private Long miliseconds;
	
	public DataPoint(){		
	}
	
	public DataPoint(Double norting, Double easting, Double speed, Coordinate coordinate, Double bearing, Long miliseconds){
		this.norting = norting;
		this.easting = easting;
		this.speed = speed;
		this.coordinate = coordinate;
		this.bearing = bearing;
		this.miliseconds = miliseconds;
	}
	
	public Double getNorting() {
		return norting;
	}

	public void setNorting(Double norting) {
		this.norting = norting;
	}

	public Double getEasting() {
		return easting;
	}

	public void setEasting(Double easting) {
		this.easting = easting;
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
		return "DataPoint [norting=" + norting + ", easting=" + easting + ", speed=" + speed + ", coordinate="
				+ coordinate + ", bearing=" + bearing + ", miliseconds=" + miliseconds + "]";
	}	
	
}
