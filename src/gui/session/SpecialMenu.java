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
package gui.session;

import gui.Activatable;
import gui.MainMenu;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import terminal.VT320;
import app.session.Session;

/**
 * @author Karl
 *
 */
public class SpecialMenu extends List implements CommandListener, Activatable {

    private static final String MAIN_OPTIONS = "Bksp|Home|End|PgU|PgD|Del|Ins|Func|||\\|~|:|;|'|\"|,|<|.|>|/|?|`|!|@|#|$|%|^|&|*|(|)|-|_|+|=|[|{|]|}|";
    
    private static final String FUNCTION_KEY_OPTIONS = "F1|F2|F3|F4|F5|F6|F7|F8|F9|F10|F11|F12|";
    
    private SpecialMenu menuFunctionKeys;
    
    private Activatable back, done;
    
    /**
     * @param title
     * @param mode
     */
    public SpecialMenu() {
        this( "Special", MAIN_OPTIONS );
    }
    
    public SpecialMenu( String title, String options ) {
        super(title, List.IMPLICIT);
        
        int start = 0;
        int i = options.indexOf('|');
        while (i != -1) {
        	append(options.substring(start, i), null);
        	start = i + 1;
        	i = options.indexOf('|', start + 1); // +1 to that we see tokens that are single |
        }

		//setSelectCommand( selectCommand );
		addCommand( MainMenu.backCommand );
		
		setCommandListener( this );
    }
    
    public void commandAction( Command command, Displayable displayed ) {
		if ( command == List.SELECT_COMMAND ) {
	        Session session = MainMenu.currentSession();
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
		else if ( command == MainMenu.backCommand ) {
		    if ( back != null ) {
		        back.activate();
		    }
		}
	}
    
    private int find( String options, String option ) {
    	int start = 0;
    	int i = options.indexOf('|');
    	int count = 0;
    	while (i != -1) {
    		if (options.substring(start, i).equals(option)) {
    			return count;
    		}
    		count++;
    		start = i + 1;
    		i = options.indexOf('|', start + 1);
    	}
        return -1;
    }
    public void activate() {
        MainMenu.setDisplay( this );
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
