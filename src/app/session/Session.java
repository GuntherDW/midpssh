/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.session;

import gui.session.SessionTerminal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

import terminal.vt320;

import app.Activatable;
import app.Main;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class Session implements SessionIOListener, Runnable, Activatable {
    /** Time to sleep between checks for new input from connection */
    public static int sleepTime = 1000;

    /**
     * After this count of cycles without communication on connection we will
     * send something just to keep connection alive
     */
    public static int keepAliveCycles = 300; // 5 minutes for sleepTime=1000

    protected vt320 emulation;

    protected SessionIOListener filter;

    private SessionTerminal terminal;

    private boolean disconnecting;

    /**
     * Holds the socket connetion object (from the Generic Connection Framework)
     * that is the basis of this connection.
     */
    private StreamConnection socket;

    /**
     * Holds the InputStream associated with the socket.
     */
    private DataInputStream in;

    /**
     * Holds the OutputStream associated with the socket.
     */
    private DataOutputStream out;

    private String host;

    private Thread reader;

    /**
     * We will collect here data for writing. Data will be sent when nothing is
     * in input stream, otherwise midlet hung up.
     * 
     * @see #run
     */
    private static byte[] output = new byte[16]; // this will grow if needed

    /**
     * Number of bytes to be written, from output array, because it has fixed
     * lenght.
     */
    private int outputCount = 0;

    private int traffic = 0;

    public Session() {
        emulation = new vt320() {
            public void sendData( byte[] b, int offset, int length ) throws IOException {
                filter.sendData( b, offset, length );
            }
        };
        terminal = new SessionTerminal( emulation, this );
        reader = new Thread( this );
    }

    protected void connect( String host, SessionIOListener filter ) {
        this.host = host;
        this.filter = filter;

        reader.start();
    }

    protected abstract int defaultPort();

    /*
     * (non-Javadoc)
     * 
     * @see telnet.TelnetIOListener#receiveData(byte[])
     */
    public void receiveData( byte[] buffer, int offset, int length ) throws IOException {
        if ( buffer != null && length > 0 ) {
            try {
                emulation.putString( new String( buffer, offset, length ) );
                terminal.redraw();
            }
            catch ( Exception e ) {

            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see telnet.TelnetIOListener#sendData(byte[])
     */
    public void sendData( byte[] b, int offset, int length ) throws IOException {
        if ( outputCount + length > output.length ) {
            byte[] newOutput = new byte[outputCount + length];
            System.arraycopy( output, 0, newOutput, 0, outputCount );
            output = newOutput;
        }
        System.arraycopy( b, offset, output, outputCount, length );
        outputCount += length;
    }

    /**
     * Continuously read from remote host and display the data on screen.
     */
    public void run() {
        Throwable terminator = null;

        try {
            int n;
            int noInputCycles = 0;

            // Connect
            try {
                emulation.putString( "Connecting..." );
                terminal.redraw();
                String conn = "socket://" + host;
                if ( host.indexOf( ":" ) == -1 )
                    conn += ":" + defaultPort();
                socket = (StreamConnection) Connector.open( conn, Connector.READ_WRITE, false );
                in = socket.openDataInputStream();
                out = socket.openDataOutputStream();
                emulation.putString( "OK" );
            }
            catch ( Exception e ) {
                emulation.putString( "FAILED" );
                terminal.redraw();
                return;
            }
            emulation.putString( "\n\r" );

            terminal.redraw();

            // Main loop
            try {
                for ( ;; ) {
                    n = in.available();

                    if ( n <= 0 ) {
                        if ( outputCount > 0 ) // Writing
                        {
                            traffic += outputCount;
                            out.write( output, 0, outputCount );
                            outputCount = 0;
                        }
                        else // Sleeping
                        {
                            Thread.sleep( sleepTime );
                            if ( noInputCycles++ > keepAliveCycles ) {
                                emulation.keyTyped( 0, 'a', 0 ); // BAD HACK -
                                // SHOULD
                                // SEND AYA...
                                emulation.keyPressed( 8, '\b', 0 );
                                noInputCycles = 0;
                            }
                        }
                    }
                    else // Reading
                    {
                        byte[] b = new byte[n]; // TODO keep one buffer
                        in.read( b, 0, n );
                        /*
                         * byte[] buffer = sshIO.handleSSH(tmp); if (buffer !=
                         * null && buffer.length > 0) { if (n > 0) { try {
                         * emulation.putString(new String(buffer));
                         * terminal.redraw(); } catch (Exception e) { } } }
                         */
                        filter.receiveData( b, 0, n );
                        noInputCycles = 0;
                    }
                } // while( connected )
            }
            catch ( Exception e ) {
                terminator = e;
            }
        }
        catch ( Throwable t ) {
            terminator = t;
        }

        if ( terminator != null && !disconnecting ) {
            Alert alert = new Alert( "Error" );
            alert.setType( AlertType.ERROR );

            String msg = terminator.getMessage();
            if ( msg == null )
                msg = terminator.toString();

            alert.setString( msg );
            Main.alertBackToMain( alert );
        }
        else {
            Main.goMainMenu();
        }

        disconnect();
        emulation.putString( "Connection closed" );
    }

    private void handleException( Throwable t ) {
        t.printStackTrace();
        if ( !disconnecting ) {
            Alert alert = new Alert( "Error" );
            alert.setType( AlertType.ERROR );

            String msg = t.getMessage();
            if ( msg == null )
                msg = t.toString();

            alert.setString( msg );
            Main.setDisplay( alert );
        }
    }

    /**
     * @return Returns the terminal.
     */
    public SessionTerminal getTerminal() {
        return terminal;
    }

    /*
     * (non-Javadoc)
     * 
     * @see telnet.Session#disconnect()
     */
    public void disconnect() {
        if ( !disconnecting ) {
            try {
                in.close();
                out.close();
                socket.close();
            }
            catch ( IOException e ) {
                handleException( e );
            }
            disconnecting = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see gui.Activatable#activate()
     */
    public void activate() {
        terminal.activate();
    }
}