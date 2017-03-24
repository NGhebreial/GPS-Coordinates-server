package utils;

import java.util.regex.Pattern;

public class GpggaMessage {

    // Raw message
    private String rawMsg;
    // GPS Message type, gpgga
    private String type;
    // Time the message was taken at
    private long time;
    // Current Latitude
    private double latitude;
    // Min Latitude
    private double latitudeMinutes;
    // Latitude Orientation
    private Orientation latitudeOrientation;
    // Current Longitude
    private double longitude;
    // Min Longitude
    private double longitudeMinutes;
    // Longitude Orientation
    private Orientation longitudeOrientation;
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
    private int quality;
    // Number of satellites
    private int noSatellites;
    // Horizontal dilution of position
    private double horDilution;
    // Altitude, Meters, above mean sea level (two fields)
    private double altitudeAboveSea;
    // Height of geoid (mean sea level) above WGS84 ellipsoid (two fields)
    private double geoidHeight;
    // Time in seconds since last DGPS update
    private int dgpsElapsedTime;
    // DGPS station ID number
    private int dgpsStationId;
    // Checksum data, always begins with *
    private int checksum;
    // Fixed data available
    private boolean fixedData;

    public enum Orientation { NORTH, SOUTH, EAST, WEST }

    public static final String MSG_TYPE = "GPGGA";

    public GpggaMessage(String rawMsg){
        this.rawMsg = rawMsg;
        this.type = MSG_TYPE;
        // Tokenize msg
        String [] tokens = rawMsg.split( Pattern.quote( "," ) );
        this.quality = Integer.parseInt( tokens[6] );
        if( this.quality == 0 || !isValid( rawMsg ) ){
            // No fixed data
            this.fixedData = false;
        }else{
            this.fixedData = true;
            // Parse date ??
            this.time = Long.parseLong( tokens[1] );
            this.setupLatAndLong(tokens[2], tokens[3], tokens[4], tokens[5]);
            this.noSatellites = Integer.parseInt( tokens[7] );
            this.horDilution = Double.parseDouble( tokens[8] );
            this.altitudeAboveSea = Double.parseDouble( tokens[9] );
            // Skip one field - Meters
            this.geoidHeight = Double.parseDouble( tokens[11] );
            // Skip one field - Meters
            // this.dgpsElapsedTime = ("").equals( tokens[13] ) ? null : Integer.parseInt( tokens[13] );
            // this.dgpsStationId = ("").equals( tokens[13] ) ? null : Integer.parseInt( tokens[13] );
            char [] aux = tokens[14].toCharArray();
            this.checksum = Integer.parseInt( String.copyValueOf(aux, 1, aux.length -2) );
        }
    }

    private void setupLatAndLong(String lat, String latOr, String lon, String lonOr){
        char [] latArr = lat.toCharArray();
        char [] lonArr = lon.toCharArray();
        this.latitude = Double.parseDouble(String.copyValueOf(latArr, 0, 2));
        this.latitudeMinutes = Double.parseDouble(String.copyValueOf(latArr, 2, latArr.length-2));
        this.longitude = Double.parseDouble(String.copyValueOf(lonArr, 0, 2));
        this.longitudeMinutes = Double.parseDouble(String.copyValueOf(lonArr, 2, latArr.length-2));

        switch( latOr.toUpperCase().charAt( 0 ) ){
            case 'N':
                this.latitudeOrientation = Orientation.NORTH;
                break;
            case 'S':
                this.latitudeOrientation = Orientation.SOUTH;
                break;
            case 'E':
                this.latitudeOrientation = Orientation.EAST;
                break;
            case 'W':
                this.latitudeOrientation = Orientation.WEST;
                break;
        }

        switch( lonOr.toUpperCase().charAt( 0 ) ){
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
                this.longitudeOrientation = Orientation.WEST;
                break;
        }
    }

    private boolean isValid(String rawMsg){
        boolean valid = true;
        byte[] bytes = rawMsg.getBytes();
        int checksumIndex = rawMsg.indexOf("*");
        byte checksumCalcValue = 0;
        int checksumValue;

        if( (rawMsg.charAt(0) != '$') || (checksumIndex==-1) ){
            valid = false;
        }
        //
        if( valid ){
            String val = rawMsg.substring(checksumIndex + 1, rawMsg.length()).trim();
            checksumValue = Integer.parseInt(val, 16);
            for (int i = 1; i < checksumIndex; i++){
                checksumCalcValue = (byte) (checksumCalcValue ^ bytes[i]);
            }
            if (checksumValue != checksumCalcValue){
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

    public long getTime(){
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

    public int getChecksum(){
        return checksum;
    }

    public boolean isFixedData(){
        return fixedData;
    }

    @Override
    public String toString(){
        return (
            "rawMsg: " + rawMsg +
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
