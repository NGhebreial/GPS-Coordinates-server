package controllers;

public class main {

	public static void main( String[] args ){
    	Controller c = new Controller("192.168.43.1", 9090);
		c.start();
	}
}
