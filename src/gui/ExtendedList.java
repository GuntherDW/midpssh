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
	public ExtendedList( String title, int mode ) {
		super( title, mode );
	}

//#ifndef midp2
	public void deleteAll() {
		while ( size() > 0 ) {
			delete( size() - 1 );
		}
	}
//#endif
	
	public void setSelectCommand( Command command ) {
//#ifdef midp2
	    super.setSelectCommand( command );
//#else
//#ifndef blackberry
	    // On the blackberry we don't require the command to be added as the implicit command is also listed in
	    // the menu.
	    addCommand( command );
//#endif
//#endif
	}
}