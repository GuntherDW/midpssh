/* This file is part of "MidpSSH".
 * Copyright (c) 2005 Karl von Randow.
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
package terminal;

import gui.Activatable;
import gui.MainMenu;
import gui.MessageForm;
import gui.session.InputDialog;
import gui.session.ModifierInputDialog;
import gui.session.SpecialMenu;

import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#ifdef midp2
import javax.microedition.lcdui.game.Sprite;
//#endif

import app.Main;
import app.Settings;
import app.session.Session;


/**
 * Class that acts as terminal. It can basicly draw input from emulation (see
 * variable "buffer"), execute and store actions defined by user.
 */

public class Terminal extends Canvas implements Activatable, CommandListener {

    private static final int MODE_DISCONNECTED = 0;
    
    private static final int MODE_CONNECTED = 1;

//#ifndef nocursororscroll
    private static final int MODE_CURSOR = 2;

    private static final int MODE_SCROLL = 3;
//#endif
    
//#ifndef notyping
    private static final int MODE_TYPING = 4;
//#endif
    
    private static int commandPriority = 1;

    // Have this separate back command as a Command.ITEM so that it will show first in the menu on
    // the phone, so that you know you're in typing mode
    private static final Command backMainCommand = new Command( "Back", Command.ITEM, commandPriority++ );
    
    private static final Command textInputCommand = new Command( "Input", Command.ITEM, commandPriority++ );

//#ifndef notyping
    private static final Command typeCommand = new Command( "Type", Command.ITEM, commandPriority++ );
//#endif
    
//#ifndef nomacros
    private static final Command macrosCommand = new Command( "Macros", Command.ITEM, commandPriority++ );
//#endif
    
    private static final Command tabCommand = new Command( "TAB", Command.ITEM, commandPriority++ );

    private static final Command spaceCommand = new Command( "SPACE", Command.ITEM, commandPriority++ );

    private static final Command enterCommand = new Command( "ENTER", Command.ITEM, commandPriority++ );

    private static final Command escCommand = new Command( "ESC", Command.ITEM, commandPriority++ );

    //private static final Command backspaceCommand = new Command( "BACKSPACE", Command.ITEM, commandPriority++ );

    private static final Command ctrlCommand = new Command( "CTRL", Command.ITEM, commandPriority++ );

    private static final Command altCommand = new Command( "ALT", Command.ITEM, commandPriority++ );
    
    private static final Command shiftCommand = new Command( "SHIFT", Command.ITEM, commandPriority++ );

//#ifndef nospecialmenu
    private static final Command specialCommand = new Command( "Special", Command.ITEM, commandPriority++ );
//#endif
    
//#ifndef nocursororscroll
    private static final Command cursorCommand = new Command( "Cursor", Command.ITEM, commandPriority++ );

    private static final Command scrollCommand = new Command( "Scroll", Command.ITEM, commandPriority++ );
//#endif
    
    private static final Command backCommand = new Command( "Back", Command.BACK, commandPriority++ );
    
    private static final Command showBindingsCommand = new Command( "Show Key Bindings", Command.ITEM, commandPriority++ );

    //private static final Command settingsCommand = new Command( "Settings", Command.ITEM, commandPriority++ );
    
    private static final Command disconnectCommand = new Command( "Disconnect", Command.ITEM, commandPriority++ );
    
    private static final Command closeCommand = new Command( "Close", Command.STOP, commandPriority++ );

    private static final Command[] commandsDisconnected = new Command[] {
            closeCommand
    };
    
    private static final Command[] commandsConnected = new Command[] {
        textInputCommand,
//#ifndef notyping
        typeCommand,
//#endif
//#ifndef nomacros
        macrosCommand,
//#endif
        tabCommand,
        spaceCommand,
        enterCommand,
        escCommand, 
        //backspaceCommand,
        ctrlCommand,
        altCommand,
        shiftCommand,
//#ifndef nospecialmenu     
        specialCommand,
//#endif
//#ifndef nocursororscroll
        cursorCommand, scrollCommand,
//#endif
        showBindingsCommand,
        //settingsCommand,
        disconnectCommand
    };

//#ifndef nocursororscroll
    private static final Command[] commandsCursor = new Command[] {
        backCommand
    };
//#endif

//#ifndef notyping
    private static final Command[] commandsTyping = new Command[] {
        backMainCommand,
        backCommand,
        textInputCommand,
//#ifndef nomacros
        macrosCommand,
//#endif
        tabCommand,
        spaceCommand,
        enterCommand,
        escCommand, 
        //backspaceCommand,
        ctrlCommand,
        altCommand,
        shiftCommand,
//#ifndef nospecialmenu
        specialCommand,
//#endif
//#ifndef nocursororscroll
        cursorCommand, scrollCommand,
//#endif
        disconnectCommand
    };
//#endif
    
    private static int [] bindingKeys = new int[] {
            Canvas.KEY_NUM1, Canvas.KEY_NUM2, Canvas.KEY_NUM3,
            Canvas.KEY_NUM4, Canvas.KEY_NUM5, Canvas.KEY_NUM6,
            Canvas.KEY_NUM7, Canvas.KEY_NUM8, Canvas.KEY_NUM9,
            Canvas.KEY_STAR, Canvas.KEY_NUM0, Canvas.KEY_POUND
    };

    private Session session;

    private InputDialog inputDialog;
    
//#ifndef nospecialmenu 
    private SpecialMenu menuSpecialKeys;
//#endif
    
    private ModifierInputDialog controlKeyDialog, altKeyDialog, shiftKeyDialog;

    private Command[] currentCommands;

    private int mode;

    /**
     * @param buffer
     */
    public Terminal( VT320 buffer, Session session ) {
        this.buffer = buffer;
        buffer.setDisplay( this );
        
//#ifdef midp2
        if ( Settings.terminalFullscreen ) {
            setFullScreenMode( true );
        }
//#endif

        if ( Main.useColors ) {
            fgcolor = color[7];
            bgcolor = color[0];
        }

        initFont();

//#ifdef midp2
        rotated = Settings.terminalRotated;
//#else
        rotated = Settings.ROT_NORMAL;
//#endif

        top = 0;
        left = 0;
        
        this.session = session;

        changeMode( MODE_DISCONNECTED );

        setCommandListener( this );
        
        // Settings
        if ( Main.useColors ) {
            bgcolor = Settings.bgcolor;
            /* If specified fgcolor is white then use default fgcolor, which is our off white */
            if (Settings.fgcolor != 0xffffff) {
                fgcolor = Settings.fgcolor;
            }
        }
        
        sizeChanged();
    }

//#ifdef midp2
    /* (non-Javadoc)
     * @see javax.microedition.lcdui.Displayable#sizeChanged(int, int)
     */
    protected void sizeChanged(int w, int h) {
        super.sizeChanged(w, h);
        sizeChanged();
    }
//#endif
    
    protected void sizeChanged() {
        width = getWidth();
        height = getHeight();
        if ( rotated != Settings.ROT_NORMAL ) {
            width = getHeight();
            height = getWidth();
        }
        cols = width / fontWidth;
        rows = height / fontHeight;
        backingStore = Image.createImage( width, height );
        
        int virtualCols = cols;
        int virtualRows = rows;
        
        if ( Settings.terminalCols != 0 ) {
            virtualCols = Settings.terminalCols;
        }
        if ( Settings.terminalRows != 0 ) {
            virtualRows = Settings.terminalRows;
        }
        
        //System.out.println( "ROWS " + virtualRows + " COLS " + virtualCols );
        
        buffer.setScreenSize( virtualCols, virtualRows );
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
//#ifndef nocursororscroll
            case MODE_CURSOR:
            case MODE_SCROLL:
                changeCurrentCommands( commandsCursor );
                break;
//#endif
//#ifndef notyping
            case MODE_TYPING:
                changeCurrentCommands( commandsTyping );
                break;
//#endif
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
        if ( command == disconnectCommand || command == closeCommand ) {
            doDisconnect();
        }
        else if ( command == textInputCommand ) {
            doTextInput();
        }
//#ifndef nomacros
        else if ( command == macrosCommand ) {
            MainMenu.doMacros(this);
        }
//#endif
        else if ( command == tabCommand ) {
            buffer.keyTyped( 0, '\t', 0 );
        }
        else if ( command == spaceCommand ) {
            buffer.keyTyped( 0, ' ', 0 );
        }
        else if ( command == enterCommand ) {
            buffer.keyTyped( 0, '\n', 0 );
        }
        else if ( command == escCommand ) {
            buffer.keyTyped( 0, (char) 27, 0 );
        }
        /*else if ( command == backspaceCommand ) {
            buffer.keyPressed( VT320.VK_BACK_SPACE, 0 );
        }*/
        else if ( command == ctrlCommand ) {
            doControlKeyInput();
        }
        else if ( command == altCommand ) {
            doAltKeyInput();
        }
        else if ( command == shiftCommand ) {
            doShiftKeyInput();
        }
//#ifndef nocursororscroll
        else if ( command == cursorCommand ) {
            doCursor();
        }
        else if ( command == scrollCommand ) {
            doScroll();
        }
//#endif
//#ifndef notyping
        else if ( command == typeCommand ) {
            doTyping();
        }
//#endif
//#ifndef nospecialmenu
        else if ( command == specialCommand ) {
            if ( menuSpecialKeys == null ) {
                menuSpecialKeys = new SpecialMenu();
            }
            menuSpecialKeys.activate( this );
        }
//#endif        
        else if ( command == backCommand  || command == backMainCommand ) {
            changeMode( MODE_CONNECTED );
        }
        else if ( command == showBindingsCommand ) {
            doShowBindings();
        }
        /*else if ( command == settingsCommand ) {
            doSettings();
        }*/
    }

    protected void keyPressed( int keycode ) { 
        switch ( mode ) {
            case MODE_CONNECTED:
                keyPressedConnected( keycode );
                break;
//#ifndef nocursororscroll
            case MODE_CURSOR:
                keyPressedCursor( keycode );
                break;
            case MODE_SCROLL:
                keyPressedScroll( keycode );
                break;
//#endif                
//#ifndef notyping
            case MODE_TYPING:
                keyPressedTyping( keycode );
                break;
//#endif
        }
    }

    protected void keyReleased( int keycode ) {
        switch ( mode ) {
            case MODE_CONNECTED:
                keyReleasedConnected( keycode );
                break;
//#ifndef notyping
            case MODE_TYPING:
                keyReleasedTyping( keycode );
                break;
//#endif
        }
    }

//#ifndef nocursororscroll
    protected void keyRepeated( int keycode ) {
        switch ( mode ) {
            case MODE_CURSOR:
                keyPressedCursor( keycode );
                break;
            case MODE_SCROLL:
                keyPressedScroll( keycode );
                break;
        }
    }
//#endif
    
    protected boolean handleGameAction( int keycode ) {
        int gameAction = getGameAction( keycode );
        
        if ( gameAction != 0 ) {
            switch ( gameAction ) {
            case Canvas.UP:
                buffer.keyPressed( VT320.VK_UP, VT320.KEY_ACTION );
                return true;
            case Canvas.DOWN:
                buffer.keyPressed( VT320.VK_DOWN, VT320.KEY_ACTION );
                return true;
            case Canvas.LEFT:
                buffer.keyPressed( VT320.VK_LEFT, VT320.KEY_ACTION );
                return true;
            case Canvas.RIGHT:
                buffer.keyPressed( VT320.VK_RIGHT, VT320.KEY_ACTION );
                return true;
            }
        }
        return false;
    }

    protected void keyPressedConnected( int keycode ) {
        // If a game action is used, allow it to operate the cursor even when not in cursor mode
        
        boolean handled = false;
        for ( int i = 0; i < bindingKeys.length; i++ ) {
            if ( bindingKeys[i] == keycode ) {
                handled = true;
            }
        }
        if ( keycode == KEY_BACKSPACE ) {
            handled = true;
        }
        
        if ( !handled ) {
            if ( handleGameAction( keycode ) ) return;
        }
    }

    protected void keyReleasedConnected( int keycode ) {
        int index = -1;
        
        for ( int i = 0; i < bindingKeys.length; i++ ) {
            if ( bindingKeys[i] == keycode ) {
                index = i;
                break;
            }
        }
        
        if ( index >= 0 && index < commandsConnected.length ) {
            commandAction( commandsConnected[index], this );
        }
        else {
            if ( keycode == KEY_BACKSPACE ) {
                // Backspace
                buffer.keyPressed( VT320.VK_BACK_SPACE, 0 );
            }
        }
    }

    private static final int KEY_BACKSPACE = -8; // Keycode for clear on sony
    
//#ifndef notyping
    private static final int KEY_SHIFT = 137; // Keycode for shift on blackberry
    
    private boolean typingShift;
    
    protected void keyPressedTyping( int keycode ) {
        if ( keycode == KEY_SHIFT ) {
            typingShift = true;
        }
        
        // If a game action is used, allow it to operate the cursor even when not in cursor mode
        // But need to make sure it's not a character that we might accept for typing
        if ( keycode == 8 || keycode == KEY_BACKSPACE || keycode == 10 || keycode == 13 ||
                keycode == KEY_SHIFT || ( keycode >= 32 && keycode < 128 ) )
        {
            // NOOP in keyPressedTyping
        }
        else {
            if ( handleGameAction( keycode ) ) return;
        }
    }
    
    protected void keyReleasedTyping( int keycode ) {
    	/* Debug typing */
//    	buffer.putString("KEY" + keycode + " ");
    	
        if ( keycode == 8 || keycode == KEY_BACKSPACE ) {
            // Backspace
            buffer.keyPressed( VT320.VK_BACK_SPACE, 0 );
        }
        else if ( keycode == 10 || keycode == 13 ) {
            //buffer.keyTyped( keycode, (char) keycode, 0 );
            buffer.keyTyped( 0, '\n', 0 );
        }
        else if ( keycode == KEY_SHIFT ) {
            typingShift = false;
        }
        else if ( keycode > 0 && keycode < 32) {
            buffer.keyTyped( keycode, (char)keycode, 0);
        }
        else if ( keycode >= 32 && keycode < 128 ) {
            char c = (char) keycode;
            if ( typingShift ) {
                c = shiftChar( c );
            }
            
            // Don't pass through the keycode, as we don't want the terminal to do any keycode mapping
            // we just care about the char
            buffer.keyTyped( 0, c, 0 );
        }
    }
    
    private char shiftChar( char c ) {
        if ( c >= 'a' && c <= 'z' ) {
            return (char) ( c - 'a' + 'A' );
        }
        else {
            switch ( c ) {
                case '0': return ')';
                case '1': return '!';
                case '2': return '@';
                case '3': return '#';
                case '4': return '$';
                case '5': return '%';
                case '6': return '^';
                case '7': return '&';
                case '8': return '*';
                case '9': return '(';
                default: return c;
            }
        }
    }
//#endif
    
//#ifndef nocursororscroll
    private int gameKeysToNumeric( int keycode ) {
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
        return keycode;
    }

    protected void keyPressedCursor( int keycode ) {
        keycode = gameKeysToNumeric( keycode );
        
        switch ( keycode ) {
            case Canvas.KEY_NUM2:
                buffer.keyPressed( VT320.VK_UP, VT320.KEY_ACTION );
                break;
            case Canvas.KEY_NUM8:
            case Canvas.KEY_NUM0:
                buffer.keyPressed( VT320.VK_DOWN, VT320.KEY_ACTION );
                break;
            case Canvas.KEY_NUM4:
                buffer.keyPressed( VT320.VK_LEFT, VT320.KEY_ACTION );
                break;
            case Canvas.KEY_NUM6:
                buffer.keyPressed( VT320.VK_RIGHT, VT320.KEY_ACTION );
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

    protected void keyPressedScroll( int keycode ) {
        keycode = gameKeysToNumeric( keycode );
        
        switch ( keycode ) {
            case Canvas.KEY_NUM2:
                if ( top > 0 ) {
                    top--;
                }
                redraw();
                break;
            case Canvas.KEY_NUM8:
            case Canvas.KEY_NUM0:
                if ( top + rows < buffer.height ) {
                    top++;
                }
                redraw();
                break;
            case Canvas.KEY_NUM4:
                if ( left > 0 ) {
                    left--;
                }
                redraw();
                break;
            case Canvas.KEY_NUM6:
                if ( left + cols < buffer.width ) {
                    left++;
                }
                redraw();
                break;
            case Canvas.KEY_NUM1:
                keyPressedScroll( Canvas.UP );
            keyPressedScroll( Canvas.LEFT );
                break;
            case Canvas.KEY_NUM3:
                keyPressedScroll( Canvas.UP );
            keyPressedScroll( Canvas.RIGHT );
                break;
            case Canvas.KEY_NUM7:
            case Canvas.KEY_STAR:
                keyPressedScroll( Canvas.DOWN );
                keyPressedScroll( Canvas.LEFT );
                break;
            case Canvas.KEY_NUM9:
            case Canvas.KEY_POUND:
                keyPressedScroll( Canvas.DOWN );
                keyPressedScroll( Canvas.RIGHT );
                break;
        }
    }
//#endif
    
    private void doDisconnect() {
        session.disconnect();
        session.goMainMenu();
    }
    
    private void doTextInput() {
        if ( inputDialog == null ) {
            inputDialog = new InputDialog();
        }
        inputDialog.activate( this );
    }

    private void doControlKeyInput() {
        if ( controlKeyDialog == null ) {
            controlKeyDialog = new ModifierInputDialog( "Control Keys", VT320.KEY_CONTROL );
        }
        controlKeyDialog.activate( this );
    }

    private void doAltKeyInput() {
        if ( altKeyDialog == null ) {
            altKeyDialog = new ModifierInputDialog( "Alt Keys", VT320.KEY_ALT );
        }
        altKeyDialog.activate( this );
    }

    private void doShiftKeyInput() {
        if ( shiftKeyDialog == null ) {
            shiftKeyDialog = new ModifierInputDialog( "Shift Keys", VT320.KEY_SHIFT );
        }
        shiftKeyDialog.activate( this );
    }

//#ifndef nocursororscroll
    public void doCursor() {
        changeMode( MODE_CURSOR );
    }

    public void doScroll() {
        changeMode( MODE_SCROLL );
    }
//#endif
    
//#ifndef notyping
    public void doTyping() {
        changeMode( MODE_TYPING );
    }
//#endif
    
    private void doShowBindings() {
        StringBuffer str = new StringBuffer();
        
        if ( currentCommands != null ) {
            for ( int i = 0; i < bindingKeys.length && i < currentCommands.length; i++ ) {
                int keycode = bindingKeys[i];
                Command comm = currentCommands[i];
                String keyName = getKeyName( keycode );
                str.append( keyName );
                str.append( ": " );
                str.append( comm.getLabel() );
                str.append( "\n" );
            }
        }
        
        new MessageForm( "Key Bindings", str.toString() ).activate( this );
    }
    
    
    
    
    
    
    
	/** the VDU buffer */
	protected VT320 buffer;

	/** first top and left character in buffer, that is displayed */
	protected int top, left;
	
	protected int width, height;
	
	private int fontWidth, fontHeight;
	
	protected int rotated;

	/** display size in characters */
	public int rows, cols;

	private Image backingStore = null;

	public int fgcolor = 0x000000;

	public int bgcolor = 0xffffff;

	/** A list of colors used for representation of the display */
    
    private static final int color[] = {
            // black, red, green, yellow
            0x000000, 0xcc0000, 0x00cc00, 0xcccc00,
            // blue, magenta, cyan, white
            0x0000cc, 0xcc00cc, 0x00cccc, 0xcccccc 
    };
    
    private static final int boldcolor[] = {
            // black, red, green, yellow
            0x333333, 0xff0000, 0x00ff00, 0xffff00,
            // blue, magenta, cyan, white
            0x0000ff, 0xff00ff, 0x00ffff, 0xffffff
    };
    
    private static final int lowcolor[] = {
            // black, red, green, yellow
            0x000000, 0x990000, 0x009900, 0x999900,
            // blue, magenta, cyan, white
            0x000099, 0x990099, 0x009999, 0x999999 
    };

    //#ifndef nopaintsync
	private Object paintMutex = new Object();
	//#endif
	
	protected void paint( Graphics g ) {
		
		// Erase display
		g.setColor( bgcolor );
		g.fillRect( 0, 0, getWidth(), getHeight() );

		// Draw terminal image
//#ifndef nopaintsync
		synchronized ( paintMutex ) {
//#endif
			// Redraw backing store if necessary
			redrawBackingStore();
			
			switch ( rotated ) {
//#ifdef midp2
            case Settings.ROT_270:
                g.drawRegion( backingStore, 0, 0, width - 1, height, Sprite.TRANS_ROT270, 0, 1, Graphics.TOP | Graphics.LEFT );
                break;
            case Settings.ROT_90:
                g.drawRegion( backingStore, 0, 0, width - 1, height, Sprite.TRANS_ROT90, 0, 1, Graphics.TOP | Graphics.LEFT );
                break;
//#endif
            default:
                // KARL the y coord 1 is because with 0 it sometimes fails to draw
                // on my SonyEricsson K700i
				g.drawImage( backingStore, 0, 1, Graphics.TOP | Graphics.LEFT );
                break;
            }
//#ifndef nopaintsync
		}
//#endif
	}

	private boolean invalid = true;

	public void redraw() {
//#ifndef nopaintsync
	    synchronized ( paintMutex ) {
//#endif
	        invalid = true;
	        repaint();
//#ifndef nopaintsync
	    }
//#endif
	}

	protected void redrawBackingStore() {
		// Only redraw if we've been marked as invalid by a call to redraw
		// The idea is that if multiple calls to redraw occur before the call to
		// paint then we save
		// time not redrawing our backingStore each time
		if ( invalid ) {
			//long st = System.currentTimeMillis();
			
			Graphics g = backingStore.getGraphics();
			g.setColor( bgcolor );
			g.fillRect( 0, 0, width, height );

			for ( int l = top; l < buffer.height && l < ( top + rows ); l++ ) {
				if ( !buffer.update[0] && !buffer.update[l + 1] ) {
					continue;
				}
				buffer.update[l + 1] = false;
				for ( int c = left; c < buffer.width && c < ( left + cols ); c++ ) {
					int addr = 0;
					int currAttr = buffer.charAttributes[buffer.windowBase + l][c];

					int fg = fgcolor;
					int bg = bgcolor;
                    
					if (Main.useColors) {
	                    int fgcolorindex = ( ( currAttr & VT320.COLOR_FG ) >> 4 ) - 1;
						if ( fgcolorindex >= 0 && fgcolorindex < 8 ) {
	                        /* Colour index 8 is invalid, 9 means use default */
	                        if ( (currAttr & VT320.BOLD) != 0) {
	                            fg = boldcolor[fgcolorindex];
	                        }
	                        else if (( currAttr & VT320.LOW ) != 0) {
	                            fg = lowcolor[fgcolorindex];
	                        }
	                        else {
	                            fg = color[fgcolorindex];
	                        }
						}
	                    int bgcolorindex = ( ( currAttr & VT320.COLOR_BG ) >> 8 ) - 1;
						if ( bgcolorindex >= 0 && bgcolorindex < 8) {
	                        /* Colour index 8 is invalid, 9 means use default */
	                        bg = color[bgcolorindex];
						}
						if ( ( currAttr & VT320.INVERT ) != 0 ) {
							int swapc = bg;
							bg = fg;
							fg = swapc;
						}
					}

					// determine the maximum of characters we can print in one
					// go
					while ( ( c + addr < buffer.width )
							&& ( ( buffer.charArray[buffer.windowBase + l][c + addr] < ' ' ) || ( buffer.charAttributes[buffer.windowBase
									+ l][c + addr] == currAttr ) ) ) {
						if ( buffer.charArray[buffer.windowBase + l][c + addr] < ' ' ) {
							buffer.charArray[buffer.windowBase + l][c + addr] = ' ';
							buffer.charAttributes[buffer.windowBase + l][c + addr] = 0;
							continue;
						}
						addr++;
					}

					// clear the part of the screen we want to change (fill
					// rectangle)
					g.setColor( bg );

					g.fillRect( ( c - left ) * fontWidth, ( l - top ) * fontHeight, addr * fontWidth, fontHeight );

					g.setColor( fg );

					// draw the characters
					drawChars( g, fg, bg, buffer.charArray[buffer.windowBase + l], c, addr, ( c - left ) * fontWidth,
							( l - top ) * fontHeight );

					c += addr - 1;
				}
			}

			// draw cursor
			if ( buffer.showcursor
					&& ( buffer.screenBase + buffer.cursorY >= buffer.windowBase && buffer.screenBase + buffer.cursorY < buffer.windowBase
							+ buffer.height ) ) {
				g.setColor( fgcolor );
				g.fillRect( ( buffer.cursorX - left ) * fontWidth,
						( buffer.cursorY - top + buffer.screenBase - buffer.windowBase ) * fontHeight, fontWidth,
						fontHeight );
			}

			invalid = false;
			//System.out.println("REDRAW " + (System.currentTimeMillis() - st));
		}
	}
	
//#ifndef small
	private LCDFont lcdfont;
	
	private int prevfg = -1, prevbg = -1;
//#endif
	
	private void initFont() {
//#ifdef small
        initInternalFont();
//#else
	    switch ( fontMode ) {
        case Settings.FONT_NORMAL:
	        initInternalFont();
            break;
        case Settings.FONT_SMALL:
	        initSystemFont( Font.SIZE_SMALL );
            break;
        case Settings.FONT_MEDIUM:
            initSystemFont( Font.SIZE_MEDIUM );
            break;
        case Settings.FONT_LARGE:
            initSystemFont( Font.SIZE_LARGE );
            break;
            //#ifdef midp2
        case Settings.FONT_TEST:
        	lcdfont = new LCDFont("/font4x7lcd.png", false);
        	fontWidth = lcdfont.fontWidth;
        	fontHeight = lcdfont.fontHeight;
        	break;
        	//#endif
        }
//#endif
	}

	private void initInternalFont() {
	    fontWidth = 4;
	    fontHeight = 6;
	    fontData = new int[128][];
	    
	    try {
			InputStream in = getClass().getResourceAsStream( FONT_RESOURCE );
			for ( int i = 33; i < 128; i++ ) {
				int b = in.read();
				int l = ( b & 3 ) + 2; // length could be 1,2,3 or 4; this is
				// len+1
				fontData[i] = new int[l]; // one more for template
				fontData[i][0] = ( b >> 2 ) - 32; // draw this template
				//        System.out.println("--- ascii "+i +"---" );
				//        System.out.println("header "+b );
				//        System.out.println("len "+(l-1) );
				//        System.out.println("template "+ data[i][0] );
				for ( int j = 1; j < l; j++ ) {
				    fontData[i][j] = in.read();
					//          System.out.println("data["+j+"]" + data[i][j] );
				}
			}
			in.close();
		}
		catch ( Exception e ) {
			//e.printStackTrace();
		}
	}
	
	private void initSystemFont( int size ) {
	    font = Font.getFont( Font.FACE_MONOSPACE, Font.STYLE_PLAIN, size );
		fontHeight = font.getHeight();
		fontWidth = font.charWidth( 'W' );
	}
	
	protected void drawChars( Graphics g, int fg, int bg, char[] chars, int offset, int length, int x, int y ) {
//#ifndef small
	    if ( fontMode == Settings.FONT_NORMAL ) {
//#endif
	        for ( int i = offset; i < offset + length; i++ ) {
				drawChar( g, chars[i], x, y );
				x += fontWidth;
			}
//#ifndef small
	    }
	    //#ifdef midp2
	    else if (fontMode == Settings.FONT_TEST) {
	    	/* Change colour */
	    	if (fg != prevfg || bg != prevbg) {
	    		lcdfont.setColor(fg, bg);
	    		prevfg = fg;
	    		prevbg = bg;
	    	}
	    	/* Draw chars */
	    	for ( int i = offset; i < offset + length; i++ ) {
				lcdfont.drawChar( g, chars[i], x, y );
				x += fontWidth;
			}
	    }
	    //#endif
	    else {
	        g.setFont( font );
			for ( int i = offset; i < offset + length; i++ ) {
				g.drawChar( chars[i], x, y, Graphics.TOP|Graphics.LEFT);
				x += fontWidth;
			}
	    }
//#endif
	}

	private void drawChar( Graphics g, char c, int x, int y ) {
		if ( c >= fontData.length || fontData[c] == null )
			return;
		for ( int j = 1; j < fontData[c].length; j++ ) {
			int x1 = fontData[c][j] & 3;
			int y1 = ( fontData[c][j] & 12 ) >> 2;
			int x2 = ( fontData[c][j] & 48 ) >> 4;
			int y2 = ( fontData[c][j] & 192 ) >> 6;

			if ( x1 == 3 ) {
				x1 = y1;
				y1 = 4;
			}

			if ( x2 == 3 ) {
				x2 = y2;
				y2 = 4;
			}

			//      System.out.println( "char " + c + " x1=" + x1 + " y1="+ (4-y1) +"
			// x2=" + x2 + " y2="+ (4-y2));

			g.drawLine( x + x1, y + y1, x + x2, y + y2 );
		}
		if ( fontData[c][0] != 0 )
			drawChar( g, (char) ( c + fontData[c][0] ), x, y ); // draw template
	}
	
	private int fontMode = Settings.fontMode;
	
	private Font font;
	
	private int[][] fontData;
	
	private static final String FONT_RESOURCE = "/font";
}