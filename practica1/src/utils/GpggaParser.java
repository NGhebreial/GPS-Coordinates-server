package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GpggaParser {

	private static double a = 6378137.0;

	private static double flattering = 1 / 298.25722;

	private static double eSquare = 0.00669437999013;

	private static double curvatureRadius = 6399593.626;

	private static double huso = 30.0;

	private static double lambda;

	private static double ro;
	
	private static double centerLambda = (huso * 6) - 183;
	
	/**
	 * Setup the basic data to pass from latitude, longitude to UTM coordinates
	 * 
	 * @param degreeLatitude
	 *            latitude degrees
	 * @param minLatitude
	 *            longitude minutes
	 * @param degreeLatitude
	 *            latitude degrees
	 * @param minLongitude
	 *            longitude minutes
	 * @param isWestLongitude
	 *            indicate if longitude refers to west or east
	 */
	public static void setup(double degreeLatitude, double minLatitude, double degreeLongitude, double minLongitude, boolean isWestLongitude) {
		ro = convertDegreesToRadians(degreeLatitude, minLatitude);
		ro = round(ro, 5);
		if (isWestLongitude) {
			lambda = -(convertDegreesToRadians(degreeLongitude, minLongitude));
		}
		else{
			lambda = convertDegreesToRadians(degreeLongitude, minLongitude);
		}
		lambda = round(lambda, 5);
	}

	private static double getLambdaSub0() {
		System.out.println("LAMBDA0 "+round(centerLambda * (Math.PI / 180),9));
		return round(centerLambda * (Math.PI / 180),9);
	}

	private static double getESquarePrime() {
		System.out.println("EsquareP "+round(eSquare / (1 - eSquare),10));
		return round(eSquare / (1 - eSquare),10);
	}

	private static double N(){
		double sinRo = Math.sin(ro);
		double square = Math.sqrt( 1 - (eSquare* Math.pow(sinRo, 2.0)));
		System.out.println("N "+round(a / square, 6));
		return round(a / square, 6);
	}

	private static double T(){
		double tan = Math.tan(ro);
		System.out.println("T "+round(Math.pow(tan, 2.0), 10));
		return round(Math.pow(tan, 2.0), 10);
	}
	
	private static double C(){
		double cosRo = Math.cos(ro);
		System.out.println("C "+round(getESquarePrime() * Math.pow(cosRo, 2.0), 8));
		return round(getESquarePrime() * Math.pow(cosRo, 2.0), 8);
	}
	
	private static double A(){
		System.out.println("A "+round(Math.cos(ro) * (lambda - getLambdaSub0()), 10));
		return round(Math.cos(ro) * (lambda - getLambdaSub0()), 10);
	}
	
	private static double M(){
		double first = ( 1 - ( eSquare/4 ) - ( 3/64 * (Math.pow(eSquare, 2.0)) ) - ( 5/256 * (Math.pow(eSquare, 3.0)) ) ) * ro;
		double second = ( ( 3/8 * eSquare ) + ( 3/32 * (Math.pow(eSquare, 2.0)) ) + ( 45/1024 * (Math.pow(eSquare, 3.0)) ) ) * Math.sin(2*ro);
		double third = ( ( 15/256 * (Math.pow(eSquare, 2.0)) ) + ( 45/1024 * (Math.pow(eSquare, 3.0)) ) ) * Math.sin(4*ro);
		double fourth =  ( 35/3072 * (Math.pow(eSquare, 3.0))) * Math.sin(6*ro);
		System.out.println("M "+round(a * ( first - second + third - fourth ), 4));
		return round(a * ( first - second + third - fourth ), 4);
	}

	private static double convertDegreesToRadians(double degrees, double minutes) {
		double angle = degrees + (minutes / 60.0);
//		System.out.println("GRADOS "+angle);
//		System.out.println("RADIANS "+Math.toRadians(angle));
		return Math.toRadians(angle);
	}
	
	/**Return the UMT easting in radians*/
	public static double getUMTEasting(){
		double second = ( A() + (((1 - T() + C()) * Math.pow(A(), 3.0))/6 + (( 5 - (18*T()) + Math.pow(T(), 2.0) + (72 * C()) - (58 * eSquare) ) * Math.pow(A(), 3.0))/120) );
		return round((0.9996 * N() * second) + 500000, 6);
	}
	
	/**Return the UMT easting in radians*/
	public static double getUMTNorting(){
		double first = (M() + N()) * Math.tan(ro);
		double second = ( Math.pow(A(), 2.0)/2 + ( (5 - T() + (9 * C()) + (4 * Math.pow(C(), 2.0)) ) * Math.pow(A(), 4.0))/24 );
		double third = ( (( 61 - (58 * T()) + Math.pow(T(), 2.0) + (600 * C()) - (330 * eSquare) ) * Math.pow(A(), 6.0))/720 );
		return round(0.9996 * ( first * ( second + third )), 6);
	}
	
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.DOWN);
	    return bd.doubleValue();
	}
}
