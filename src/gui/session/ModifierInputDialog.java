/*
 * Created on Oct 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session;

import gui.Activatable;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;

import terminal.vt320;
import app.Main;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ModifierInputDialog extends Form implements Activatable, CommandListener {

	private static Command enterCommand = new Command( "Enter", Command.OK, 1 );

	private static Command backCommand = new Command( "Back", Command.CANCEL, 2 );

	private vt320 vt;

	private TextField tf;

	private Activatable back;

	public int modifier;

	public ModifierInputDialog( vt320 vt ) {
		super( "Control Keys" );
		//super("Control Keys", "", 10, TextField.ANY);

		this.vt = vt;

		tf = new TextField( "Enter one or more letters", null, 10, TextField.ANY );
		append( tf );

		addCommand( enterCommand );
		addCommand( backCommand );

		setCommandListener( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gui.Activatable#activate()
	 */
	public void activate() {
		tf.setString( "" );
		Main.setDisplay( this );
	}
	
	public void activate( Activatable back ) {
		this.back = back;
		activate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
	 *      javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable arg1 ) {
		if ( command == enterCommand ) {
			String str = tf.getString();
			for ( int i = 0; i < str.length(); i++ ) {
				vt.keyTyped( 0, str.charAt( i ), modifier );
			}
		}

		back.activate();
	}
}