/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import app.Activatable;
import app.ConnectionManager;
import app.ConnectionSpec;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NewConnectionForm extends ConnectionForm {

	private static Command createCommand = new Command( "Create", Command.SCREEN, 1 );

	/**
	 * @param title
	 */
	public NewConnectionForm( Activatable back ) {
		super( back, "New Connection" );
		addCommand( createCommand );
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

			ConnectionSpec conn = new ConnectionSpec();
			conn.alias = alias;
			conn.type = type;
			conn.host = host;
			conn.username = username;
			conn.password = password;
			ConnectionManager.addConnection( conn );

			doBack();
		}
	}
}