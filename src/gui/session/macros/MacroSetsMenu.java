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

import app.session.MacroSetManager;

/**
 * @author Karl von Randow
 *
 */
public class MacroSetsMenu extends EditableMenu {
	
	private static MacroSetForm newMacroSetForm = new MacroSetForm( false );
	
	private static MacroSetForm editMacroSetForm = new MacroSetForm( true );
	
	public MacroSetsMenu() {
		super( "Macro Sets" );
	}
	
	/* (non-Javadoc)
	 * @see gui.EditableMenu#addItems()
	 */
	protected void addItems() {
		deleteAll();

		Vector macroSets = MacroSetManager.getMacroSets();
		if ( macroSets != null ) {
			for ( int i = 0; i < macroSets.size(); i++ ) {
				MacroSet macroSet = (MacroSet) macroSets.elementAt( i );
				append( macroSet.getName(), null );
			}
		}
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doDelete(int)
	 */
	protected void doDelete( int i ) {
		if ( i != -1 ) {
			MacroSetManager.deleteMacroSet( i );
			delete( i );
		}
	}
	/* (non-Javadoc)
	 * @see gui.EditableMenu#doSelect(int)
	 */
	protected void doSelect( int i ) {
		if ( i != -1 ) {
			MacroSet macroSet = MacroSetManager.getMacroSet( i );
			MacrosMenu macrosMenu = new MacrosMenu( macroSet, i );
			macrosMenu.activate( this );
		}
	}
	protected void doEdit( int i ) {
		if ( i != -1 ) {
			editMacroSetForm.setMacroSetIndex( i );
			editMacroSetForm.activate( this );
		}
	}

	protected void doNew() {
		newMacroSetForm.activate( this );
	}
}
