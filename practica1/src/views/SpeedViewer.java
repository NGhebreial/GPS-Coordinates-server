package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import utils.Coordinate;

public class SpeedViewer extends JFrame {

	private Double cSpeed;
	private Coordinate coordinate;
	private Double rSpeed;

	private JLabel red;
	private JLabel yellow;
	private JLabel green;
	private JLabel cSpeedInfo;
	private JLabel rSpeedInfo;
	private JLabel orientationInfo;

	public SpeedViewer(){
		super("Speed information");

		cSpeed = rSpeed = new Double( 0.0 );
		coordinate = Coordinate.NORTH;
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		this.setPreferredSize(new Dimension(1000, 500));
		this.red = new JLabel(); 
		this.yellow = new JLabel();
		this.green = new JLabel();
		this.cSpeedInfo = new JLabel();
		this.rSpeedInfo = new JLabel();
		this.orientationInfo = new JLabel();
		//TODO
		this.red.setBackground(Color.RED);
		red.setText("");
		red.setPreferredSize(new Dimension(50, 50));
		this.yellow.setBackground(Color.YELLOW);
		yellow.setPreferredSize(new Dimension(50, 50));
		this.green.setBackground(Color.green);
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
	}
	public static void main( String[] args ){
		new SpeedViewer();
	}
}
