package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class GpggaReceiver extends Thread {

    private Socket connection;
    private MessageBox box;

    public GpggaReceiver(MessageBox box){
        this("localhost", 9090, box);
    }

    public GpggaReceiver(String addr, MessageBox box){
        this(addr, 9090, box);
    }

    public GpggaReceiver(String addr, int port, MessageBox box){
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
            }catch( IOException eSystem.out.println("IOException " + e.getMessage()); ) {
                e.printStackTrace();
            }
        }
    }
}
