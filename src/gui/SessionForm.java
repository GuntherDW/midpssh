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
package gui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import app.SessionManager;
import app.SessionSpec;

/**
 * @author Karl von Randow
 * 
 */
public class SessionForm extends EditableForm {

	private static Command saveCommand = new Command( "Save", Command.SCREEN, 1 );

	private static Command createCommand = new Command( "Create", Command.SCREEN, 1 );

	private int connectionIndex = 1;

	private boolean edit;
	
	private TextField tfAlias, tfHost, tfUsername, tfPassword;

	private ChoiceGroup cgType;

	private static String[] typeNames = new String[] {
			"SSH"
//#ifndef notelnet
	        , "Telnet"
//#endif
	};

	private static String[] typeCodes = new String[] {
			SessionSpec.TYPE_SSH
//#ifndef notelnet			
			, SessionSpec.TYPE_TELNET
//#endif
	};

	/**
	 * @param arg0
	 */
	public SessionForm( boolean edit ) {
	    super( edit ? "Edit Session" : "New Session" );

		this.edit = edit;

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
//#ifndef notelnet
		append( new StringItem( "Authentication:\n", "For SSH connections only." ) );
//#endif
		append( tfUsername );
		append( tfPassword );
		
		if ( edit ) {
		    addCommand( saveCommand );
		}
		else {
			addCommand( createCommand );
		}
	}

	/* (non-Javadoc)
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
	    if ( !edit ) {
			tfAlias.setString( "" );
			tfHost.setString( "" );
			tfUsername.setString( "" );
			tfPassword.setString( "" );
	    }
		super.activate();
	}

	public void setConnectionIndex( int connectionIndex ) {
		this.connectionIndex = connectionIndex;

		SessionSpec conn = SessionManager.getSession( connectionIndex );
		if ( conn != null ) {
			tfAlias.setString( conn.alias );
			tfHost.setString( conn.host );
			if ( conn.type != null ) {
				for ( int i = 0; i < typeCodes.length; i++ ) {
					if ( typeCodes[i].equals( conn.type ) ) {
						cgType.setSelectedIndex( i, true );
					}
				}
			}
			tfUsername.setString( conn.username );
			tfPassword.setString( conn.password );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable displayed ) {
		if ( command == saveCommand ) {
			doSave();
		}
		else if ( command == createCommand ) {
			doCreate();
		}
		else {
			super.commandAction( command, displayed );
		}
	}

	private void doSave() {
		if ( connectionIndex != -1 ) {
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
				SessionManager.replaceSession( connectionIndex, conn );

				doBack();
			}
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