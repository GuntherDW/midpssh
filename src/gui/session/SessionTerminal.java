/*
 * Created on Sep 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session;

import gui.Activatable;
import gui.session.macros.MacroSetsMenu;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import terminal.KeyEvent;
import terminal.Terminal;
import terminal.vt320;
import app.Main;
import app.session.Session;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SessionTerminal extends Terminal implements Activatable, CommandListener {

	private static final int MODE_DISCONNECTED = 0;
	
	private static final int MODE_CONNECTED = 1;

	private static final int MODE_CURSOR = 2;

	private static final Command textInputCommand = new Command( "Input", Command.ITEM, 10 );

	private static final Command macrosCommand = new Command( "Macros", Command.ITEM, 11 );

	private static final Command cursorCommand = new Command( "Cursor", Command.ITEM, 15 );

	private static final Command scrollCommand = new Command( "Scroll", Command.ITEM, 16 );

	private static final Command tabCommand = new Command( "TAB", Command.ITEM, 20 );

	private static final Command enterCommand = new Command( "ENTER", Command.ITEM, 21 );

	private static final Command escCommand = new Command( "ESC", Command.ITEM, 30 );

	private static final Command backspaceCommand = new Command( "BACKSPACE", Command.ITEM, 31 );

	private static final Command ctrlCommand = new Command( "CTRL", Command.ITEM, 50 );

	private static final Command altCommand = new Command( "ALT", Command.ITEM, 51 );

	private static final Command disconnectCommand = new Command( "Close", Command.STOP, 100 );

	private static final Command backCommand = new Command( "Back", Command.BACK, 90 );

	private static final Command[] commandsDisconnected = new Command[] {
			disconnectCommand
	};
	
	private static final Command[] commandsConnected = new Command[] {
			textInputCommand, macrosCommand, cursorCommand, scrollCommand,
			tabCommand, enterCommand, escCommand, backspaceCommand,
			ctrlCommand, altCommand, 
			disconnectCommand
	};

	private static final Command[] commandsCursor = new Command[] {
		backCommand
	};

	private Session session;

	private InputDialog inputDialog;
	
	private MacroSetsMenu macrosMenu;

	private ModifierInputDialog modifierInputDialog;

	private Command[] currentCommands;

	private int mode;

	/**
	 * @param buffer
	 */
	public SessionTerminal( vt320 buffer, Session session ) {
		super( buffer );
		this.session = session;

		changeMode( MODE_DISCONNECTED );

		setCommandListener( this );
	}
	
	public void connected() {
		changeMode( MODE_CONNECTED );
	}
	
	public void disconnected() {
		changeMode( MODE_DISCONNECTED );
	}

	protected void changeMode( int mode ) {
		this.mode = mode;

		switch ( mode ) {
			case MODE_DISCONNECTED:
				changeCurrentCommands( commandsDisconnected );
				break;
			case MODE_CONNECTED:
				changeCurrentCommands( commandsConnected );
				break;
			case MODE_CURSOR:
				changeCurrentCommands( commandsCursor );
				break;
		}
	}

	protected void changeCurrentCommands( Command[] commands ) {
		if ( currentCommands != null ) {
			for ( int i = 0; i < currentCommands.length; i++ ) {
				removeCommand( currentCommands[i] );
			}
		}

		for ( int i = 0; i < commands.length; i++ ) {
			addCommand( commands[i] );
		}

		this.currentCommands = commands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		Main.setDisplay( this );
	}
	
	public void activate( Activatable back ) {
		activate();
	}

	public void commandAction( Command command, Displayable displayable ) {
		if ( command == disconnectCommand ) {
			doDisconnect();
		}
		else if ( command == textInputCommand ) {
			doTextInput();
		}
		else if ( command == macrosCommand ) {
			doMacros();
		}
		else if ( command == tabCommand ) {
			buffer.keyTyped( 0, '\t', 0 );
		}
		else if ( command == enterCommand ) {
			buffer.keyTyped( 0, '\n', 0 );
		}
		else if ( command == escCommand ) {
			buffer.keyTyped( 0, (char) 27, 0 );
		}
		else if ( command == backspaceCommand ) {
			buffer.keyTyped( 0, '\b', 0 );
		}
		else if ( command == ctrlCommand ) {
			doModifierInput( vt320.KEY_CONTROL );
		}
		else if ( command == altCommand ) {
			doModifierInput( vt320.KEY_ALT );
		}
		else if ( command == cursorCommand ) {
			doCursor();
		}
		else if ( command == backCommand ) {
			changeMode( MODE_CONNECTED );
		}
	}

	protected void keyPressed( int keycode ) {
		switch ( mode ) {
			case MODE_CONNECTED:
				keyPressedConnected( keycode );
				break;
			case MODE_CURSOR:
				keyPressedCursor( keycode );
				break;
		}
	}

	protected void keyPressedConnected( int keycode ) {
		int index = -1;
		
		// Map keys to actions
		if ( keycode >= Canvas.KEY_NUM1 && keycode <= Canvas.KEY_NUM9 ) {
			index = keycode - Canvas.KEY_NUM1;
		}
		else {
			switch ( keycode ) {
				case Canvas.KEY_STAR:
					index = 10;
					break;
				case Canvas.KEY_NUM0:
					index = 11;
					break;
				case Canvas.KEY_POUND:
					index = 12;
					break;
			}
		}
		
		if ( index >= 0 && index < commandsConnected.length ) {
			commandAction( commandsConnected[index], this );
		}
	}

	protected void keyPressedCursor( int keycode ) {
		// Convert game actions to keys
		int gameAction = getGameAction( keycode );
		switch ( gameAction ) {
			case Canvas.UP:
				keycode = Canvas.KEY_NUM2;
				break;
			case Canvas.DOWN:
				keycode = Canvas.KEY_NUM8;
				break;
			case Canvas.LEFT:
				keycode = Canvas.KEY_NUM4;
				break;
			case Canvas.RIGHT:
				keycode = Canvas.KEY_NUM6;
				break;
		}
		
		switch ( keycode ) {
			case Canvas.KEY_NUM2:
				buffer.keyPressed( KeyEvent.VK_UP, (char) 65535, vt320.KEY_ACTION );
				break;
			case Canvas.KEY_NUM8:
			case Canvas.KEY_NUM0:
				buffer.keyPressed( KeyEvent.VK_DOWN, (char) 65535, vt320.KEY_ACTION );
				break;
			case Canvas.KEY_NUM4:
				buffer.keyPressed( KeyEvent.VK_LEFT, (char) 65535, vt320.KEY_ACTION );
				break;
			case Canvas.KEY_NUM6:
				buffer.keyPressed( KeyEvent.VK_RIGHT, (char) 65535, vt320.KEY_ACTION );
				break;
			case Canvas.KEY_NUM1:
				keyPressedCursor( Canvas.UP );
				keyPressedCursor( Canvas.LEFT );
				break;
			case Canvas.KEY_NUM3:
				keyPressedCursor( Canvas.UP );
				keyPressedCursor( Canvas.RIGHT );
				break;
			case Canvas.KEY_NUM7:
			case Canvas.KEY_STAR:
				keyPressedCursor( Canvas.DOWN );
				keyPressedCursor( Canvas.LEFT );
				break;
			case Canvas.KEY_NUM9:
			case Canvas.KEY_POUND:
				keyPressedCursor( Canvas.DOWN );
				keyPressedCursor( Canvas.RIGHT );
				break;
		}
	}

	private void doDisconnect() {
		Main.goMainMenu();
		session.disconnect();
	}
	
	private void doTextInput() {
		if ( inputDialog == null ) {
			inputDialog = new InputDialog( buffer );
		}
		inputDialog.activate( this );
	}
	
	private void doMacros() {
		if ( macrosMenu == null ) {
			macrosMenu = new MacroSetsMenu();
		}
		macrosMenu.activate( this );
	}

	private void doModifierInput( int modifier ) {
		if ( modifierInputDialog == null ) {
			modifierInputDialog = new ModifierInputDialog( buffer );
		}
		modifierInputDialog.modifier = modifier;
		modifierInputDialog.activate();
	}

	private static final Alert doCursorAlert = new Alert( "Cursor Mode",
			"Move the cursor using the stick or numeric keys.", null, AlertType.INFO );

	private static boolean doneCursorAlert = false;

	private void doCursor() {
		if ( !doneCursorAlert ) {
			Main.setDisplay( doCursorAlert );
			doneCursorAlert = true;
		}
		changeMode( MODE_CURSOR );
	}
}