/*
 * Created on Oct 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import app.Main;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class EditableMenu extends ExtendedList implements CommandListener, Activatable {

	private static Command selectCommand = new Command( "Select", Command.ITEM, 1 );

	private static Command newCommand = new Command( "New", Command.SCREEN, 8 );

	private static Command editCommand = new Command( "Edit", Command.ITEM, 9 );

	private static Command deleteCommand = new Command( "Delete", Command.ITEM, 10 );

	private static Command backCommand = new Command( "Back", Command.BACK, 2 );
	
	private Activatable back;

	public EditableMenu( String title ) {
		super( title, List.IMPLICIT );
		
		addCommand( selectCommand );
		addCommand( newCommand );
		addCommand( editCommand );
		addCommand( deleteCommand );
		addCommand( backCommand );

		setCommandListener( this );
	}
	
	protected abstract void addItems();
	
	/* (non-Javadoc)
	 * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command, javax.microedition.lcdui.Displayable)
	 */
	public void commandAction( Command command, Displayable displayable ) {
		if ( command == List.SELECT_COMMAND || command == selectCommand ) {
			doSelect( getSelectedIndex() );
		}
		else if ( command == newCommand ) {
			doNew();
		}
		else if ( command == editCommand ) {
			doEdit( getSelectedIndex() );
		}
		else if ( command == deleteCommand ) {
			doDelete( getSelectedIndex() );
		}
		else if ( command == backCommand ) {
			doBack();
		}
	}
	
	/* (non-Javadoc)
	 * @see app.Activatable#activate()
	 */
	public void activate() {
		addItems();
		Main.setDisplay( this );
	}
	
	public void activate( Activatable back ) {
		this.back = back;
		activate();
	}

	protected abstract void doSelect( int i );

	protected abstract void doEdit( int i );

	protected abstract void doDelete( int i );

	protected abstract void doNew();

	protected void doBack() {
		back.activate();
	}
}
