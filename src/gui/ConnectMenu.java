/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui;

import java.util.Vector;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

import app.Activatable;
import app.ConnectionManager;
import app.ConnectionSpec;
import app.Main;
import app.session.SshSession;
import app.session.TelnetSession;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ConnectMenu extends ExtendedList implements CommandListener, Activatable {

    private static Command selectCommand = new Command( "Select", Command.ITEM, 1 );

    private static Command newCommand = new Command( "New", Command.SCREEN, 8 );

    private static Command editCommand = new Command( "Edit", Command.ITEM, 9 );

    private static Command deleteCommand = new Command( "Delete", Command.ITEM, 10 );

    private static Command backCommand = new Command( "Back", Command.BACK, 2 );

    private MainMenu mainMenu;

    private NewConnectionForm newConnectionForm = new NewConnectionForm( this );

    private EditConnectionForm editConnectionForm = new EditConnectionForm( this );

    /**
     * @param arg0
     * @param arg1
     */
    public ConnectMenu( MainMenu mainMenu ) {
        super( "Connect", List.IMPLICIT );

        this.mainMenu = mainMenu;

        //append( "Add...", null );
        addConnections();

        addCommand( selectCommand );
        addCommand( newCommand );
        addCommand( editCommand );
        addCommand( deleteCommand );
        addCommand( backCommand );

        setCommandListener( this );
    }

    private void addConnections() {
        deleteAll();

        Vector connections = ConnectionManager.getConnections();
        if ( connections != null ) {
            for ( int i = 0; i < connections.size(); i++ ) {
                ConnectionSpec conn = (ConnectionSpec) connections.elementAt( i );
                append( conn.alias, null );
            }
        }
    }

    public void activate() {
        addConnections();
        Main.setDisplay( this );
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.microedition.lcdui.CommandListener#commandAction(javax.microedition.lcdui.Command,
     *      javax.microedition.lcdui.Displayable)
     */
    public void commandAction( Command command, Displayable displayed ) {
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

    private void doSelect( int i ) {
        ConnectionSpec conn = ConnectionManager.getConnection( i );
        if ( conn != null ) {
            if ( conn.type.equals( ConnectionSpec.TYPE_SSH ) ) {
                SshSession session = new SshSession();
                session.connect( conn.host, conn.username, conn.password );
                session.activate();
            }
            else if ( conn.type.equals( ConnectionSpec.TYPE_TELNET ) ) {
                TelnetSession session = new TelnetSession();
                session.connect( conn.host );
                session.activate();
            }
        }
    }

    private void doEdit( int i ) {
        editConnectionForm.setConnectionIndex( i );
        Main.setDisplay( editConnectionForm );
    }

    private void doDelete( int i ) {
        ConnectionManager.deleteConnection( i );
    }

    private void doNew() {
        Main.setDisplay( newConnectionForm );
    }

    private void doBack() {
        Main.setDisplay( mainMenu );
    }
}