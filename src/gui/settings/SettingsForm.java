/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
public class SettingsForm extends ExtendedList implements Activatable, CommandListener {

	private static Command selectCommand = new Command( "Select", Command.ITEM, 1 );
	
	private static Command backCommand = new Command( "Back", Command.BACK, 2 );
	
	private Activatable back;
	
	public SettingsForm() {
		super( "Settings", List.IMPLICIT );
		
		append( "Back Colour", null );
		append( "Fore Colour", null );
		
		setSelectCommand( selectCommand );
		addCommand( backCommand );
		
		setCommandListener( this );
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
	
	private void doSelect( int i ) {
		switch ( i ) {
			case 0:
				ColourForm form = new BackColourForm();
				form.activate( this );
				break;
			case 1:
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
