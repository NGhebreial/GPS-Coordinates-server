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
    private boolean debug = false;

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
        Double latMin = (double)Math.round( msg.getLatitudeMinutes() );
        Double latSec = msg.getLatitudeMinutes() - latMin;
        Double lonMin = (double)Math.round( msg.getLongitudeMinutes() );
        Double lonSec = msg.getLongitudeMinutes() - lonMin;

        Double lat = (msg.getLatitude() + (latMin / 60.0) + (latSec / 3600.0));
        lat = (msg.getLatitudeOrientation() == GpggaMessage.Orientation.SOUTH ? -1 : 1) * lat;

        Double lon = (msg.getLongitude() + (lonMin / 60.0) + (lonSec / 3600.0));
        lon = (msg.getLongitudeOrientation() == GpggaMessage.Orientation.WEST ? -1 : 1) * lon;

        if( debug ){
            System.out.println("Prepare" +
                    "\noriginal: lat " + msg.getLatitude() + " latmin " + msg.getLatitudeMinutes() + " lon " + msg.getLongitude() + " lonmin " + msg.getLongitudeMinutes() +
                    "\nparsed: latMin " + latMin + " latSec " + latSec + " lonMin " + lonMin + " lonSec " + lonSec +
                    "\nsending: " + lat + " lon " + lon
            );
        }
        // Get Center and bounds
        return "{ \"point\" : { " +
                    "\"latitude\":" + lat + "," +
                    "\"longitude\":" + lon +
                "}}";
    }

    private void sendBuffer(){
        if( !this.buffer.isEmpty() && !this.pool.isEmpty() ){
            for( GpggaMessage msg : this.buffer ){
                System.out.println("Sending message");
                String toSend = prepareMessage( msg );
                for( WebSocket conn : this.pool ) {
                    conn.send( toSend );
                }
            }
            cleanBuffer();
        }else{
            System.out.println("Empty buffer");
        }
    }

    private void sendBuffer( GpggaMessage msg ){
        if( !this.pool.isEmpty() ){
            System.out.println("Sending message");
            String toSend = prepareMessage( msg );
            for( WebSocket conn : this.pool ) {
                conn.send( toSend );
            }
        }else{
            System.out.println("Empty buffer");
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
            if( this.pool.size() > 0 && !this.buffer.isEmpty() ){
                // sendBuffer();
            // }else{
            //     cleanBuffer();
            }
        }
    }

    @Override
    public void onConnectionAdded( WebSocket connection ){
        pool.add( connection );
        System.out.println("Adding connection total: " + pool.size());
    }

    @Override
    public void onConnectionRemoved( WebSocket connection ){
        pool.remove( connection );
    }

    public void apendToBuffer(GpggaMessage msg){
        if( msg.isValid() && msg.isFixedData() ){
            System.out.println( "Add to buffer" );
            this.buffer.add( msg );
            this.sendBuffer( msg );
        }
    }
    public void stopServing(){ this.serving.set( false ); }
    public boolean isServing(){ return this.serving.get(); }
}
