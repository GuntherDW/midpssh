/* This file is part of "MidpSSH".
 * Copyright (c) 2004 Karl von Randow.
 * 
 * MidpSSH is based upon Telnet Floyd and FloydSSH by Radek Polak.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 *
 */
package gui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import app.Main;

/**
 * @author Karl von Randow
 *
 */
public abstract class EditableMenu extends ExtendedList implements CommandListener, Activatable {

	protected static Command defaultSelectCommand = new Command( "Select", Command.ITEM, 1 );

	protected static Command newCommand = new Command( "New", Command.SCREEN, 8 );

	protected static Command editCommand = new Command( "Edit", Command.ITEM, 9 );

	protected static Command deleteCommand = new Command( "Delete", Command.ITEM, 10 );

	protected static Command backCommand = new Command( "Back", Command.BACK, 2 );
	
	protected Command selectCommand = defaultSelectCommand;
	
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
	
	protected void replaceSelectCommand( Command selectCommand ) {
		removeCommand( this.selectCommand );
		this.selectCommand = selectCommand;
		
		setSelectCommand( selectCommand );
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
