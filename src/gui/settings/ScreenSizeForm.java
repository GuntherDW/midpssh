/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.settings;

import gui.EditableForm;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextField;

import app.Settings;
import app.SettingsManager;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ScreenSizeForm extends EditableForm {

	private static Command saveCommand = new Command( "Save", Command.OK, 1 );
	
	protected TextField tfCols = new TextField( "Columns", "", 3, TextField.NUMERIC );
	
	protected TextField tfRows = new TextField( "Rows", "", 3, TextField.NUMERIC );
	
	public ScreenSizeForm() {
		super( "Screen Size" );
		
		append( tfCols );
		append( tfRows );
		
		addCommand( saveCommand );
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
}
