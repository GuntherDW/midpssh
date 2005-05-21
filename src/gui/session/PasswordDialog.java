/*
 * Created on 22/05/2005
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
