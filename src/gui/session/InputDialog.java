/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;

import app.Activatable;
import app.Main;

import terminal.vt320;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class InputDialog extends TextBox implements Activatable,
		CommandListener {

	private static Command enterCommand = new Command("Enter", Command.OK, 1);

	private static Command typeCommand = new Command("Type", Command.ITEM, 2);

	private vt320 vt;

	private Activatable back;

	public InputDialog(Activatable back, vt320 vt) {
		super("Input", "", 255, TextField.ANY);

		this.back = back;
		this.vt = vt;

		addCommand(enterCommand);
		addCommand(typeCommand);

		setCommandListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		setString("");
		Main.setDisplay(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction(Command command, Displayable arg1) {
		doInput();

		if (command == enterCommand) {
			vt.keyTyped(0, '\n', 0);
		}
		back.activate();
	}

	private void doInput() {
		String str = getString();
		for (int i = 0; i < str.length(); i++) {
			vt.keyTyped(0, str.charAt(i), 0);
		}

	}
}