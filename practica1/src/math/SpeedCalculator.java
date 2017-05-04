package math;

import models.DataPoint;
import utils.Coordinate;

public class SpeedCalculator {
	
	private DataPoint previousDataPoint;
	private DataPoint currentDataPoint;
	
	public SpeedCalculator(){
	}
	
	public DataPoint calculateSpeed(UTMConverter utm){
		currentDataPoint = new DataPoint();
		//First use -> cannot calculate the speed
		if(previousDataPoint != null){
			//Setting actual millisecond and UTM coordinates 
			currentDataPoint.setMiliseconds(System.currentTimeMillis());
			currentDataPoint.setUtm(utm);
			//Using previous data to calculate km and milliseconds 
			UTMConverter previousUTM = previousDataPoint.getUtm();
			Double kmTraverse = kilometersTraverse(previousUTM.getUMTNorting(), utm.getUMTNorting(), previousUTM.getUMTEasting(), utm.getUMTEasting());
			Long diferenceHour = ( currentDataPoint.getMiliseconds() - previousDataPoint.getMiliseconds() / (1000*60*60) ) % 24;
			//With the km traverse and the time in hours calculate the speed
			Double speed =  Math.round( (kmTraverse/diferenceHour) * 100000.0)/100000.0 ;
			//Setting speed
			currentDataPoint.setSpeed( speed < 1? 0.0: speed );
			Double bearing = bearingDistance(previousUTM.getUMTNorting(), utm.getUMTNorting(), previousUTM.getUMTEasting(), utm.getUMTEasting());
			//Setting bearing
			currentDataPoint.setBearing(bearing);
			//Calculate the direction using bearing
			Integer direction = (int) (Math.round(bearing / 45) < 0? Math.round(bearing / 45) + 8: Math.round(bearing / 45));
			currentDataPoint.setCoordinate(Coordinate.values()[direction]);
			//new Data point is setting to previous data point
			previousDataPoint = new DataPoint(currentDataPoint.getUtm(), currentDataPoint.getSpeed(), currentDataPoint.getCoordinate(), currentDataPoint.getBearing(), currentDataPoint.getMiliseconds());
		}
		else{
			previousDataPoint = new DataPoint(utm, 0.0, Coordinate.NORTH, 0.0, System.currentTimeMillis());

			
		}
		return currentDataPoint;
	}
	/**Calculate kilometers traverse using Pythagorean Theorem*/
	private Double kilometersTraverse(Double previousN, Double currentN, Double previousE, Double currentE){
		Double diferenceN = Math.pow(previousN - currentN, 2.0); 
		Double diferenceE = Math.pow(previousE - currentE, 2.0); 
		return Math.sqrt( diferenceN + diferenceE ) / 1000;
	}
	/**Calculate the bearing between previous and current UTM coordinates*/
	private Double bearingDistance(Double previousN, Double currentN, Double previousE, Double currentE){
		return Math.toDegrees(Math.atan2( (previousN - currentN), (previousE - currentE) ) );
	}
}
