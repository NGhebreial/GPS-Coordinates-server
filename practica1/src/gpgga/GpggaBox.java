package gpgga;

import utils.MessageBox;
import math.UTMConverter;

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
	public synchronized void call( Object d ){
		String data = (String)d;
		if( this.filter(data) ){
			GpggaMessage message = new GpggaMessage( data );
			if( !message.isFixedData() ){
				System.out.println("No fixed data!!");
			}else{
				UTMConverter utm = new UTMConverter();
				utm.setup(  message.getLatitude(),
						message.getLatitudeMinutes(),
						message.getLongitude(),
						message.getLongitudeMinutes(),
						message.getLongitudeOrientation() == GpggaMessage.Orientation.WEST);
				System.out.println("Calculated: Norting -> " + utm.getUTMNorting() + " Easting -> " + utm.getUTMEasting());
				if( chain != null ){
					chain.call(utm);
				}                
			}
		}
	}

	public void setChain(MessageBox chain) {
		this.chain = chain;
	}
}
