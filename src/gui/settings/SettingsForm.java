/*
 * Created on Jan 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.settings;

import gui.EditableForm;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import app.SettingsManager;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class SettingsForm extends EditableForm {

	protected static final Command saveCommand = new Command( "Save", Command.OK, 1 );

	protected static final Command defaultCommand = new Command( "Default", Command.ITEM, 10 );

	/**
	 * @param title
	 */
	public SettingsForm(String title) {
		super(title);
		
		addCommand( saveCommand );
		addCommand( defaultCommand );
	}
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable arg1 ) {
		if ( command == saveCommand ) {
			save( false );
		}
		else if ( command == defaultCommand ) {
			save( true );
		}
		else {
			super.commandAction( command, arg1 );
		}
	}
	
	private void save( boolean doDefault ) {
		boolean ok = doSave( doDefault );
		if ( ok ) {
			SettingsManager.saveSettings( );
			doBack();
		}
	}
	
	protected abstract boolean doSave( boolean doDefault );
	
}