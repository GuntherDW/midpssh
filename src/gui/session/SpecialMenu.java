/*
 * Created on Nov 25, 2004
 *
 */
package gui.session;

import gui.Activatable;
import gui.ExtendedList;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import terminal.VT320;
import app.Main;
import app.session.Session;

/**
 * @author Karl
 *
 */
public class SpecialMenu extends ExtendedList implements CommandListener, Activatable {

	private static Command backCommand = new Command( "Back", Command.BACK, 2 );

    private static final String[] MAIN_OPTIONS = new String[] {      
        "BACKSPACE", "Home", "End", "Page Up", "Page Down", "Delete", "Insert",
        "Function Keys", 
        "|", "\\", "~", ":", ";", "'", "\"",
        ",", "<", ".", ">", "/", "?",
        "`", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")",
        "-", "_", "+", "=",
        "[", "{", "]", "}"
    };
    
    private static final String[] FUNCTION_KEY_OPTIONS = new String[] {
        "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12"
    };
    
    private SpecialMenu menuFunctionKeys;
    
    private Activatable back, done;
    
    /**
     * @param title
     * @param mode
     */
    public SpecialMenu() {
        this( "Special Keys", MAIN_OPTIONS );
    }
    
    public SpecialMenu( String title, String [] options ) {
        super(title, List.IMPLICIT);
        
        for ( int i = 0; i < options.length; i++ ) {
            append( options[i], null );
        }

		//setSelectCommand( selectCommand );
		addCommand( backCommand );
		
		setCommandListener( this );
    }
    
    public void commandAction( Command command, Displayable displayed ) {
		if ( command == List.SELECT_COMMAND ) {
	        Session session = Main.currentSession();
			if ( session != null ) {
			    String option = getString( getSelectedIndex() );
			    int keyCode = 0;
			    String str = null;
			    
			    // Main options
			    int i = find( MAIN_OPTIONS, option );
			    if ( i != -1 ) {
			        switch ( i ) {
			            case 0:
			                keyCode = VT320.VK_BACK_SPACE;
			                break;
                        case 1:
                            keyCode = VT320.VK_HOME;
                            break;
                        case 2:
                            keyCode = VT320.VK_END;
                            break;
                        case 3:
                            keyCode = VT320.VK_PAGE_UP;
                            break;
                        case 4:
                            keyCode = VT320.VK_PAGE_DOWN;
                            break;
                        case 5:
                            keyCode = VT320.VK_DELETE;
                            break;
                        case 6:
                            keyCode = VT320.VK_INSERT;
                            break;
			            case 7:
			                if ( menuFunctionKeys == null ) {
					            menuFunctionKeys = new SpecialMenu( "Function Keys", FUNCTION_KEY_OPTIONS );
					        }
					        menuFunctionKeys.activate( this, done );
					        break;
			            default:
			                str = option;
			            	break;
			        }
			    }
			    
			    // Function keys
			    i = find( FUNCTION_KEY_OPTIONS, option );
			    if ( i != -1 ) {
			        keyCode = VT320.VK_F1 + i;
			    }
			    if ( keyCode != 0 ) {
			        session.typeKey( keyCode, 0 );
					done.activate();
			    }
			    else if ( str != null ) {
			        session.typeString( str );
					done.activate();
			    }
		    }
		}
		else if ( command == backCommand ) {
		    if ( back != null ) {
		        back.activate();
		    }
		}
	}
    
    private int find( String [] options, String option ) {
        for (int i = 0; i < options.length; i++) {
            if ( options[i].equals( option ) ) {
                return i;
            }
        }
        return -1;
    }
    public void activate() {
        Main.setDisplay( this );
    }
    public void activate(Activatable back) {
        activate( back, back );
    }
    public void activate(Activatable back, Activatable done) {
        this.back = back;
        this.done = done;
        activate();
    }
}
