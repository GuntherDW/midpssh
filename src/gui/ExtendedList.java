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

package gui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;

/**
 * @author Karl von Randow
 * 
 */
public class ExtendedList extends List {
	/**
	 * @param arg0
	 * @param arg1
	 */
	public ExtendedList( String arg0, int arg1 ) {
		super( arg0, arg1 );
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 */
	public ExtendedList( String arg0, int arg1, String[] arg2, Image[] arg3 ) {
		super( arg0, arg1, arg2, arg3 );
	}

	public void deleteAll() {
		while ( size() > 0 ) {
			delete( size() - 1 );
		}
	}
	
	public void setSelectCommand( Command command ) {
		try {
			super.setSelectCommand( command );
		}
		catch ( Throwable t ) {
			// MIDP 1.0 TODO does this actually happen on MIDP 1.0 devices?
			t.printStackTrace();
			addCommand( command );
		}
	}
}