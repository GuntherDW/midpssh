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
public abstract class MacroForm extends EditableForm {
	protected TextField tfName, tfValue;

	/**
	 * @param arg0
	 */
	public MacroForm( String title ) {
		super( title );

		tfValue = new TextField( "Value:", null, 255, TextField.ANY );
		tfName = new TextField( "Name (Optional):", null, 255, TextField.ANY );
		
		append( tfName );
		append( tfValue );
	}

	protected boolean validateForm() {
		String errorMessage = null;
		
		if ( tfValue.getString() == null || tfValue.getString().length() == 0 ) {
			errorMessage = "Please fill in the value";
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