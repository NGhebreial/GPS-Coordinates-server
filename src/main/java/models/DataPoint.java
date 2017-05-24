package models;

import utils.Orientation;

public class DataPoint {
	
	private Double norting;
	private Double easting;
	private Double speed;
	private Orientation orientation;
	private Double bearing;
	private Long miliseconds;
	
	public DataPoint(){		
	}

	public DataPoint(Double norting, Double easting, Orientation orientation ){
		this.norting = norting;
		this.easting = easting;
		this.speed = 0.0;
		this.orientation = orientation;
		this.bearing = 0.0;
		this.miliseconds = System.currentTimeMillis();
	}

	public DataPoint( Double norting, Double easting, Double speed, Orientation orientation, Double bearing, Long miliseconds){
		this.norting = norting;
		this.easting = easting;
		this.speed = speed;
		this.orientation = orientation;
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

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation( Orientation orientation ) {
		this.orientation = orientation;
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

	public double getDistanceTo( DataPoint target ){
        double diffN = Math.pow( this.norting - target.getNorting(), 2.0 );
        double diffE = Math.pow( this.easting - target.getEasting(), 2.0 );
        return Math.sqrt( diffN + diffE );
    }

	@Override
	public String toString() {
		return "DataPoint [norting=" + norting + ", easting=" + easting + ", speed=" + speed + ", coordinate="
				+ orientation + ", bearing=" + bearing + ", miliseconds=" + miliseconds + "]";
	}

}
