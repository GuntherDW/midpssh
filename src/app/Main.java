package app;

import gui.Console;
import gui.MainMenu;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

/* This file is part of "Telnet Floyd".
 *
 * (c) Radek Polak 2003-2004. All Rights Reserved.
 *
 * Please visit project homepage at http://phoenix.inf.upol.cz/~polakr
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

/**
 * Main class for whole application. Here are all important variables defined as
 * public static members, so that they can be easily accessed. See also method
 * run, that handles main telnet loop.
 */

public class Main extends MIDlet {

	private static Main instance;

	public static Console console = new Console();

	public static boolean useColors;

	private static MainMenu mainMenu = new MainMenu();

	/** Constructor */
	public Main() {
		instance = this;
		useColors = getDisplay().isColor();
	}

	/** Main method */
	public void startApp() {
		setDisplay(mainMenu);
	}

	/** Handle pausing the MIDlet */
	public void pauseApp() {
	}

	/** Handle destroying the MIDlet */
	public void destroyApp(boolean unconditional) {
	}

	/** Quit the MIDlet */
	public static void quitApp() {
		instance.destroyApp(true);
		instance.notifyDestroyed();
		instance = null;
	}

	public static void setDisplay(Displayable display) {
		instance.getDisplay().setCurrent(display);
	}

	public Display getDisplay() {
		return Display.getDisplay(this);
	}

	/**
	 *  
	 */
	public static void goMainMenu() {
		setDisplay(mainMenu);
	}

	/**
	 * @param alert
	 */
	public static void alertBackToMain(Alert alert) {
		instance.getDisplay().setCurrent(alert, mainMenu);
	}
}