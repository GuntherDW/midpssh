/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ConnectionManager {
    private static Vector connections;

    public static Vector getConnections() {
        if ( connections == null ) {
            try {
                RecordStore rec = RecordStore.openRecordStore( "connections",
                        false );
                RecordEnumeration recs = rec.enumerateRecords( null, null,
                        false );
                Vector connections = new Vector();

                while ( recs.hasNextElement() ) {
                    byte[] data = recs.nextRecord();
                    DataInputStream in = new DataInputStream(
                            new ByteArrayInputStream( data ) );
                    ConnectionSpec conn = new ConnectionSpec();
                    try {
                        conn.read( in );
                        connections.addElement( conn );
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    in.close();
                }
                rec.closeRecordStore();
                ConnectionManager.connections = connections;

            }
            catch ( RecordStoreFullException e ) {
                e.printStackTrace();
            }
            catch ( RecordStoreNotFoundException e ) {
                // Start with an empty Vector
                connections = new Vector();
            }
            catch ( RecordStoreException e ) {
                e.printStackTrace();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return connections;
    }

    private static void saveConnections() {
        if ( connections != null ) {
            try {
                try {
                    RecordStore.deleteRecordStore( "connections" );
                }
                catch ( RecordStoreNotFoundException e1 ) {

                }

                RecordStore rec = RecordStore.openRecordStore( "connections",
                        true );
                for ( int i = 0; i < connections.size(); i++ ) {
                    ConnectionSpec conn = (ConnectionSpec) connections
                            .elementAt( i );
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    DataOutputStream dout = new DataOutputStream( out );
                    try {
                        conn.write( dout );
                        dout.close();

                        byte[] data = out.toByteArray();
                        rec.addRecord( data, 0, data.length );
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                }

                rec.closeRecordStore();

            }
            catch ( RecordStoreFullException e ) {
                e.printStackTrace();
            }
            catch ( RecordStoreNotFoundException e ) {
                e.printStackTrace();
            }
            catch ( RecordStoreException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param conn
     */
    public static void addConnection( ConnectionSpec conn ) {
        Vector connections = getConnections();
        if ( connections == null ) {
            connections = new Vector();
        }
        connections.addElement( conn );
        saveConnections();
    }

    /**
     * @param i
     * @return
     */
    public static ConnectionSpec getConnection( int i ) {
        if ( i < 0 )
            return null;
        Vector connections = getConnections();
        if ( connections == null || i >= connections.size() )
            return null;
        return (ConnectionSpec) connections.elementAt( i );
    }

    /**
     * @param i
     */
    public static void deleteConnection( int i ) {
        if ( i < 0 )
            return;
        Vector connections = getConnections();
        if ( connections == null || i >= connections.size() )
            return;
        connections.removeElementAt( i );
        saveConnections();
    }

    /**
     * @param connectionIndex
     * @param conn
     */
    public static void replaceConnection( int i, ConnectionSpec conn ) {
        if ( i < 0 )
            return;
        Vector connections = getConnections();
        if ( connections == null )
            connections = new Vector();
        if ( i >= connections.size() ) {
            connections.addElement( conn );
        }
        else {
            connections.setElementAt( conn, i );
        }
        saveConnections();
    }
}