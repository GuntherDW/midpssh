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

import gui.Activatable;
import gui.ExtendedList;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import app.Main;

/**
 * @author Karl von Randow
 */
public class SettingsMenu extends ExtendedList implements Activatable, CommandListener {

	private static Command selectCommand = new Command( "Select", Command.ITEM, 1 );
	
	private static Command backCommand = new Command( "Back", Command.BACK, 2 );
	
	protected static final int SETTINGS_OPTIONS = 2;
	
	private Activatable back;
	
	protected SettingsMenu( String title ) {
		super( title, List.IMPLICIT );
		
		append( "Background Colour", null );
		append( "Foreground Colour", null );
		append( "Screen Size", null );
		
		setSelectCommand( selectCommand );
		addCommand( backCommand );
		
		setCommandListener( this );
	}
	
	public SettingsMenu() {
		this( "Settings" );
	}
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable displayable ) {
		if ( command == selectCommand || command == List.SELECT_COMMAND ) {
			doSelect( getSelectedIndex() );
		}
		else if ( command == backCommand ) {
			doBack();
		}
	}
	
	protected void doSelect( int i ) {
		switch ( i ) {
			case 0:
				ColourForm backForm = new BackColourForm();
				backForm.activate( this );
				break;
			case 1:
				ColourForm foreForm = new ForeColourForm();
				foreForm.activate( this );
				break;
			case 2:
				ScreenSizeForm screenSizeForm = new ScreenSizeForm();
				screenSizeForm.activate( this );
				break;
		}
	}
	
	private void doBack() {
		back.activate();
	}
	
	/* (non-Javadoc)
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		Main.setDisplay( this );
	}
	/* (non-Javadoc)
	 * @see gui.Activatable#activate(gui.Activatable)
	 */
	public void activate( Activatable back ) {
		this.back = back;
		activate();
	}
}
