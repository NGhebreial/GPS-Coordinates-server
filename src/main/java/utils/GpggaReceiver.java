package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GpggaReceiver <T extends MessageBox> extends Thread {

    private Socket connection;
    private T box;

    public GpggaReceiver(T box){
        this("localhost", 9090, box);
    }

    public GpggaReceiver(String addr, T box){
        this(addr, 9090, box);
    }

    public GpggaReceiver(String addr, int port, T box){
        try {
            this.connection = new Socket(addr, port);
            this.box = box;
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
