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

import app.Settings;
import app.SettingsManager;

/**
 * @author Karl von Randow
 */
public class ScreenSizeForm extends EditableForm {

	private static Command saveCommand = new Command( "Save", Command.OK, 1 );

	private static Command defaultCommand = new Command( "Default", Command.ITEM, 10 );
	
	protected TextField tfCols = new TextField( "Columns", "", 3, TextField.NUMERIC );
	
	protected TextField tfRows = new TextField( "Rows", "", 3, TextField.NUMERIC );
	
	public ScreenSizeForm() {
		super( "Screen Size" );
		
		append( tfCols );
		append( tfRows );
		
		addCommand( saveCommand );
		addCommand( defaultCommand );
	}
	/* (non-Javadoc)
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		Settings settings = SettingsManager.getSettings();
		int cols = settings.screenColumns;
		int rows = settings.screenRows;
		
		if ( cols > 0 ) {
			tfCols.setString( "" + cols );
		}
		else {
			tfCols.setString( "" );
		}
		if ( rows > 0 ) {
			tfRows.setString( "" + rows );
		}
		else {
			tfRows.setString( "" );
		}
		
		super.activate();
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
	
	private void doSave() {
		Settings settings = SettingsManager.getSettings();
		
		int cols = 0, rows = 0;
		try {
			cols = Integer.parseInt( tfCols.getString() );
		}
		catch ( NumberFormatException e ) {
			
		}
		try {
			rows = Integer.parseInt( tfRows.getString() );
		}
		catch ( NumberFormatException e ) {
			
		}
		
		settings.screenColumns = cols;
		settings.screenRows = rows;
		SettingsManager.saveSettings( settings );
		
		doBack();
	}
	
	private void doDefault() {
		Settings settings = SettingsManager.getSettings();
		
		settings.screenColumns = 0;
		settings.screenRows = 0;
		SettingsManager.saveSettings( settings );
		
		doBack();
	}
}
