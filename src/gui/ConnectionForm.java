/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;

import app.ConnectionSpec;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class ConnectionForm extends EditableForm {
	protected TextField tfAlias, tfHost, tfUsername, tfPassword;

	protected ChoiceGroup cgType;

	private static String[] typeNames = new String[] {
			"SSH", "Telnet"
	};

	private static String[] typeCodes = new String[] {
			ConnectionSpec.TYPE_SSH, ConnectionSpec.TYPE_TELNET
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
			if ( type.equals( ConnectionSpec.TYPE_SSH ) ) {
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