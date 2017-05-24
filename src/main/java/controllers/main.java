package controllers;

public class main {

	public static void main( String[] args ){
		Controller c = new Controller("192.168.43.1", 9090, 8080, args.length > 0 );
		c.start();
	}
}
