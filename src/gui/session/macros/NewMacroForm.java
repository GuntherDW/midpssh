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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import app.session.MacroSetManager;

/**
 * @author Karl von Randow
 * 
 */
public class NewMacroForm extends MacroForm {

	private static Command createCommand = new Command( "Create", Command.SCREEN, 1 );
	
	private int macroSetIndex;

	/**
	 * @param title
	 */
	public NewMacroForm() {
		super( "New Macro" );
		addCommand( createCommand );
	}

	/* (non-Javadoc)
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		tfName.setString( "" );
		tfValue.setString( "" );
		super.activate();
	}
	
	/**
	 * @param macroSetIndex The macroSetIndex to set.
	 */
	public void setMacroSetIndex( int macroSetIndex ) {
		this.macroSetIndex = macroSetIndex;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable displayed ) {
		if ( command == createCommand ) {
			doCreate();
		}
		else {
			super.commandAction( command, displayed );
		}
	}

	private void doCreate() {
		if ( validateForm() ) {
			MacroSet macroSet = MacroSetManager.getMacroSet( macroSetIndex );
			String value = tfValue.getString();
			if ( cgType.getSelectedIndex() == 0 ) {
				value += "\n";
			}
			Macro macro = new Macro( tfName.getString(), value );
			macroSet.addMacro( macro );
			
			doBack();
		}
	}
}