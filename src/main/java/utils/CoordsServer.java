package utils;

import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class CoordsServer extends Thread implements SocketConnector.OnConnectionAdded {

    private ServerSocket serverSocket;
    private SocketConnector socketConnector;
    private ArrayBlockingQueue<WebSocket> pool;
    private AtomicBoolean serving;
    private int port;
    private LinkedBlockingQueue<GpggaMessage> buffer;

    public CoordsServer(int port){
        this.port = port;
        this.pool = new ArrayBlockingQueue<WebSocket>(10);
        this.serving = new AtomicBoolean( false );
        this.buffer = new LinkedBlockingQueue<GpggaMessage>();
    }

    private void setup(){
        InetSocketAddress address = new InetSocketAddress( "localhost", this.port );
        this.socketConnector = new SocketConnector(address, this);
    }

    private void printInfo(){
        System.out.println( "Address: " + this.socketConnector.getAddress() );
    }

    private void startListening(){
        this.socketConnector.start();
    }

    private String prepareMessage(GpggaMessage msg){
        // Get Center and bounds
        return "{" +
                "\"latitude\" : " + msg.getLatitude() + ", " +
                "\"latitudeMinutes\" : " + msg.getLatitudeMinutes() + ", " +
                "\"latitudeOrientation\" : \"" + msg.getLatitudeOrientation() + "\", " +
                "\"longitude\" : " + msg.getLongitude() + ", " +
                "\"longitudeMinutes\" : " + msg.getLongitudeMinutes() + ", " +
                "\"longituderientation\" : \"" + msg.getLongitudeOrientation() + "\" " +
                "}";

    }

    private void sendBuffer(){
        if( !this.buffer.isEmpty() && !this.pool.isEmpty() ){
            for( GpggaMessage msg : this.buffer ){
                for(WebSocket conn : this.pool) {
                    conn.send( prepareMessage( msg ) );
                }
            }
        }
    }

    private void cleanBuffer(){
        this.buffer.clear();
    }

    @Override
    public void run(){
        setup();
        printInfo();
        startListening();
        this.serving.set( true );
        System.out.println("Server done, awaiting connections");
        while( this.serving.get() ){
            sendBuffer();
            cleanBuffer();
        }
    }

    @Override
    public void onConnectionAdded( WebSocket connection ){
        pool.add( connection );
    }

    @Override
    public void onConnectionRemoved( WebSocket connection ){
        pool.remove( connection );
    }

    public void apendToBuffer(GpggaMessage msg){
        this.buffer.add( msg );
    }
    public void stopServing(){ this.serving.set( false ); }
    public boolean isServing(){ return this.serving.get(); }
}
