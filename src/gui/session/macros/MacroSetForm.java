/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session.macros;

import gui.EditableForm;

import javax.microedition.lcdui.TextField;


/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class MacroSetForm extends EditableForm {
	protected TextField tfName;

	/**
	 * @param arg0
	 */
	public MacroSetForm( String title ) {
		super( title );

		tfName = new TextField( "Macro Set Name:", null, 255, TextField.ANY );

		append( tfName );
	}

	protected boolean validateForm() {
		String errorMessage = null;
		
		if ( tfName.getString() == null || tfName.getString().length() == 0 ) {
			errorMessage = "Please fill in the Macro Set Name";
		}

		if ( errorMessage != null ) {
			showErrorMessage( errorMessage );
			return false;
		}
		else {
			return true;
		}
	}
}