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
package app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

/**
 * @author Karl von Randow
 */
public class SettingsManager {
	
	private static final String RMS_NAME = "settings";
	
	private static Settings settings;
	
	public static Settings getSettings() {
		if ( settings == null ) {
			Settings settings = new Settings();
			try {
				RecordStore rec = RecordStore.openRecordStore( RMS_NAME, false );
				RecordEnumeration recs = rec.enumerateRecords( null, null, false );

				if ( recs.hasNextElement() ) {
					byte[] data = recs.nextRecord();
					DataInputStream in = new DataInputStream( new ByteArrayInputStream( data ) );
					try {
						settings.read( in );
					}
					catch ( IOException e ) {
						// If an IOException occurs we've read off the end of the settings or an invalid format,
						// just ignore it.
					}
					in.close();
				}
				rec.closeRecordStore();
			}
			catch ( RecordStoreFullException e ) {
				e.printStackTrace();
			}
			catch ( RecordStoreNotFoundException e ) {
			}
			catch ( RecordStoreException e ) {
				e.printStackTrace();
			}
			catch ( IOException e ) {
				e.printStackTrace();
			}
			SettingsManager.settings = settings;
		}
		return settings;
	}

	/**
	 * @param settings2
	 */
	public static void saveSettings( Settings settings ) {
		try {
			try {
				RecordStore.deleteRecordStore( RMS_NAME );
			}
			catch ( RecordStoreNotFoundException e1 ) {

			}

			RecordStore rec = RecordStore.openRecordStore( RMS_NAME, true );
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dout = new DataOutputStream( out );
			try {
				settings.write( dout );
				dout.close();

				byte[] data = out.toByteArray();
				rec.addRecord( data, 0, data.length );
			}
			catch ( IOException e ) {
				e.printStackTrace();
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
		
		SettingsManager.settings = settings;
	}
}
