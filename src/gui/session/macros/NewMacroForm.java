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
public class NewMacroForm extends MacroForm {

	private static Command createCommand = new Command( "Create", Command.SCREEN, 1 );
	
	private int macroSetIndex;

	/**
	 * @param title
	 */
	public NewMacroForm() {
		super( "New Macro" );
		addCommand( createCommand );
	}

	/**
	 * @param macroSetIndex The macroSetIndex to set.
	 */
	public void setMacroSetIndex( int macroSetIndex ) {
		this.macroSetIndex = macroSetIndex;
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
			MacroSet macroSet = MacroSetManager.getMacroSet( macroSetIndex );
			String value = tfValue.getString();
			if ( cgType.getSelectedIndex() == 0 ) {
				value += "\n";
			}
			Macro macro = new Macro( tfName.getString(), value );
			macroSet.addMacro( macro );
			
			doBack();
		}
	}
}