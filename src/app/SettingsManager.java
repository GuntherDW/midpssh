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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

/**
 * @author Karl von Randow
 */
public class SettingsManager extends MyRecordStore {
	
	private static final String RMS_NAME = "settings";
	
	private static Settings settings;
	
	private static SettingsManager me = new SettingsManager();
	
	public static Settings getSettings() {
		if ( settings == null ) {
			Vector v = me.load( RMS_NAME, false );
			if ( v != null && !v.isEmpty() ) {
			    settings = (Settings) v.elementAt( 0 );
			}
			else {
			    settings = new Settings();
			}
		}
		return settings;
	}

	/**
	 * @param settings2
	 */
	public static void saveSettings( Settings settings ) {
		Vector v = new Vector();
		v.addElement( settings );
		me.save( RMS_NAME, v );
		
		SettingsManager.settings = settings;
	}
	
    /* (non-Javadoc)
     * @see app.MyRecordStore#read(java.io.DataInputStream)
     */
    protected Object read(DataInputStream in) throws IOException {
        Settings settings = new Settings();
        settings.read( in );
        return settings;
    }
    /* (non-Javadoc)
     * @see app.MyRecordStore#write(java.io.DataOutputStream, java.lang.Object)
     */
    protected void write(DataOutputStream out, Object ob) throws IOException {
        Settings settings = (Settings) ob;
        settings.write( out );
    }
}
