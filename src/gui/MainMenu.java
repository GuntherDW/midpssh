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
package gui;

import gui.session.macros.MacroSetsMenu;
import gui.settings.SettingsMenu;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import app.Main;

/**
 * @author Karl von Randow
 * 
 */
public class MainMenu extends ExtendedList implements CommandListener, Activatable {

	private static Command selectCommand = new Command( "Select", Command.ITEM, 1 );

	private static Command quitCommand = new Command( "Quit", Command.EXIT, 2 );

	private SessionsMenu sessionsMenu;
	
	private MacroSetsMenu macrosMenu;

	/**
	 * @param arg0
	 * @param arg1
	 */
	public MainMenu() {
		super( "MidpSSH", List.IMPLICIT );

		append( "Sessions", null );
		append( "Macros", null );
		append( "Settings", null );
		append( "About MidpSSH", null );
		append( "Help", null );
		append( "Quit", null );

		setSelectCommand( selectCommand );
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
			case 2:
				doSettings();
				break;
			case 3:
				doAbout();
				break;
			case 4:
				doHelp();
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
	
	private void doSettings() {
		new SettingsMenu().activate( this );
	}

	private void doAbout() {
		new MessageForm( "About MidpSSH",
				"MidpSSH is a Telnet and SSH application for J2ME compatible devices.\n\n" +
				"Please visit the project website for more information:\n" +
				"http://www.xk72.com/midpssh/\n\n" +
				"MidpSSH is developed by Karl von Randow. MidpSSH is based upon " +
				"FloydSSH and Telnet Floyd by Radek Polak.\n\n" +
				"MidpSSH is distributed under the GPL licence. For more information please " +
				"visit the website." 
				).activate( this );
	}
	
	private void doHelp() {
		new MessageForm( "MidpSSH Help",
				"Connecting\n\n" +
				"To connect to a remote server choose the Sessions option " +
				"from the main menu - create a new session, entering in the host and other details. " +
				"You can then connect to that server by choosing the new session.\n\n" +
				
				"Problems Connecting\n\n" +
				"Check that your Internet settings for Java are configured for " +
				"Internet access rather than WAP access, and that your provider allows Internet access " +
				"from your device.\n\n" +
				
				"More Information\n\n" +
				"For more information please visit the project website http://www.xk72.com/midpssh/"
				).activate( this );
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