/*
 * Created on Oct 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import app.Main;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditableForm extends Form implements CommandListener, Activatable {

	private Activatable back;

	private static Command backCommand = new Command( "Back", Command.BACK, 2 );
	
	public EditableForm( String title ) {
		super( title );

		addCommand( backCommand );
		setCommandListener( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable displayed ) {
		if ( command == backCommand ) {
			doBack();
		}
	}

	protected void doBack() {
		back.activate();
	}
	
	protected void showErrorMessage( String errorMessage ) {
		Alert alert = new Alert( "Error" );
		alert.setString( errorMessage );
		alert.setType( AlertType.ERROR );
		Main.setDisplay( alert );
	}
	/* (non-Javadoc)
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		Main.setDisplay( this );
	}
	/* (non-Javadoc)
	 * @see gui.Activatable#activate(gui.Activatable)
	 */
	public void activate( Activatable back ) {
		this.back = back;
		activate();
	}
}
