package utils;

import views.MapViewer;

import javax.swing.*;
import java.util.HashMap;

public class Controller extends Thread {

	private MapViewer map;
	private GpggaReceiver<GpggaBox> receiver;

	private GpggaBox box;

	private CoordsCalculator coordsCalc;

	public Controller(String addr, int port){
		doMap();
		box = new GpggaBox();
		receiver = new GpggaReceiver<GpggaBox>(addr, port, box);
	}
	
	public CoordsCalculator initCoords(){
		UTMConverter utm = new UTMConverter();
		utm.setup(40.392132, 0.0, 3.638561, 0.0, true);
		double imUpX = utm.getUMTNorting();
		double imUpY = utm.getUMTEasting();

		utm.setup(40.386382, 0.0, 3.620250, 0.0, true);
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
		receiver.start();
	}

	private void doMap(){
		SwingUtilities.invokeLater( new Runnable() {
			@Override
			public void run(){
				map = new MapViewer();
			}
		});
	}
}
