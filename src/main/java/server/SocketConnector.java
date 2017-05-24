package server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class SocketConnector extends WebSocketServer {

    private OnConnectionAdded pool;
    private boolean ready;
    private AtomicBoolean listening;

    public SocketConnector( InetSocketAddress address, OnConnectionAdded connPool ){
        super(address);
        this.ready = false;
        this.listening = new AtomicBoolean( false );
        this.pool = connPool;
    }

    @Override
    public void onStart(){
        System.out.println("WebSocket OnStart");
        this.ready = true;
        this.listening.set( true );
    }

    @Override
    public void onOpen( WebSocket webSocket, ClientHandshake clientHandshake ){
        System.out.println("SocketConnector 'onOpen' " + clientHandshake.getResourceDescriptor());
        if( this.listening.get() ){
            this.pool.onConnectionAdded( webSocket );
        }
    }

    @Override
    public void onClose( WebSocket webSocket, int i, String s, boolean b ){
        System.out.println("SocketConnector 'onClose'");
        this.pool.onConnectionRemoved( webSocket );
    }

    @Override
    public void onMessage( WebSocket webSocket, String s ){
        System.out.println("SocketConnector 'onMessage'");
        // TODO
    }

    @Override
    public void onError( WebSocket webSocket, Exception e ){
        this.pool.onConnectionRemoved( webSocket );
    }

    public interface OnConnectionAdded {
        void onConnectionAdded(WebSocket connection);
        void onConnectionRemoved(WebSocket connection);
    }

    public void stopListening(){
        this.listening.set( false );
    }
    public synchronized boolean isReady(){ return this.ready ; }
    public boolean isListening(){ return this.listening.get(); }
}
