/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session.macros;

import gui.Activatable;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import app.session.MacroSetManager;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class NewMacroSetForm extends MacroSetForm {

	private static Command createCommand = new Command( "Create", Command.SCREEN, 1 );

	/**
	 * @param title
	 */
	public NewMacroSetForm() {
		super( "New Macro Set" );
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
			MacroSet macroSet = new MacroSet();
			macroSet.setName( tfName.getString() );
			MacroSetManager.addMacroSet( macroSet );

			doBack();
		}
	}
}