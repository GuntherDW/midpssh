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
package gui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import app.SessionSpec;

/**
 * @author Karl von Randow
 * 
 */
public abstract class ConnectionForm extends EditableForm {
	protected TextField tfAlias, tfHost, tfUsername, tfPassword;

	protected ChoiceGroup cgType;

	private static String[] typeNames = new String[] {
			"SSH", "Telnet"
	};

	private static String[] typeCodes = new String[] {
			SessionSpec.TYPE_SSH, SessionSpec.TYPE_TELNET
	};

	/**
	 * @param arg0
	 */
	public ConnectionForm( String title ) {
		super( title );

		tfAlias = new TextField( "Alias:", null, 255, TextField.ANY );
		tfHost = new TextField( "Host:", null, 255, TextField.ANY );
		tfUsername = new TextField( "Username:", null, 255, TextField.ANY );
		tfPassword = new TextField( "Password:", null, 255, TextField.PASSWORD );
		cgType = new ChoiceGroup( "Type", ChoiceGroup.EXCLUSIVE );
		for ( int i = 0; i < typeNames.length; i++ ) {
			cgType.append( typeNames[i], null );
		}

		append( tfAlias );
		append( tfHost );
		append( cgType );
		append( new StringItem( "Authentication:\n", "For SSH connections only." ) );
		append( tfUsername );
		append( tfPassword );
	}

	protected boolean validateForm() {
		String alias = tfAlias.getString();
		String host = tfHost.getString();
		String type = selectedConnectionType();
		String username = tfUsername.getString();
		String password = tfPassword.getString();
		String errorMessage;

		if ( type != null ) {
			if ( type.equals( SessionSpec.TYPE_SSH ) ) {
				if ( alias.length() > 0 && host.length() > 0 && username.length() > 0 ) {
					errorMessage = null;
				}
				else {
					errorMessage = "Please fill in the Alias, Host and Username fields.";
				}
			}
			else {
				if ( alias.length() > 0 && host.length() > 0 ) {
					errorMessage = null;
				}
				else {
					errorMessage = "Please fill in the Alias and Host fields.";
				}
			}
		}
		else {
			errorMessage = "Please choose the connection type.";
		}

		if ( errorMessage != null ) {
			showErrorMessage( errorMessage );
			return false;
		}
		else {
			return true;
		}
	}

	protected String selectedConnectionType() {
		int i = cgType.getSelectedIndex();
		if ( i < 0 || i >= typeCodes.length ) {
			return null;
		}
		else {
			return typeCodes[i];
		}
	}
}