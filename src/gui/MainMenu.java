/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

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
public class MainMenu extends List implements CommandListener {

	private static Command selectCommand = new Command("Select", Command.ITEM,
			1);

	private static Command quitCommand = new Command("Quit", Command.EXIT, 2);

	private ConnectMenu connectMenu = new ConnectMenu(this);

	/**
	 * @param arg0
	 * @param arg1
	 */
	public MainMenu() {
		super("FloydSSHx", List.IMPLICIT);

		append("Connect", null);
		append("Settings", null);
		append("About FloydSSHx", null);
		append("Help", null);
		append("Quit", null);

		addCommand(selectCommand);
		addCommand(quitCommand);

		setCommandListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command command, Displayable displayed) {
		if (command == List.SELECT_COMMAND || command == selectCommand) {
			doSelect(getSelectedIndex());
		} else if (command == quitCommand) {
			doQuit();
		}
	}

	private void doSelect(int i) {
		switch (i) {
		case 0:
			doConnect();
			break;
		case 4:
			doQuit();
			break;
		}
	}

	private void doConnect() {
		Main.setDisplay(connectMenu);
	}

	private void doQuit() {
		Main.quitApp();
	}
}