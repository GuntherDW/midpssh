/*
 * Created on Oct 13, 2004
 *
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
 */
public abstract class MyRecordStore {
    
    protected Vector load( String rmsName, boolean sort ) {
		try {
			RecordStore rec = RecordStore.openRecordStore( rmsName, false );
			RecordEnumeration recs = rec.enumerateRecords( null,
			        sort ? new BytewiseRecordComparator() : null,
			                false );
			Vector vector = new Vector();

			while ( recs.hasNextElement() ) {
				byte[] data = recs.nextRecord();
				DataInputStream in = new DataInputStream( new ByteArrayInputStream( data ) );
				try {
					vector.addElement( read( in ) );
				}
				catch ( IOException e ) {
					e.printStackTrace();
				}
				in.close();
			}
			rec.closeRecordStore();
			return vector;
		}
		catch ( RecordStoreFullException e ) {
			e.printStackTrace();
		}
		catch ( RecordStoreNotFoundException e ) {
			// Start with an empty Vector
		}
		catch ( RecordStoreException e ) {
			e.printStackTrace();
		}
		catch ( IOException e ) {
			e.printStackTrace();
		}
		return new Vector();
	}

	protected void save( String rmsName, Vector vector ) {
		if ( vector != null ) {
			try {
				try {
					RecordStore.deleteRecordStore( rmsName );
				}
				catch ( RecordStoreNotFoundException e1 ) {

				}

				RecordStore rec = RecordStore.openRecordStore( rmsName, true );
				for ( int i = 0; i < vector.size(); i++ ) {
					Object ob = vector.elementAt( i );
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					DataOutputStream dout = new DataOutputStream( out );
					try {
						write( dout, ob );
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
    
    protected abstract Object read( DataInputStream in ) throws IOException;
    
    protected abstract void write( DataOutputStream out, Object ob ) throws IOException;
}
