package utils;

import java.util.regex.Pattern;

public class GpggaBox extends MessageBox {

    private String filter;

    public GpggaBox(){
        this.filter = "$GPGGA";
    }

    private boolean filter(String data){
        String[] splitten = data.split( Pattern.quote( "," ) );
        boolean ret = false;
        if( filter.equals(splitten[0]) ){
            ret = true;
        }
        return ret;
    }

    @Override
    public void call( String data ){
        if( this.filter(data) ){
            System.out.println("Got data > " + data);
            GpggaMessage message = new GpggaMessage( data );
            if( !message.isFixedData() ){
                System.out.println("No fixed data!!");
            }else{
                UTMConverter.setup(
                    message.getLatitude(),
                    message.getLatitudeMinutes(),
                    message.getLongitude(),
                    message.getLongitudeMinutes(),
                    message.getLongitudeOrientation() == GpggaMessage.Orientation.WEST
                );
                System.out.println("Calculated: Norting -> " + UTMConverter.getUMTNorting() + " Easting -> " + UTMConverter.getUMTEasting());
            }
        }
    }
}
