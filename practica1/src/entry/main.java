package entry;

import utils.GpggaReceiver;
import utils.MessageBox;

public class main {

    public static void main( String[] args ){
        GpggaReceiver receiver = new GpggaReceiver( new MessageBox() {
            @Override
            public void call( String data ){
                System.out.println("Got data > " + data);
            }
        });
        receiver.start();
        try {
            receiver.join();
        }catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }
}
