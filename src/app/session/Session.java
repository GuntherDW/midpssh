/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.session;

import gui.Activatable;
import gui.session.SessionTerminal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;

import terminal.vt320;
import app.Main;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class Session implements SessionIOListener, Activatable {
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

	private Thread reader, writer;

	/**
	 * We will collect here data for writing. Data will be sent when nothing is
	 * in input stream, otherwise midlet hung up.
	 * 
	 * @see #run
	 */
	private static byte[] outputBuffer = new byte[16]; // this will grow if needed

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
		reader = new Reader();
		writer = new Writer();
	}

	protected void connect( String host, SessionIOListener filter ) {
		this.host = host;
		this.filter = filter;

		//reader.start();
		writer.start();
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
		synchronized ( writer ) {
			if ( outputCount + length > outputBuffer.length ) {
				byte[] newOutput = new byte[outputCount + length];
				System.arraycopy( outputBuffer, 0, newOutput, 0, outputCount );
				outputBuffer = newOutput;
			}
			System.arraycopy( b, offset, outputBuffer, outputCount, length );
			outputCount += length;
			
			writer.notify();
		}
	}
	
	public void typeString( String str ) {
		for ( int i = 0; i < str.length(); i++ ) {
			emulation.keyTyped( 0, str.charAt( i ), 0 );
		}
	}
	
	private boolean connect() {
		try {
			emulation.putString( "Connecting to " + host + "..." );
			terminal.redraw();
			String conn = "socket://" + host;
			if ( host.indexOf( ":" ) == -1 )
				conn += ":" + defaultPort();
			socket = (StreamConnection) Connector.open( conn, Connector.READ_WRITE, false );
			in = socket.openDataInputStream();
			out = socket.openDataOutputStream();
			emulation.putString( "OK\r\n" );

			terminal.redraw();
			return true;
		}
		catch ( Exception e ) {
			emulation.putString( "FAILED\r\n" );
			emulation.putString( e.toString() );
			terminal.redraw();
			return false;
		}
	}

	/**
	 * Continuously read from remote host and display the data on screen.
	 */
	private void read() throws IOException {
		byte [] buf = new byte[1024];
		
		int n = in.read( buf, 0, buf.length );
		while ( n != -1 ) {
			filter.receiveData( buf, 0, n );
			
			n = in.read( buf, 0, buf.length );
		}
	}
	
	private void write() throws IOException {
		while ( !disconnecting ) {
			synchronized ( writer ) {
				while ( outputCount == 0 && !disconnecting ) {
					try {
						writer.wait( sleepTime * keepAliveCycles );
					}
					catch ( InterruptedException e ) {
					}
					// TODO send keepalive
				}
				
				if ( !disconnecting ) {
					out.write( outputBuffer, 0, outputCount );
					outputCount = 0;
				}
				
				/*
				 * if ( outputCount > 0 ) // Writing
				{
					traffic += outputCount;
					out.write( outputBuffer, 0, outputCount );
					outputCount = 0;
				}
				else // Sleeping
				{
					Thread.sleep( sleepTime );
					if ( noInputCycles++ > keepAliveCycles ) {
						emulation.keyTyped( 0, 'a', 0 ); // BAD HACK - SHOULD SEND AYA...
						emulation.keyPressed( 8, '\b', 0 );
						noInputCycles = 0;
					}
				}
				 */
			}
		}
	}


	private void handleException( Throwable t ) {
		if ( !disconnecting ) {
			t.printStackTrace();
			
			Alert alert = new Alert( "Session Error" );
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
			synchronized ( writer ) {
				disconnecting = true;
				try {
					in.close();
					out.close();
					socket.close();
				}
				catch ( IOException e ) {
					handleException( e );
				}
				
				writer.notify();
			}
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
	
	public void activate( Activatable back ) {
		activate();
	}
	
	private class Reader extends Thread {
		public void run() {
			try {
				read();
				disconnect();
			}
			catch ( Exception e ) {
				handleException( e );
				disconnect();
			}
		}
	}
	
	private class Writer extends Thread {
		public void run() {
			try {
				connect();
				terminal.connected();
				reader.start();
				write();
				
				disconnect();
				terminal.disconnected();
			}
			catch ( Exception e ) {
				handleException( e );
				disconnect();
			}
		}
	}
}