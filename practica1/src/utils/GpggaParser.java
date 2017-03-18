package utilities;

public class GPGGAParser {

	private static double a = 6378137.0;

	private static double flattering = 1 / 298.25722;

	private static double eSquare = 0.00669437999013;

	private static double curvatureRadius = 6399593.626;

	private static double huso = 30.0;

	private static double lambda;

	private static double ro;
	
	// 2.87456 Radians = 180 degrees
	private static double centerLambda = (huso * 6) - Math.toDegrees(2.87456);

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
	public static void setup(double degreeLatitude, double minLatitude, double degreeLongitude, double minLongitude,
			boolean isWestLongitude) {
		ro = convertDegreesToRadians(degreeLatitude, minLatitude);
		if (isWestLongitude) {
			lambda = -(convertDegreesToRadians(degreeLongitude, minLongitude));
		}
		else{
			lambda = convertDegreesToRadians(degreeLongitude, minLongitude);
		}
	}

	private double getLambdaSub0() {
		return centerLambda * (Math.PI / 180);
	}

	private double getESquarePrime() {
		return eSquare / (1 - eSquare);
	}

	private double N(){
		double sinRo = Math.sin(ro);
		double square = Math.sqrt( 1 - (eSquare* Math.pow(sinRo, 2.0)));
		return a / square;
	}

	private double T(){
		double tan = Math.tan(ro);
		return Math.pow(tan, 2.0);
	}
	
	private double C(){
		double cosRo = Math.cos(ro);
		return getESquarePrime() * Math.pow(cosRo, 2.0);
	}
	
	private double A(){
		return Math.cos(ro) * (lambda - getLambdaSub0());
	}
	
	private double M(){
		double first = ( 1 - ( eSquare/4 ) - ( 3/64* Math.pow(eSquare, 2.0) ) - ( 5/256* Math.pow(eSquare, 3.0) ) ) * ro;
		double second = ( ( 3/8 * eSquare ) + ( 3/32 * Math.pow(eSquare, 2.0) ) + ( 45/1024 * Math.pow(eSquare, 3.0) ) ) * Math.sin(2*ro);
		double third = ( ( 15/256 * Math.pow(eSquare, 2.0) ) + ( 45/1024 * Math.pow(eSquare, 3.0) ) ) * Math.sin(4*ro);
		double fourth =  ( 35/3072 * Math.pow(eSquare, 3.0)) * Math.sin(6*ro);
		return a * ( first - second + third - fourth );
	}
	private static double convertDegreesToRadians(double degrees, double minutes) {
		double angle = degrees + minutes / 60.0;
		return Math.toRadians(angle);
	}
	
	/**Return the UMT easting in radians*/
	public double getUMTEasting(){
		double second = ( A() + (1 - T() + C())*Math.pow(A(), 3.0)/6 + ( 5 - 18*T() + Math.pow(T(), 2.0) + 72 * C() - 58 * eSquare ) * Math.pow(A(), 3.0)/120 );
		return 0.9996 * N() * second + 500000;
	}
	
	/**Return the UMT easting in radians*/
	public double getUMTNorting(){
		double first = M() + N() * Math.tan(ro);
		double second = ( Math.pow(A(), 2.0)/2 + ( 5 - T() + 9 * C() + 4 * Math.pow(C(), 2.0) ) * Math.pow(A(), 4.0)/24 );
		double third = ( ( 61 - 58 * T() + Math.pow(T(), 2.0) + 600 * C() - 330 * eSquare ) * Math.pow(A(), 6.0)/720 );
		return 0.9996 * ( first * ( second + third ));
	}
}
