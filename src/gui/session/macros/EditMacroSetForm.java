/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session.macros;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;

import app.session.MacroSetManager;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class EditMacroSetForm extends MacroSetForm {

	private static Command saveCommand = new Command( "Save", Command.SCREEN, 1 );

	private int macroSetIndex = 1;

	/**
	 * @param back
	 * @param title
	 */
	public EditMacroSetForm() {
		super( "Edit Macro Set" );

		addCommand( saveCommand );
	}

	public void setMacroSetIndex( int macroSetIndex ) {
		this.macroSetIndex = macroSetIndex;

		MacroSet macroSet = MacroSetManager.getMacroSet( macroSetIndex );
		if ( macroSet != null ) {
			tfName.setString( macroSet.getName() );
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
		else {
			super.commandAction( command, displayed );
		}
	}

	private void doSave() {
		if ( macroSetIndex != -1 ) {
			if ( validateForm() ) {
				MacroSet macroSet = MacroSetManager.getMacroSet( macroSetIndex );
				macroSet.setName( tfName.getString() );
				MacroSetManager.replaceMacroSet( macroSetIndex, macroSet );

				doBack();
			}
		}
	}

}