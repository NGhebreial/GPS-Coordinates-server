package entry;

import utils.Controller;

public class main {

    public static void main( String[] args ){
        Controller c = new Controller("192.168.1.131", 9090, 8978);
        c.start();
    }
}
