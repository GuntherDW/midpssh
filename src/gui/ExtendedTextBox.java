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
package gui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

import app.Main;

/**
 * @author Karl
 *
 */
public abstract class ExtendedTextBox extends TextBox implements Activatable, CommandListener {
    protected Activatable back;

    public ExtendedTextBox( String title, String text, int maxSize, int constraints ) {
        super( title, text, maxSize, constraints);

        setCommandListener( this );
    }

    /*
     * (non-Javadoc)
     * 
     * @see gui.Activatable#activate()
     */
    public void activate() {
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
        if ( command != MessageForm.backCommand ) {
            if (handleText(command, getString())) {
                back.activate();
            }
        }
        else {
            back.activate();
        }
    }
    
    protected abstract boolean handleText(Command command, String text);
}
