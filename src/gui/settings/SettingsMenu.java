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
