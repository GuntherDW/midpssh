/* This file is part of "MidpSSH".
 * Copyright (c) 2005 Karl von Randow.
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
import app.SessionSpec;
import app.session.SshSession;


/**
 * @author Karl
 *
 */
public class PasswordDialog extends ExtendedTextBox {

    private SshSession session;
    
    private SessionSpec conn;
    
    /**
     * @param title
     * @param text
     * @param maxSize
     * @param constraints
     */
    public PasswordDialog(SshSession session, SessionSpec conn) {
        super("Password", "", 255, TextField.PASSWORD);
        
        addCommand(MessageForm.okCommand);
        addCommand(MessageForm.backCommand);
        
        this.session = session;
        this.conn = conn;
    }
    
    protected boolean handleText(Command command, String text) {
        session.connect( conn, text );
        Main.openSession( session );
        return false;
    }
}
