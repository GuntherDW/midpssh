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

/**
 * @author Karl von Randow
 */
public class Settings {
	
	public static final int DEFAULT_BGCOLOR = 0x000000, DEFAULT_FGCOLOR = 0xffffff;
	
	public int bgcolor, fgcolor;
	
	public int screenColumns, screenRows;
	
	public Settings() {
		defaults();
	}

	/**
	 * @param in
	 */
	public void read( DataInputStream in ) throws IOException {
		fgcolor = in.readInt();
		bgcolor = in.readInt();
		screenColumns = in.readInt();
		screenRows = in.readInt();
	}
	
	public void write( DataOutputStream out ) throws IOException {
		out.writeInt( fgcolor );
		out.writeInt( bgcolor );
		out.writeInt( screenColumns );
		out.writeInt( screenRows );
	}
	
	private void defaults() {
		fgcolor = DEFAULT_FGCOLOR;
		bgcolor = DEFAULT_BGCOLOR;
		screenColumns = 0;
		screenRows = 0;
	}
}
