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

package app;

import gui.MainMenu;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import app.session.Session;

public class Main extends MIDlet {

	private static Main instance;

	public static boolean useColors;

	private static MainMenu mainMenu = new MainMenu();
	
	private static Session currentSession;
    
    private static boolean paused;
	
	/** Constructor */
	public Main() {
		instance = this;
		useColors = getDisplay().isColor();
		Settings.init();
	}

	/** Main method */
	protected void startApp() {
        //if ( !paused ) {
            setDisplay( mainMenu );
        //}
	}

	/** Handle pausing the MIDlet */
	protected void pauseApp() {
        paused = true;
	}

	/** Handle destroying the MIDlet */
	protected void destroyApp( boolean unconditional ) {
        paused = false;
	}

	/** Quit the MIDlet */
	public static void quitApp() {
		instance.destroyApp( true );
		instance.notifyDestroyed();
		instance = null;
	}

	public static void setDisplay( Displayable display ) {
		instance.getDisplay().setCurrent( display );
	}

	public static void alert( Alert alert, Displayable back ) {
		instance.getDisplay().setCurrent( alert, back );
	}

	public Display getDisplay() {
		return Display.getDisplay( this );
	}

	/**
	 *  
	 */
	public static void goMainMenu() {
		setDisplay( mainMenu );
	}

	/**
	 * @param alert
	 */
	public static void alertBackToMain( Alert alert ) {
		instance.getDisplay().setCurrent( alert, mainMenu );
	}
	
	public static void openSession( Session session ) {
		currentSession = session;
		session.activate();
	}
	
	public static Session currentSession() {
		return currentSession;
	}
}