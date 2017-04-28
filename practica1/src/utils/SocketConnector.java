package utils;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class SocketConnector extends WebSocketServer {

    private WeakReference<ServerSocket> serverRef;
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
        this.ready = true;
    }

    @Override
    public void onOpen( WebSocket webSocket, ClientHandshake clientHandshake ){
        if( this.listening.get() ){
            this.pool.onConnectionAdded( webSocket );
        }
    }

    @Override
    public void onClose( WebSocket webSocket, int i, String s, boolean b ){
        this.pool.onConnectionRemoved( webSocket );
    }

    @Override
    public void onMessage( WebSocket webSocket, String s ){
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
