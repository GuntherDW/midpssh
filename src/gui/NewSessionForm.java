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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import app.SessionManager;
import app.SessionSpec;

/**
 * @author Karl von Randow
 * 
 */
public class NewSessionForm extends ConnectionForm {

	private static Command createCommand = new Command( "Create", Command.SCREEN, 1 );

	/**
	 * @param title
	 */
	public NewSessionForm() {
		super( "New Session" );
		addCommand( createCommand );
	}

	/* (non-Javadoc)
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		tfAlias.setString( "" );
		tfHost.setString( "" );
		tfUsername.setString( "" );
		tfPassword.setString( "" );
		super.activate();
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
			String alias = tfAlias.getString();
			String type = selectedConnectionType();
			String host = tfHost.getString();
			String username = tfUsername.getString();
			String password = tfPassword.getString();

			SessionSpec conn = new SessionSpec();
			conn.alias = alias;
			conn.type = type;
			conn.host = host;
			conn.username = username;
			conn.password = password;
			SessionManager.addSession( conn );

			doBack();
		}
	}
}