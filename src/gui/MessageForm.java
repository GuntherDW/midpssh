/*
 * Created on Oct 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;

import app.Main;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MessageForm extends Form implements Activatable, CommandListener {
	
	private static Command okCommand = new Command( "OK",Command.OK, 1 );
	
	private Activatable back;
	
	public MessageForm( String title, String msg) {
		super( title );
		
		append( new StringItem( null, msg ) );
		addCommand( okCommand );
		setCommandListener( this );
	}
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable arg1 ) {
		if ( command == okCommand ) {
			doBack();
		}
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
	
	protected void doBack() {
		back.activate();
	}
}
