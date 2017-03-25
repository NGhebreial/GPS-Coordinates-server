package entry;

import utils.GpggaBox;
import utils.GpggaReceiver;

public class main {

    public static void main( String[] args ){
        GpggaReceiver receiver = new GpggaReceiver("192.168.1.134", 9090, new GpggaBox());
        receiver.start();
        try {
            receiver.join();
        }catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
