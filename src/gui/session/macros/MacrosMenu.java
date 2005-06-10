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
package gui.session.macros;

import gui.EditableMenu;
import gui.session.InputDialog;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import app.Main;
import app.session.MacroSetManager;
import app.session.Session;

/**
 * @author Karl von Randow
 *
 */
public class MacrosMenu extends EditableMenu {
    
    protected static Command useCommand = new Command( "Use", Command.ITEM, 1 );
	
	private MacroSet macroSet;
	
	private int macroSetIndex;
    
    private boolean isMacroSets;
	
    public MacrosMenu() {
        super( "Macro Sets" );
        isMacroSets = true;
    }
    
	public MacrosMenu( MacroSet macroSet, int macroSetIndex ) {
		super( "Macros: " + macroSet.name );
        isMacroSets = false;
		this.macroSet = macroSet;
		this.macroSetIndex = macroSetIndex;
        
        if ( Main.currentSession() != null ) {
            addCommand( useCommand );
        }
	}
	
    /* (non-Javadoc)
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
     */
    public void commandAction(Command command, Displayable displayable) {
        if ( command == useCommand ) {
            int i = getSelectedIndex();
            if ( i >= 0 && i < size() ) {
                Session session = Main.currentSession();
                if ( session != null ) {
                    Macro macro = macroSet.getMacro( i );
                    if ( macro != null ) {
                        InputDialog input = new InputDialog();
                        input.activate( session );
                        input.setString( macro.value.trim() );
                    }
                }
                else {
                    doEdit( i );
                }
            }
        }
        else {
            super.commandAction(command, displayable);
        }
    }
	/* (non-Javadoc)
	 * @see gui.EditableMenu#addItems()
	 */
	protected void addItems() {
		deleteAll();

        if ( isMacroSets ) {
            Vector macroSets = MacroSetManager.getMacroSets();
            if ( macroSets != null ) {
                for ( int i = 0; i < macroSets.size(); i++ ) {
                    MacroSet macroSet = (MacroSet) macroSets.elementAt( i );
                    append( macroSet.name, null );
                }
            }
        }
        else {
    		Vector macros = macroSet.macros;
    		if ( macros != null ) {
    			for ( int i = 0; i < macros.size(); i++ ) {
    				Macro macro = (Macro) macros.elementAt( i );
    				String name = macro.name;
    				if ( name == null || name.length() == 0 ) {
    					name = macro.value.trim(); // trim off whitespace as it may end with a newline
    				}
    				append( name, null );
    			}
    		}
        }
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doDelete(int)
	 */
	protected void doDelete( int i ) {
		if ( i != -1 ) {
            if ( isMacroSets ) {
                MacroSetManager.deleteMacroSet( i );
            }
            else {
                macroSet.deleteMacro( i );
            }
			delete( i );
		}
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doSelect(int)
	 */
	protected void doSelect( int i ) {
		if ( i != -1 ) {
            if ( isMacroSets ) {
                MacroSet macroSet = MacroSetManager.getMacroSet( i );
                MacrosMenu macrosMenu = new MacrosMenu( macroSet, i );
                macrosMenu.activate( this );
            }
            else {
    			Session session = Main.currentSession();
    			if ( session != null ) {
    				Macro macro = macroSet.getMacro( i );
    				if ( macro != null ) {
    					session.typeString( macro.value );
    					session.activate();
    				}
    			}
    			else {
    				doEdit( i );
    			}
            }
		}
	}
	protected void doEdit( int i ) {
		if ( i != -1 ) {
            MacroForm editMacroForm = new MacroForm( true, isMacroSets );
            if ( isMacroSets ) {
                editMacroForm.setMacroSetIndex( i );
            }
            else {
    			editMacroForm.setMacroIndices( macroSetIndex, i );
            }
            editMacroForm.activate( this );
		}
	}

	protected void doNew() {
        MacroForm newMacroForm = new MacroForm( false, isMacroSets );
        if ( !isMacroSets ) {
            newMacroForm.setMacroSetIndex( macroSetIndex );
        }
		newMacroForm.activate( this );
	}
}
