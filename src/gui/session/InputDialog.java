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
public class InputDialog extends TextBox implements Activatable, CommandListener {

	private static Command enterCommand = new Command( "Enter", Command.OK, 1 );

	private static Command typeCommand = new Command( "Type", Command.ITEM, 2 );

	private static Command tabCommand = new Command( "TAB", Command.ITEM, 3 );

	private static Command backCommand = new Command( "Back", Command.BACK, 4 );

	private Activatable back;

	public InputDialog() {
		super( "Input", "", 255, TextField.ANY );

		addCommand( enterCommand );
		addCommand( typeCommand );
		addCommand( tabCommand );
		addCommand( backCommand );

		setCommandListener( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		setString( "" );
		Main.setDisplay( this );
	}

	/* (non-Javadoc)
	 * @see gui.Activatable#activate(gui.Activatable)
	 */
	public void activate( Activatable back ) {
		this.back = back;
		activate();
	}
	
	private StringBuffer commandBuffer = new StringBuffer();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable arg1 ) {
		if ( command != backCommand ) {
			Session session = Main.currentSession();
			if ( session != null ) {
			    commandBuffer.setLength( 0 );
			    commandBuffer.append( getString() );
				if ( command == enterCommand ) {
				    commandBuffer.append( '\n' );
				}
				if ( command == tabCommand ) {
				    commandBuffer.append( '\t' );
				}
				session.typeString( commandBuffer.toString() );
				session.activate();
			}
			
		}
		
		back.activate();
	}
}