package math;


public class UTMConverter {

	private static double a = 6378137.0;

	private static double flattering = 1.0 / 298.25722;

	private static double eSquare = 0.00669437999013;

	private static double curvatureRadius = 6399593.626;

	private static double huso = 30.0;

	private double lambda;

	private double ro;

	private static double centerLambda = (huso * 6.0) - 183.0;
	
	private boolean isWestLongitude;
	
	public UTMConverter(){
		
	}

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
	public UTMConverter (double degreeLatitude, double minLatitude, double degreeLongitude, double minLongitude, boolean isWestLongitude){
		setup(degreeLatitude, minLatitude, degreeLongitude, minLongitude, isWestLongitude);
	}

	public void setup(double degreeLatitude, double minLatitude, double degreeLongitude, double minLongitude, boolean isWestLongitude) {
		this.isWestLongitude = isWestLongitude;
		ro = convertDegreesToRadians(degreeLatitude, minLatitude);
		lambda = (isWestLongitude ? -1 : 1) * convertDegreesToRadians(degreeLongitude, minLongitude);
	}	
	
	public boolean isWestLongitude(){
		return isWestLongitude;
	}
	private double getLambdaSub0() {
		return centerLambda * (Math.PI / 180.0);
	}

	private double getESquarePrime() {
		return eSquare / (1.0 - eSquare);
	}

	private double N(){
		double square = Math.sqrt( 1.0 - (eSquare*Math.pow(Math.sin(ro), 2.0)));
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
		double first = ( 1.0 - ( eSquare/4.0 ) - ( 3.0/64.0 * (Math.pow(eSquare, 2.0)) ) - ( 5.0/256.0 * (Math.pow(eSquare, 3.0)) ) ) * ro;
		double second = ( ( 3.0/8.0 * eSquare ) + ( 3.0/32.0 * (Math.pow(eSquare, 2.0)) ) + ( 45.0/1024.0 * (Math.pow(eSquare, 3.0)) ) ) * Math.sin(2.0*ro);
		double third = ( ( 15.0/256.0 * (Math.pow(eSquare, 2.0)) ) + ( 45.0/1024.0 * (Math.pow(eSquare, 3.0)) ) ) * Math.sin(4.0*ro);
		double fourth =  ( 35.0/3072.0 * (Math.pow(eSquare, 3.0))) * Math.sin(6.0*ro);
		return a * ( first - second + third - fourth );
	}

	private static double convertDegreesToRadians(double degrees, double minutes) {
		double angle = degrees + (minutes / 60.0);
		return Math.toRadians(angle);
	}

	/**Return the UMT easting in radians*/
	public double getUTMEasting(){
		double second = ( (A() + (((1.0 - T() + C()) * Math.pow(A(), 3.0))/6.0) + ((( 5.0 - (18.0*T()) + Math.pow(T(), 2.0) + (72.0 * C()) - (58.0 * eSquare) ) * Math.pow(A(), 3.0))/120.0) ));

		return (0.9996 * N() * second) + 500000.0;
	}

	/**Return the UMT easting in radians*/
	public double getUTMNorting(){
		double first = (N() * Math.tan(ro));
		double second = ( (Math.pow(A(), 2.0)/2.0) + ( (5.0 - T() + (9.0* C()) + (4.0 * Math.pow(C(), 2.0)) ) * Math.pow(A(), 4.0))/24.0 );
		double third = ( (( 61.0 - (58.0 * T()) + Math.pow(T(), 2.0) + (600.0 * C()) - (330.0 * eSquare) ) * Math.pow(A(), 6.0))/720.0 );
		return 0.9996 * (M() + first * ( second + third ));
	}
}
