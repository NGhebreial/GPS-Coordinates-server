package gpgga;

import utils.MessageBox;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * Created by Guillermo Echegoyen Blanco on 2016.
 */
public class GpggaEmulator extends Thread {

    private String DATA_FILE = "data/gpggatrace.txt";

    private static final int sleepTime = 500;
    private MessageBox box;
    private boolean cyclic;

    private ArrayList<GpggaMessage> points;

    public GpggaEmulator( boolean onlyFixed, boolean cyclic ){
        this.points = new ArrayList<>();
        this.cyclic = cyclic;
        this.loadDataFile( DATA_FILE, points, onlyFixed );
    }

    public void loadDataFile( String path, ArrayList<GpggaMessage> store, boolean fixed ){
        try {
            Scanner sc = new Scanner( new BufferedInputStream( new FileInputStream( path ) ) ).useLocale( Locale.US);
            while( sc.hasNextLine() ){
                GpggaMessage msg = new GpggaMessage( sc.nextLine() );
                if( !fixed || msg.isFixedData() ){
                    store.add( msg );
                }
            }
        }catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        System.out.println("Gpgga Emulator run");
        int i = 0;
        while( i < this.points.size() ){
            try {
                Thread.sleep( sleepTime );
                box.call( this.points.get(i++) );
            }catch( InterruptedException e ) {
                e.printStackTrace();
            }
            if( this.cyclic && i == this.points.size() ){
                i = 0;
            }
        }
    }

    public void setBox( MessageBox box ){
        this.box = box;
    }

}

