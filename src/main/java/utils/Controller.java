package utils;

import views.MapViewer;

import javax.swing.*;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class Controller extends Thread {

	private MapViewer map;
	private GpggaReceiver<GpggaBox> receiver;
	private CoordsServer server;

	private GpggaBox box;

	private CoordsCalculator coordsCalc;

	private Semaphore semaphore;

	public Controller(String addr, int port, int coordServerPort){
		semaphore = new Semaphore( 0 );
	    doMap(semaphore);
		box = new GpggaBox();
		receiver = new GpggaReceiver<GpggaBox>(addr, port, box);
		this.server = new CoordsServer( coordServerPort );
	}
	
	public CoordsCalculator initCoords(){
		UTMConverter utm = new UTMConverter();
		utm.setup(40.387835, 0.0, 3.633741, 0.0, true);
		//utm.setup(40.392132, 0.0, 3.638561, 0.0, true);
		double imUpX = utm.getUMTNorting();
		double imUpY = utm.getUMTEasting();
        utm.setup(40.385732, 0.0, 3.630539, 0.0, true);
		//utm.setup(40.386382, 0.0, 3.620250, 0.0, true);
		double imDownX = utm.getUMTNorting();
		double imDownY = utm.getUMTEasting();

		return new CoordsCalculator(imUpX, imUpY, imDownX, imDownY, map.getImWidth(), map.getHeight());
	}
	
	@Override
	public void run() {
		box.setChain(new MessageBox() {

			@Override
			public void call(Object data) {
				//Received data in UTM format
				UTMConverter utm = (UTMConverter) data;
				coordsCalc = initCoords();
				HashMap<String, Integer> coodsCalculated = coordsCalc.translatetoInt(utm.getUMTNorting(), true, utm.getUMTEasting(), utm.isWestLongitude());
				map.drawPointer(coodsCalculated.get("x"), coodsCalculated.get("y"));
			}
		});
		box.setRawChain( new MessageBox() {
            @Override
            public void call( Object data ){
                server.apendToBuffer( (GpggaMessage) data );
            }
        } );
        try {
            semaphore.acquire();
			System.out.println("Starting receiver and coords server");
			this.server.start();
            this.receiver.start();
        }catch( InterruptedException e ) {
            e.printStackTrace();
        }
	}

	private void doMap( final Semaphore semaphore){
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run(){
				map = new MapViewer(semaphore);
			}
		});
	}
}
