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
public class Settings extends MyRecordStore {
	
	public static final int DEFAULT_BGCOLOR = 0x000000, DEFAULT_FGCOLOR = 0xffffff;
    
    public static final int ROT_NORMAL = 0;
    
    public static final int ROT_270 = 1;
    
    public static final int ROT_90 = 2;
    
    public static final int FONT_NORMAL = 0;
    
    public static final int FONT_SMALL = 1;
    
    public static final int FONT_MEDIUM = 2;
    
    public static final int FONT_LARGE = 3;
    
    private static final String RMS_NAME = "settings";
    
	
	public static int bgcolor = DEFAULT_BGCOLOR, fgcolor = DEFAULT_FGCOLOR;
	
	public static int terminalCols, terminalRows;
	
	public static String terminalType = "";

	public static int terminalRotated = ROT_NORMAL;
    
    public static int fontMode = FONT_NORMAL;
	
	private static Settings me = new Settings();
	
	public static void init() {
		me.load( RMS_NAME, false );
	}

	/**
	 * @param settings2
	 */
	public static void saveSettings() {
		Vector v = new Vector();
		v.addElement( null );
		me.save( RMS_NAME, v );
	}
	
    /* (non-Javadoc)
     * @see app.MyRecordStore#read(java.io.DataInputStream)
     */
    protected Object read(DataInputStream in) throws IOException {
    	fgcolor = in.readInt();
		bgcolor = in.readInt();
		terminalCols = in.readInt();
		terminalRows = in.readInt();
		terminalType = in.readUTF();
		terminalRotated = in.readInt();
        fontMode = in.readInt();
        return null;
    }
    
    /* (non-Javadoc)
     * @see app.MyRecordStore#write(java.io.DataOutputStream, java.lang.Object)
     */
    protected void write(DataOutputStream out, Object ob) throws IOException {
    	out.writeInt( fgcolor );
		out.writeInt( bgcolor );
		out.writeInt( terminalCols );
		out.writeInt( terminalRows );
		out.writeUTF( terminalType );
		out.writeInt( terminalRotated );
        out.writeInt( fontMode );
    }
}
