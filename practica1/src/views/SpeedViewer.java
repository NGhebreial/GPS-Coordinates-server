package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import utils.Coordinate;

public class SpeedViewer extends JFrame {

	private Double cSpeed;
	private Coordinate coordinate;
	private Double rSpeed;

	private JButton red;
	private JButton yellow;
	private JButton green;
	private JLabel cSpeedInfo;
	private JLabel rSpeedInfo;
	private JLabel orientationInfo;
	//Colors light
	private float[] greenRGB = Color.RGBtoHSB(182, 242, 189, null);
	private float[] redRGB = Color.RGBtoHSB(250, 190, 190, null);
	private float[] yellowRGB = Color.RGBtoHSB(240, 247, 163, null);
	
	private static final int WIDTH = 650;
	private static final int HEIGHT = 500;
	
	public SpeedViewer( Semaphore semaphore  ){
		super("Speed information");

		cSpeed = rSpeed = new Double( 0.0 );
		coordinate = Coordinate.NORTH;
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        this.setLocation((int) (dimension.getWidth()- WIDTH / 2),
                (int) (dimension.getHeight() / 2 - HEIGHT / 2));
        
		this.red = new JButton(); 
		this.yellow = new JButton();
		this.green = new JButton();
		this.cSpeedInfo = new JLabel();
		this.rSpeedInfo = new JLabel();
		this.orientationInfo = new JLabel();

		resetButtonValues();		
		red.setPreferredSize(new Dimension(50, 50));
		yellow.setPreferredSize(new Dimension(50, 50));
		green.setPreferredSize(new Dimension(50, 50));
		
		cSpeedInfo.setText("Current speed: "+cSpeed);
		rSpeedInfo.setText("Recomended speed: "+rSpeed);
		orientationInfo.setText("Orientation: "+coordinate);

		this.setLayout(new GridLayout(2, 3));
		this.add(green);
		this.add(yellow);
		this.add(red);
		this.add(cSpeedInfo);
		this.add(rSpeedInfo);
		this.add(orientationInfo);

		this.pack();
		this.setVisible( true );
		
		semaphore.release();
	}
	public void refreshInfo(Double cSpeed, Double rSpeed, Coordinate coordinate){
		updateButtonInfo(cSpeed, rSpeed);
		cSpeedInfo.setText("Current speed: "+cSpeed);
		rSpeedInfo.setText("Recomended speed: "+rSpeed);
		orientationInfo.setText("Orientation: "+coordinate);
	}
	private void updateButtonInfo(Double cSpeed, Double rSpeed){		
		double minSpeed = rSpeed - (rSpeed * 0.1);
		double maxSpeed = rSpeed + (rSpeed * 0.1);
		resetButtonValues();
		//Is under recommended speed
		if( cSpeed < minSpeed){
			this.green.setBackground(Color.GREEN);
			this.green.setText(cSpeed+" Km/h");
		}
		//Is over recommended speed
		else if( cSpeed > maxSpeed ){
			this.red.setBackground(Color.RED);
			this.red.setText(cSpeed+" Km/h");
		}
		//Is in range
		else{
			this.yellow.setBackground(Color.YELLOW);
			this.yellow.setText(cSpeed+" Km/h");
		}			
	}
	private void resetButtonValues(){
		this.red.setBackground(Color.getHSBColor(redRGB[0], redRGB[1], redRGB[2]));
		this.yellow.setBackground(Color.getHSBColor(yellowRGB[0], yellowRGB[1], yellowRGB[2]));
		this.green.setBackground(Color.getHSBColor(greenRGB[0], greenRGB[1], greenRGB[2]));
		
		this.red.setText("");
		this.yellow.setText("");
		this.green.setText("");
	}
//	public static void main( String[] args ){
//		SpeedViewer view = new SpeedViewer();
//		//Over recommended
//		view.refreshInfo(100.0, 80.0, Coordinate.NORTH);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//Under
//		view.refreshInfo(100.0, 120.0, Coordinate.NORTH);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//Just
//		view.refreshInfo(100.0, 110.0, Coordinate.EAST);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		
//		view.refreshInfo(120.0, 100.0, Coordinate.NORTHEAST);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//Under
//		view.refreshInfo(80.0, 100.0, Coordinate.SOUTH);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		//Just
//		view.refreshInfo(100.0, 110.0, Coordinate.NORTH);
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
}
