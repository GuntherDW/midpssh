/*
 * Created on Sep 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import terminal.Terminal;
import terminal.vt320;

import app.Activatable;
import app.Main;
import app.session.Session;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SessionTerminal extends Terminal implements Activatable,
		CommandListener {

	private static Command disconnectCommand = new Command("Close", Command.STOP, 100);
	private static Command textInputCommand = new Command("Input (5)", Command.ITEM, 10);
	private static Command tabCommand = new Command( "Tab", Command.ITEM, 20 );
	private static Command ctrlCommand = new Command( "CTRL", Command.ITEM, 21 );

	private Session session;

	private InputDialog inputDialog;
	private ModifierInputDialog modifierInputDialog;

	/**
	 * @param buffer
	 */
	public SessionTerminal(vt320 buffer, Session session) {
		super(buffer);

		this.session = session;
		addCommand(disconnectCommand);
		addCommand(textInputCommand);
		addCommand(tabCommand);
		addCommand(ctrlCommand);

		setCommandListener(this);
	}

	public void commandAction(Command command, Displayable displayable) {
		if (command == disconnectCommand) {
			session.disconnect();
		} else if (command == textInputCommand) {
			doTextInput();
		} else if ( command == tabCommand ) {
			buffer.keyTyped( 0, '\t', 0 );
		} else if ( command == ctrlCommand ) {
			doModifierInput( vt320.KEY_CONTROL );
		}
	}

	protected void keyPressed(int keycode) {
		switch (keycode) {
		case Canvas.KEY_NUM5:
			doTextInput();
			break;
		}
	}

	private void doTextInput() {
		if (inputDialog == null) {
			inputDialog = new InputDialog(this, buffer);
		}
		inputDialog.activate();
	}
	
	private void doModifierInput( int modifier ) {
		if ( modifierInputDialog == null ) {
			modifierInputDialog = new ModifierInputDialog( this, buffer );
		}
		modifierInputDialog.modifier = modifier;
		modifierInputDialog.activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		Main.setDisplay(this);
	}
}