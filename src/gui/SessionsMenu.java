/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import java.util.Vector;

import app.ConnectionManager;
import app.ConnectionSpec;
import app.Main;
import app.session.SshSession;
import app.session.TelnetSession;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SessionsMenu extends EditableMenu {

	private static NewConnectionForm newConnectionForm = new NewConnectionForm();

	private static EditConnectionForm editConnectionForm = new EditConnectionForm();

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SessionsMenu() {
		super( "Sessions" );
	}

	protected void addItems() {
		deleteAll();

		Vector connections = ConnectionManager.getConnections();
		if ( connections != null ) {
			for ( int i = 0; i < connections.size(); i++ ) {
				ConnectionSpec conn = (ConnectionSpec) connections.elementAt( i );
				append( conn.alias, null );
			}
		}
	}

	protected void doSelect( int i ) {
		ConnectionSpec conn = ConnectionManager.getConnection( i );
		if ( conn != null ) {
			if ( conn.type.equals( ConnectionSpec.TYPE_SSH ) ) {
				SshSession session = new SshSession();
				session.connect( conn.host, conn.username, conn.password );
				Main.openSession( session );
			}
			else if ( conn.type.equals( ConnectionSpec.TYPE_TELNET ) ) {
				TelnetSession session = new TelnetSession();
				session.connect( conn.host );
				Main.openSession( session );
			}
		}
	}

	protected void doEdit( int i ) {
		editConnectionForm.setConnectionIndex( i );
		Main.setDisplay( editConnectionForm );
	}

	protected void doDelete( int i ) {
		ConnectionManager.deleteConnection( i );
	}

	protected void doNew() {
		Main.setDisplay( newConnectionForm );
	}
}