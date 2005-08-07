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
package gui.session;

import gui.ExtendedTextBox;
import gui.MessageForm;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.TextField;

import app.Main;
import app.Settings;
import app.session.Session;

/**
 * @author Karl von Randow
 * 
 */
public class ModifierInputDialog extends ExtendedTextBox {

	private int modifier;

	public ModifierInputDialog( String title, int modifier ) {
		super( title, "", 10, TextField.ANY );

		this.modifier = modifier;
        addCommand(InputDialog.enterCommand);
        addCommand(MessageForm.backCommand);
        
//#ifdef midp2
        if (!Settings.predictiveText) {
        	setConstraints(TextField.ANY | TextField.NON_PREDICTIVE);
        }
//#endif
	}

    protected boolean handleText(Command command, String str) {
		Session session = Main.currentSession();
		if ( session != null ) {
			for ( int i = 0; i < str.length(); i++ ) {
				session.typeChar( str.charAt( i ), modifier );
			}
		}
        return true;
	}
}