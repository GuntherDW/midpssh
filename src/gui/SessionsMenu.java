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

import java.util.Vector;

import javax.microedition.lcdui.Command;

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
	
	private static NewSessionForm newConnectionForm = new NewSessionForm();

	private static EditSessionForm editConnectionForm = new EditSessionForm();

	public SessionsMenu() {
		super( "Sessions" );
		replaceSelectCommand( connectCommand );
	}

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
		SessionSpec conn = SessionManager.getSession( i );
		if ( conn != null ) {
			if ( conn.type.equals( SessionSpec.TYPE_SSH ) ) {
				SshSession session = new SshSession();
				session.connect( conn.host, conn.username, conn.password );
				Main.openSession( session );
			}
			else if ( conn.type.equals( SessionSpec.TYPE_TELNET ) ) {
				TelnetSession session = new TelnetSession();
				session.connect( conn.host );
				Main.openSession( session );
			}
		}
	}

	protected void doEdit( int i ) {
		editConnectionForm.setConnectionIndex( i );
		editConnectionForm.activate( this );
	}

	protected void doDelete( int i ) {
		SessionManager.deleteSession( i );
		delete( i );
	}

	protected void doNew() {
		newConnectionForm.activate( this );
	}
}