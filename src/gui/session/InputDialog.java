/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
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
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class InputDialog extends TextBox implements Activatable, CommandListener {

	private static Command enterCommand = new Command( "Enter", Command.OK, 1 );

	private static Command typeCommand = new Command( "Type", Command.ITEM, 2 );

	private static Command backCommand = new Command( "Back", Command.BACK, 3 );

	private Activatable back;

	public InputDialog() {
		super( "Input", "", 255, TextField.ANY );

		addCommand( enterCommand );
		addCommand( typeCommand );
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
				session.typeString( getString() );
				session.activate();
			}
			
			if ( command == enterCommand ) {
				session.typeString( "\n" );
			}
		}
		
		back.activate();
	}
}