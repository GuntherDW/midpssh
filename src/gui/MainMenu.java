/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import gui.session.macros.MacroSetsMenu;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import app.Main;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MainMenu extends List implements CommandListener, Activatable {

	private static Command selectCommand = new Command( "Select", Command.ITEM, 1 );

	private static Command quitCommand = new Command( "Quit", Command.EXIT, 2 );

	private SessionsMenu sessionsMenu;
	
	private MacroSetsMenu macrosMenu;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public MainMenu() {
		super( "FloydSSHx", List.IMPLICIT );

		append( "Sessions", null );
		append( "Macros", null );
		append( "Settings", null );
		append( "About FloydSSHx", null );
		append( "Help", null );
		append( "Quit", null );

		addCommand( selectCommand );
		addCommand( quitCommand );

		setCommandListener( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable displayed ) {
		if ( command == List.SELECT_COMMAND || command == selectCommand ) {
			doSelect( getSelectedIndex() );
		}
		else if ( command == quitCommand ) {
			doQuit();
		}
	}

	private void doSelect( int i ) {
		switch ( i ) {
			case 0:
				doSessions();
				break;
			case 1:
				doMacros();
				break;
			case 5:
				doQuit();
				break;
		}
	}

	private void doSessions() {
		if ( sessionsMenu == null ) {
			sessionsMenu = new SessionsMenu();
		}
		sessionsMenu.activate( this );
	}
	
	private void doMacros() {
		if ( macrosMenu == null ) {
			macrosMenu = new MacroSetsMenu();
		}
		macrosMenu.activate( this );
	}

	private void doQuit() {
		Main.quitApp();
	}
	/* (non-Javadoc)
	 * @see app.Activatable#activate()
	 */
	public void activate() {
		Main.setDisplay( this );
	}
	/* (non-Javadoc)
	 * @see gui.Activatable#activate(gui.Activatable)
	 */
	public void activate( Activatable back ) {
		activate();
	}
}