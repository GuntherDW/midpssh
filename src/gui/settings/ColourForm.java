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
package gui.settings;

import gui.EditableForm;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;

/**
 * @author Karl von Randow
 */
public abstract class ColourForm extends EditableForm {

	private static Command saveCommand = new Command( "Save", Command.OK, 1 );

	private static Command defaultCommand = new Command( "Default", Command.ITEM, 10 );
	
	protected TextField tfRed = new TextField( "Red", "", 3, TextField.NUMERIC );
	
	protected TextField tfGreen = new TextField( "Green", "", 3, TextField.NUMERIC );
	
	protected TextField tfBlue = new TextField( "Blue", "", 3, TextField.NUMERIC );
	
	public ColourForm( String title ) {
		super( title );
		
		append( tfRed );
		append( tfGreen );
		append( tfBlue );
		
		addCommand( saveCommand );
		addCommand( defaultCommand );
	}
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable arg1 ) {
		if ( command == saveCommand ) {
			doSave();
		}
		else if ( command == defaultCommand ) {
			doDefault();
		}
		else {
			super.commandAction( command, arg1 );
		}
	}
	
	protected abstract void doSave();
	
	protected abstract void doDefault();
	
	protected int parseColour() {
		try {
			int color = Integer.parseInt( tfRed.getString() ) << 16 | Integer.parseInt( tfGreen.getString() ) << 8 |
				Integer.parseInt( tfBlue.getString() );
			return color;
		}
		catch ( NumberFormatException e ) {
			showErrorMessage( "Please fill in the Red, Green and Blue fields with numbers representing the appropriate colour." );
			return -1;
		}
	}
	/**
	 * @param color
	 */
	protected void initColour( int color ) {
		int red = ( color >> 16 ) & 255;
		int green = ( color >> 8 ) & 255;
		int blue = color & 255;
		tfRed.setString( "" + red );
		tfGreen.setString( "" + green );
		tfBlue.setString( "" + blue );
	}
}
