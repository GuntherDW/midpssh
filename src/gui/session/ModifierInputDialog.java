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
package gui.session;

import gui.Activatable;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import app.Main;
import app.session.Session;

/**
 * @author Karl von Randow
 * 
 */
public class ModifierInputDialog extends TextBox implements Activatable, CommandListener {

	private static Command enterCommand = new Command( "Type", Command.OK, 1 );

	private static Command backCommand = new Command( "Back", Command.BACK, 2 );

	private Activatable back;

	private int modifier;

	public ModifierInputDialog( String title, int modifier ) {
		super( title, "", 10, TextField.ANY );

		this.modifier = modifier;
		
		addCommand( enterCommand );
		addCommand( backCommand );

		setCommandListener( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		Main.setDisplay( this );
	}
	
	public void activate( Activatable back ) {
		this.back = back;
		activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable arg1 ) {
		if ( command == enterCommand ) {
			String str = getString();
			Session session = Main.currentSession();
			if ( session != null ) {
				for ( int i = 0; i < str.length(); i++ ) {
					session.typeChar( str.charAt( i ), modifier );
				}
			}
		}

		back.activate();
	}
}