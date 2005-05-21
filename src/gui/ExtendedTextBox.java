/*
 * Created on 22/05/2005
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
    private Activatable back;

    private int modifier;

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
