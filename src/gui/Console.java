package gui;

/* This file is part of "Telnet Floyd".
 *
 * (c) Radek Polak 2003-2004. All Rights Reserved.
 *
 * Please visit project homepage at http://phoenix.inf.upol.cz/~polakr
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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

/**
 * Debugging console. Use Telnet.console.append(String s) for printing text to
 * this console.
 */

public class Console extends List implements CommandListener {

	public Console() {
		super( "Console", List.IMPLICIT );
		setCommandListener( this );
		addCommand( new Command( "Exit", Command.EXIT, 1 ) );
	}

	public void commandAction( Command command, Displayable displayable ) {
		while ( this.size() > 0 )
			delete( 0 );
		//Main.setDisplay(Main.terminal);
	}

	public void append( String s ) {
		if ( s != null ) {
			append( s, null );
		}
	}
}