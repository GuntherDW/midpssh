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

import gui.session.PasswordDialog;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import app.SessionManager;
import app.SessionSpec;
import app.Main;
import app.session.SshSession;
import app.session.TelnetSession;

/**
 * @author Karl von Randow
 * 
 */
public class SessionsMenu extends EditableMenu {

	private static Command connectCommand = new Command( "Connect", Command.ITEM, 1 );
	
	private static SessionForm newConnectionForm = new SessionForm( false );

	private static SessionForm editConnectionForm = new SessionForm( true );
    
//#ifndef small
    private static Command importCommand = new Command("Import", Command.ITEM, 20);
    
    private ImportSessionsForm importSessionsForm;
//#endif
    
	public SessionsMenu() {
		super( "Sessions" );
		replaceSelectCommand( connectCommand );
        
//#ifndef small
        addCommand(importCommand);
//#endif
	}
    
//#ifndef small
    public void commandAction(Command command, Displayable displayable) {
        if (command == importCommand) {
            /* Import */
            if (importSessionsForm == null) {
                importSessionsForm = new ImportSessionsForm();
            }
            importSessionsForm.activate(this);
        }
        else {
            super.commandAction(command, displayable);
        }
    }
//#endif
    
	protected void addItems() {
		deleteAll();

		Vector connections = SessionManager.getSessions();
		if ( connections != null ) {
			for ( int i = 0; i < connections.size(); i++ ) {
				SessionSpec conn = (SessionSpec) connections.elementAt( i );
				append( conn.alias, null );
			}
		}
	}

	protected void doSelect( int i ) {
		if ( i != -1 ) {
			SessionSpec conn = SessionManager.getSession( i );
			if ( conn != null ) {
//#ifndef nossh
				if ( conn.type.equals( SessionSpec.TYPE_SSH ) ) {
					SshSession session = new SshSession();
                    String password = conn.password;
                    
                    if (password == null || password.length() == 0) {
                        /* Prompt for password */
                        new PasswordDialog(session, conn).activate(this);
                    }
                    else {
    					session.connect( conn, null );
    					Main.openSession( session );
                    }
				}
//#endif
//#ifndef notelnet
				if ( conn.type.equals( SessionSpec.TYPE_TELNET ) ) {
					TelnetSession session = new TelnetSession();
					session.connect( conn );
					Main.openSession( session );
				}
//#endif
			}
		}
	}

	protected void doEdit( int i ) {
		if ( i != -1 ) {
			editConnectionForm.setConnectionIndex( i );
			editConnectionForm.activate( this );
		}
	}

	protected void doDelete( int i ) {
		if ( i != -1 ) {
			SessionManager.deleteSession( i );
			delete( i );
		}
	}

	protected void doNew() {
		newConnectionForm.activate( this );
	}
}