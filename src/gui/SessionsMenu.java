/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import java.util.Vector;

import app.SessionManager;
import app.SessionSpec;
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

	private static NewSessionForm newConnectionForm = new NewSessionForm();

	private static EditSessionForm editConnectionForm = new EditSessionForm();

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SessionsMenu() {
		super( "Sessions" );
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
		addItems();
	}

	protected void doNew() {
		newConnectionForm.activate( this );
	}
}