package gpgga;

import utils.MessageBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GpggaReceiver <T extends MessageBox> extends Thread {

    private Socket connection;
    private T box;
    private String addr;
    private int port;

    public GpggaReceiver(T box){
        this("localhost", 9090, box);
    }

    public GpggaReceiver(String addr, T box){
        this(addr, 9090, box);
    }

    public GpggaReceiver(String addr, int port, T box){
        this.addr = addr;
        this.port = port;
        this.box = box;
    }

    public void connect(){
        try {
            this.connection = new Socket(addr, port);
        }catch( IOException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        String read = "";
        if( connection != null && connection.isConnected() ){
            try {
                BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while( connection.isConnected() && (read = buffer.readLine()) != null ){
                    box.call(read);
                }
            }catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
