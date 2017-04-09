package entry;

import utils.Controller;

public class main {

	public static void main( String[] args ){
    	Controller c = new Controller("192.168.1.132", 9090);
		c.start();
	}
}
