package utils;

import java.util.regex.Pattern;

public class GpggaBox extends MessageBox {

    private String filter;
    private MessageBox chain;

    public GpggaBox(){
        this.filter = "$GPGGA";
    }
    
    public GpggaBox(MessageBox msgbox){
        this.filter = "$GPGGA";
        this.chain = msgbox;
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
    public void call( Object d ){
    	String data = (String)d;
        if( this.filter(data) ){
            System.out.println("Got data > " + data);
            GpggaMessage message = new GpggaMessage( data );
            if( !message.isFixedData() ){
                System.out.println("No fixed data!!");
            }else{
            	UTMConverter utm= new UTMConverter(
                    message.getLatitude(),
                    message.getLatitudeMinutes(),
                    message.getLongitude(),
                    message.getLongitudeMinutes(),
                    message.getLongitudeOrientation() == GpggaMessage.Orientation.WEST
                );
            	if( chain != null ){
            		chain.call(utm);
            	}
                System.out.println("Calculated: Norting -> " + utm.getUMTNorting() + " Easting -> " + utm.getUMTEasting());
            }
        }
    }
    
	public void setChain(MessageBox chain) {
		this.chain = chain;
	}
}
