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
public class SessionManager {
	
	private static final String RMS_NAME = "connections";
	
	private static Vector sessions;

	public static Vector getSessions() {
		if ( sessions == null ) {
			try {
				RecordStore rec = RecordStore.openRecordStore( RMS_NAME, false );
				RecordEnumeration recs = rec.enumerateRecords( null, null, false );
				Vector connections = new Vector();

				while ( recs.hasNextElement() ) {
					byte[] data = recs.nextRecord();
					DataInputStream in = new DataInputStream( new ByteArrayInputStream( data ) );
					SessionSpec conn = new SessionSpec();
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
				SessionManager.sessions = connections;

			}
			catch ( RecordStoreFullException e ) {
				e.printStackTrace();
			}
			catch ( RecordStoreNotFoundException e ) {
				// Start with an empty Vector
				sessions = new Vector();
			}
			catch ( RecordStoreException e ) {
				e.printStackTrace();
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		return sessions;
	}

	private static void saveSessions() {
		if ( sessions != null ) {
			try {
				try {
					RecordStore.deleteRecordStore( RMS_NAME );
				}
				catch ( RecordStoreNotFoundException e1 ) {

				}

				RecordStore rec = RecordStore.openRecordStore( RMS_NAME, true );
				for ( int i = 0; i < sessions.size(); i++ ) {
					SessionSpec conn = (SessionSpec) sessions.elementAt( i );
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
	public static void addSession( SessionSpec conn ) {
		Vector connections = getSessions();
		if ( connections == null ) {
			connections = new Vector();
		}
		connections.addElement( conn );
		saveSessions();
	}

	/**
	 * @param i
	 * @return
	 */
	public static SessionSpec getSession( int i ) {
		if ( i < 0 )
			return null;
		Vector connections = getSessions();
		if ( connections == null || i >= connections.size() )
			return null;
		return (SessionSpec) connections.elementAt( i );
	}

	/**
	 * @param i
	 */
	public static void deleteSession( int i ) {
		if ( i < 0 )
			return;
		Vector connections = getSessions();
		if ( connections == null || i >= connections.size() )
			return;
		connections.removeElementAt( i );
		saveSessions();
	}

	/**
	 * @param connectionIndex
	 * @param conn
	 */
	public static void replaceSession( int i, SessionSpec conn ) {
		if ( i < 0 )
			return;
		Vector connections = getSessions();
		if ( connections == null )
			connections = new Vector();
		if ( i >= connections.size() ) {
			connections.addElement( conn );
		}
		else {
			connections.setElementAt( conn, i );
		}
		saveSessions();
	}
}