/*
 * Created on Sep 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
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

    private static final int MODE_NORMAL = 0;

    private static final int MODE_CURSOR = 1;

    private static final Command textInputCommand = new Command( "Input",
            Command.ITEM, 10 );

    private static final Command cursorCommand = new Command( "Cursor",
            Command.ITEM, 15 );

    private static final Command scrollCommand = new Command( "Scroll",
            Command.ITEM, 16 );

    private static final Command tabCommand = new Command( "TAB", Command.ITEM,
            20 );

    private static final Command ctrlCommand = new Command( "CTRL",
            Command.ITEM, 21 );

    private static final Command altCommand = new Command( "ALT", Command.ITEM,
            22 );

    private static final Command enterCommand = new Command( "ENTER",
            Command.ITEM, 23 );

    private static final Command disconnectCommand = new Command( "Close",
            Command.STOP, 100 );

    private static final Command backCommand = new Command( "Back",
            Command.BACK, 90 );

    private static final Command[] commandsNormal = new Command[] {
            textInputCommand, cursorCommand, scrollCommand, tabCommand,
            ctrlCommand, altCommand, enterCommand, disconnectCommand
    };

    private static final Command[] commandsCursor = new Command[] {
        backCommand
    };

    private Session session;

    private InputDialog inputDialog;

    private ModifierInputDialog modifierInputDialog;

    private Command[] currentCommands;

    private int mode;

    /**
     * @param buffer
     */
    public SessionTerminal( vt320 buffer, Session session ) {
        super( buffer );
        this.session = session;

        changeMode( MODE_NORMAL );

        setCommandListener( this );
    }

    protected void changeMode( int mode ) {
        this.mode = mode;

        switch ( mode ) {
            case MODE_NORMAL:
                changeCurrentCommands( commandsNormal );
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

    public void commandAction( Command command, Displayable displayable ) {
        if ( command == disconnectCommand ) {
            session.disconnect();
        }
        else if ( command == textInputCommand ) {
            doTextInput();
        }
        else if ( command == tabCommand ) {
            buffer.keyTyped( 0, '\t', 0 );
        }
        else if ( command == enterCommand ) {
            buffer.keyTyped( 0, '\n', 0 );
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
            changeMode( MODE_NORMAL );
        }
    }

    protected void keyPressed( int keycode ) {
        switch ( keycode ) {
            case Canvas.KEY_NUM5:
                doTextInput();
                break;
        }
    }

    private void doTextInput() {
        if ( inputDialog == null ) {
            inputDialog = new InputDialog( this, buffer );
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

    private static final Alert doCursorAlert = new Alert( "Cursor",
            "Move the cursor using stick or 2,5,6,8 keys", null, AlertType.INFO );

    private void doCursor() {
        Main.setDisplay( doCursorAlert );
        changeMode( MODE_CURSOR );
    }
}