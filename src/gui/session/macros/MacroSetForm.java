/* This file is part of "MidpSSH".
 * Copyright (c) 2004 XK72 Ltd.
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

import gui.EditableForm;

import javax.microedition.lcdui.TextField;


/**
 * @author Karl von Randow
 * 
 */
public abstract class MacroSetForm extends EditableForm {
	protected TextField tfName;

	/**
	 * @param arg0
	 */
	public MacroSetForm( String title ) {
		super( title );

		tfName = new TextField( "Macro Set Name:", null, 255, TextField.ANY );

		append( tfName );
	}

	protected boolean validateForm() {
		String errorMessage = null;
		
		if ( tfName.getString() == null || tfName.getString().length() == 0 ) {
			errorMessage = "Please fill in the Macro Set Name";
		}

		if ( errorMessage != null ) {
			showErrorMessage( errorMessage );
			return false;
		}
		else {
			return true;
		}
	}
}