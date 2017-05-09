package math;

import models.DataPoint;
import utils.Orientation;

public class SpeedCalculator {
	
	private DataPoint previousDataPoint;
	private DataPoint currentDataPoint;
	
	public SpeedCalculator(){
	}
	
	public DataPoint calculateSpeed(UTMConverter utm){
		currentDataPoint = new DataPoint(0.0, 0.0, 0.0, Orientation.NORTH, 0.0, System.currentTimeMillis());
		//First use -> cannot calculate the speed
		if(previousDataPoint != null){
			//Setting actual millisecond and UTM coordinates 
			currentDataPoint.setMiliseconds(System.currentTimeMillis());
			currentDataPoint.setNorting(utm.getUMTNorting());
			currentDataPoint.setEasting(utm.getUMTEasting());
			//Using previous data to calculate km and milliseconds
			Double distaceKm = Math.round( traversedKilometers(previousDataPoint.getNorting(), currentDataPoint.getNorting(),
					previousDataPoint.getEasting(), currentDataPoint.getEasting()) * 100000.0 )/100000.0 ;
			Double diferenceHour = Math.round( ((currentDataPoint.getMiliseconds() - previousDataPoint.getMiliseconds()) / 
					(1000.0*60.0*60.0)) * 1000000.0 ) / 1000000.0  ;
			//With the km traverse and the time in hours calculate the speed
			Double speed =  Math.round( (distaceKm/diferenceHour) * 100000.0)/100000.0 ;
			//Setting speed
			currentDataPoint.setSpeed( speed < 1? 0.0: speed );
			Double bearing = bearingDistance(previousDataPoint.getNorting(), currentDataPoint.getNorting(),
					previousDataPoint.getEasting(), currentDataPoint.getEasting());
			//Setting bearing
			currentDataPoint.setBearing(bearing);
			//Calculate the direction using bearing
			Integer direction = (int) (Math.round(bearing / 45) < 0? Math.round(bearing / 45) + 8: Math.round(bearing / 45));
			currentDataPoint.setOrientation( Orientation.values()[direction]);
			//new Data point is setting to previous data point
			previousDataPoint = new DataPoint(currentDataPoint.getNorting(), currentDataPoint.getEasting(), currentDataPoint.getSpeed(), currentDataPoint.getOrientation(), currentDataPoint.getBearing(), currentDataPoint.getMiliseconds());
		}
		else{
			previousDataPoint = new DataPoint(0.0, 0.0, 0.0, Orientation.NORTH, 0.0, System.currentTimeMillis());
		}
		return currentDataPoint;
	}
	/**Calculate traversed kilometers using Pythagorean Theorem*/
	private Double traversedKilometers( Double previousN, Double currentN, Double previousE, Double currentE){
		Double differenceN = Math.pow(previousN - currentN, 2.0);
		Double differenceE = Math.pow(previousE - currentE, 2.0);
		return Math.sqrt( differenceN + differenceE ) / 1000;
	}
	/**Calculate the bearing between previous and current UTM coordinates*/
	private Double bearingDistance(Double previousN, Double currentN, Double previousE, Double currentE){
		return Math.toDegrees(Math.atan2( (previousN - currentN), (previousE - currentE) ) );
	}
}
