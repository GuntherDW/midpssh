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

import java.util.Vector;

import app.Main;
import app.session.Session;

/**
 * @author Karl von Randow
 *
 */
public class MacrosMenu extends EditableMenu {
	private static MacroForm newMacroForm = new MacroForm( false );
	
	private static MacroForm editMacroForm = new MacroForm( true );
	
	private MacroSet macroSet;
	
	private int macroSetIndex;
	
	public MacrosMenu( MacroSet macroSet, int macroSetIndex ) {
		super( "Macros: " + macroSet.getName() );
		this.macroSet = macroSet;
		this.macroSetIndex = macroSetIndex;
	}
	
	/* (non-Javadoc)
	 * @see gui.EditableMenu#addItems()
	 */
	protected void addItems() {
		deleteAll();

		Vector macros = macroSet.getMacros();
		if ( macros != null ) {
			for ( int i = 0; i < macros.size(); i++ ) {
				Macro macro = (Macro) macros.elementAt( i );
				String name = macro.getName();
				if ( name == null || name.length() == 0 ) {
					name = macro.getValue().trim(); // trim off whitespace as it may end with a newline
				}
				append( name, null );
			}
		}
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doDelete(int)
	 */
	protected void doDelete( int i ) {
		macroSet.deleteMacro( i );
		delete( i );
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doSelect(int)
	 */
	protected void doSelect( int i ) {
		Session session = Main.currentSession();
		if ( session != null ) {
			Macro macro = macroSet.getMacro( i );
			if ( macro != null ) {
				session.typeString( macro.getValue() );
				session.activate();
			}
		}
		else {
			doEdit( i );
		}
	}
	protected void doEdit( int i ) {
		editMacroForm.setMacroIndices( macroSetIndex, i );
		editMacroForm.activate( this );
	}

	protected void doNew() {
	    newMacroForm.setMacroSetIndex( macroSetIndex );
		newMacroForm.activate( this );
	}
}
