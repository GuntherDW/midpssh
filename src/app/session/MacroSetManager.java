/* This file is part of "MidpSSH".
 * Copyright (c) 2004 Karl von Randow.
 * 
 * MidpSSH is based upon Telnet Floyd and FloydSSH by Radek Polak.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 *
 */
package app.session;

import gui.session.macros.MacroSet;

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

import app.BytewiseRecordComparator;

/**
 * @author Karl von Randow
 *
 */
public class MacroSetManager {
	
	private static final String RMS_NAME = "macros";
	
	private static Vector macroSets;
	
	public static Vector getMacroSets() {
		if ( macroSets == null ) {
			try {
				RecordStore rec = RecordStore.openRecordStore( RMS_NAME, false );
				RecordEnumeration recs = rec.enumerateRecords( null, new BytewiseRecordComparator(), false );
				Vector macroSets = new Vector();

				while ( recs.hasNextElement() ) {
					byte[] data = recs.nextRecord();
					DataInputStream in = new DataInputStream( new ByteArrayInputStream( data ) );
					MacroSet macroSet = new MacroSet();
					try {
						macroSet.read( in );
						macroSets.addElement( macroSet );
					}
					catch ( IOException e ) {
						e.printStackTrace();
					}
					in.close();
				}
				rec.closeRecordStore();
				MacroSetManager.macroSets = macroSets;
			}
			catch ( RecordStoreFullException e ) {
				e.printStackTrace();
			}
			catch ( RecordStoreNotFoundException e ) {
				// Start with an empty Vector
				macroSets = new Vector();
			}
			catch ( RecordStoreException e ) {
				e.printStackTrace();
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
		}
		return macroSets;
	}

	public static void saveMacroSets() {
		if ( macroSets != null ) {
			try {
				try {
					RecordStore.deleteRecordStore( RMS_NAME );
				}
				catch ( RecordStoreNotFoundException e1 ) {

				}

				RecordStore rec = RecordStore.openRecordStore( RMS_NAME, true );
				for ( int i = 0; i < macroSets.size(); i++ ) {
					MacroSet macroSet = (MacroSet) macroSets.elementAt( i );
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					DataOutputStream dout = new DataOutputStream( out );
					try {
						macroSet.write( dout );
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
	 * @param i
	 * @return
	 */
	public static MacroSet getMacroSet( int i ) {
		if ( i < 0 )
			return null;
		Vector macroSets = getMacroSets();
		if ( macroSets == null || i >= macroSets.size() )
			return null;
		return (MacroSet) macroSets.elementAt( i );
	}

	/**
	 * @param conn
	 */
	public static void addMacroSet( MacroSet macroSet ) {
		Vector macroSets = getMacroSets();
		if ( macroSets == null ) {
			macroSets = new Vector();
		}
		macroSets.addElement( macroSet );
		saveMacroSets();
	}

	/**
	 * @param i
	 */
	public static void deleteMacroSet( int i ) {
		if ( i < 0 )
			return;
		Vector macroSets = getMacroSets();
		if ( macroSets == null || i >= macroSets.size() )
			return;
		macroSets.removeElementAt( i );
		saveMacroSets();
	}

	/**
	 * @param connectionIndex
	 * @param conn
	 */
	public static void replaceMacroSet( int i, MacroSet macroSet ) {
		if ( i < 0 )
			return;
		Vector macroSets = getMacroSets();
		if ( macroSets == null )
			macroSets = new Vector();
		if ( i >= macroSets.size() ) {
			macroSets.addElement( macroSet );
		}
		else {
			macroSets.setElementAt( macroSet, i );
		}
		saveMacroSets();
	}
}
