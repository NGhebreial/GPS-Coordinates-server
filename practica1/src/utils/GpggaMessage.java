package utils;

import java.util.regex.Pattern;

public class GpggaMessage {

    // Raw message
    private String rawMsg = "";
    // GPS Message type, gpgga
    private String type = MSG_TYPE;
    // Time the message was taken at
    private double time = -1;
    // Current Latitude
    private double latitude = -1;
    // Min Latitude
    private double latitudeMinutes = -1;
    // Latitude Orientation
    private Orientation latitudeOrientation = Orientation.NORTH;
    // Current Longitude
    private double longitude = -1;
    // Min Longitude
    private double longitudeMinutes = -1;
    // Longitude Orientation
    private Orientation longitudeOrientation = Orientation.WEST;
    // Data quality
    /**
     *  Fix quality:
     *  0 = invalid
     *  1 = GPS fix (SPS)
     *  2 = DGPS fix
     *  3 = PPS fix
     *  4 = Real Time Kinematic
     *  5 = Float RTK
     *  6 = estimated (dead reckoning) (2.3 feature)
     */
    private int quality = -1;
    // Number of satellites
    private int noSatellites = -1;
    // Horizontal dilution of position
    private double horDilution = -1;
    // Altitude, Meters, above mean sea level (two fields)
    private double altitudeAboveSea = -1;
    // Height of geoid (mean sea level) above WGS84 ellipsoid (two fields)
    private double geoidHeight = -1;
    // Time in seconds since last DGPS update
    private int dgpsElapsedTime = -1;
    // DGPS station ID number
    private int dgpsStationId = -1;
    // Checksum data, always begins with *
    private String checksum = "";
    // Fixed data available
    private boolean fixedData = false;
    private boolean validData = false;

    public enum Orientation { NORTH, SOUTH, EAST, WEST }

    public static final String MSG_TYPE = "GPGGA";

    public GpggaMessage( String rawMsg){
        this.rawMsg = rawMsg;
        this.type = MSG_TYPE;
        // Tokenize msg
        String [] tokens = rawMsg.split( Pattern.quote( "," ) );
        if( !("$GPGGA").equalsIgnoreCase(tokens[0]) ){
            // Not a gpgga packet
            this.fixedData = false;
        }else{
            this.setQuality( tokens.length > 6? tokens[ 6 ]:"" );
            if( this.quality <= 0 || !isValid( rawMsg ) ){
                // No fixed data
                this.fixedData = false;
            }else{
                this.fixedData = true;
                // Parse date ??
                this.setTime( tokens[ 1 ] );
                this.setLatitude( tokens[ 2 ] );
                this.setLatitudeOrientation( tokens[ 3 ] );
                this.setLongitude( tokens[ 4 ] );
                this.setLongitudeOrientation( tokens[ 5 ] );

                this.setNoSatellites( tokens[ 7 ] );
                this.setHorDilution( tokens[ 8 ] );
                this.setAltitudeAboveSea( tokens[ 9 ] );
                // Skip one field - Meters
                this.setGeoidHeight( tokens[ 11 ] );
                // Skip one field - Meters
                // this.setDgpsElapsedTime(tokens[12]);
                // this.setDgpsStationId(tokens[12]);

                this.checksum = tokens[14];
                this.validData = isValid(tokens[14]);
            }
        }
    }

    private boolean isValid(String rawMsg){
        boolean valid = true;
        byte[] bytes = rawMsg.getBytes();
        int checksumIndex = rawMsg.indexOf("*");
        byte checksumCalcValue = 0;
        int checksumValue;

        if( (rawMsg.charAt(0) != '$') || (checksumIndex == -1) ){
            valid = false;
        }else{
            String val = rawMsg.substring(checksumIndex + 1, rawMsg.length()).trim();
            checksumValue = Integer.parseInt(val, 16);
            for(int i = 1; i < checksumIndex; i++){
                checksumCalcValue = (byte) (checksumCalcValue ^ bytes[i]);
            }
            if( checksumValue != checksumCalcValue ){
                valid = false;
            }
        }
        return valid;
    }

    public String getRawMsg(){
        return rawMsg;
    }

    public String getType(){
        return type;
    }

    public double getTime(){
        return time;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLatitudeMinutes(){
        return latitudeMinutes;
    }

    public Orientation getLatitudeOrientation(){
        return latitudeOrientation;
    }

    public double getLongitude(){
        return longitude;
    }

    public double getLongitudeMinutes(){
        return longitudeMinutes;
    }

    public Orientation getLongitudeOrientation(){
        return longitudeOrientation;
    }

    public int getQuality(){
        return quality;
    }

    public int getNoSatellites(){
        return noSatellites;
    }

    public double getHorDilution(){
        return horDilution;
    }

    public double getAltitudeAboveSea(){
        return altitudeAboveSea;
    }

    public double getGeoidHeight(){
        return geoidHeight;
    }

    public int getDgpsElapsedTime(){
        return dgpsElapsedTime;
    }

    public int getDgpsStationId(){
        return dgpsStationId;
    }

    public String getChecksum(){
        return checksum;
    }

    public boolean isFixedData(){
        return fixedData;
    }

    public boolean isValid(){
        return this.isValid( this.rawMsg );
    }

    public void setTime(String time){
        this.time = ("").equals(time) ? -1 : Double.parseDouble( time );
    }

    public void setLatitude(String latitude){
        if( !("").equals(latitude) ){
        	//1 --> minutes
        	String[] latSplit = latitude.split(Pattern.quote("."));
        	//Degrees and minutes            
            String lat1 = latSplit[0].substring(0, latSplit[0].length() - 2);
            String lat2 = latSplit[0].substring(latSplit[0].length() - 2);
            this.latitude = Double.parseDouble( lat1 );
            this.latitudeMinutes = Double.parseDouble( lat2 + "." + latSplit[1].substring(0, 3) );
            
        }
    }

    public void setLatitudeOrientation(String latitudeOrientation){
        switch( latitudeOrientation.toUpperCase().charAt( 0 ) ){
            case 'S':
                this.latitudeOrientation = Orientation.SOUTH;
                break;
            case 'E':
                this.latitudeOrientation = Orientation.EAST;
                break;
            case 'W':
                this.latitudeOrientation = Orientation.WEST;
                break;
            case 'N':
            default:
                this.latitudeOrientation = Orientation.NORTH;
                break;
        }
    }

    public void setLongitude(String longitude){
        if( !("").equals( longitude ) ){
        	//1 --> minutes
        	String[] lonSplit = longitude.split(Pattern.quote("."));
        	//Degrees and minutes            
            String lon1 = lonSplit[0].substring(0, lonSplit[0].length() - 2);
            String lon2 = lonSplit[0].substring(lonSplit[0].length() - 2);
            this.longitude = Double.parseDouble( lon1 );
            this.longitudeMinutes = Double.parseDouble( lon2 + "." + lonSplit[1].substring(0, 3) );
            
        }
    }

    public void setLongitudeOrientation(String longitudeOrientation){
        switch( longitudeOrientation.toUpperCase().charAt( 0 ) ){
            case 'N':
                this.longitudeOrientation = Orientation.NORTH;
                break;
            case 'S':
                this.longitudeOrientation = Orientation.SOUTH;
                break;
            case 'E':
                this.longitudeOrientation = Orientation.EAST;
                break;
            case 'W':
            default:
                this.longitudeOrientation = Orientation.WEST;
                break;
        }
    }

    public void setQuality(String quality){
        this.quality = ("").equals(quality) ? -1 : Integer.parseInt(quality);
    }

    public void setNoSatellites(String noSatellites){
        this.noSatellites = ("").equals(noSatellites) ? -1 : Integer.parseInt(noSatellites);
    }

    public void setHorDilution(String horDilution){
        this.horDilution = ("").equals(horDilution) ? -1 : Double.parseDouble(horDilution);
    }

    public void setAltitudeAboveSea(String altitudeAboveSea){
        this.altitudeAboveSea = ("").equals(altitudeAboveSea) ? -1 : Double.parseDouble(altitudeAboveSea);
    }

    public void setGeoidHeight(String geoidHeight){
        this.geoidHeight = ("").equals(geoidHeight) ? -1 : Double.parseDouble(geoidHeight);
    }

    public void setDgpsElapsedTime(String dgpsElapsedTime){
        this.dgpsElapsedTime = ("").equals(dgpsElapsedTime) ? -1 : Integer.parseInt(dgpsElapsedTime);
    }

    public void setDgpsStationId(String dgpsStationId){
        this.dgpsStationId = ("").equals(dgpsStationId) ? -1 : Integer.parseInt(dgpsStationId);
    }

    @Override
    public String toString(){
        return (
            "rawMsg: " + rawMsg +
            "\nfixedData: " + fixedData +
            "\ntype: " + type +
            "\ntime: " + time +
            "\nlatitude: " + latitude +
            "\nlatitudeMinutes: " + latitudeMinutes +
            "\nlatitudeOrientation: " + latitudeOrientation +
            "\nlongitude: " + longitude +
            "\nlongitudeMinutes: " + longitudeMinutes +
            "\nlongitudeOrientation: " + longitudeOrientation +
            "\nquality: " + quality +
            "\nnoSatellites: " + noSatellites +
            "\nhorDilution: " + horDilution +
            "\naltitudeAboveSea: " + altitudeAboveSea +
            "\ngeoidHeight: " + geoidHeight +
            "\ndgpsElapsedTime: " + dgpsElapsedTime +
            "\ndgpsStationId: " + dgpsStationId
        );
    }
    
}
