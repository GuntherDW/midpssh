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
 * @author Karl von Randow
 * 
 */
public abstract class Session implements SessionIOHandler, Activatable {
	/**
	 * After this count of millisends without communication on connection we will
	 * send something just to keep connection alive
	 */
	public static int keepAliveTime = 1000 * 60 * 5; // 5 minutes

	protected vt320 emulation;

	protected SessionIOHandler filter;

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

	private int bytesWritten = 0, bytesRead = 0;

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

	protected void connect( String host, SessionIOHandler filter ) {
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
	
	public void typeChar( char c, int modifier ) {
		emulation.keyTyped( 0, c, modifier );
	}
	
	private boolean connect() throws IOException {
		emulation.putString( "Connecting to " + host + "..." );

		String conn = "socket://" + host;
		if ( host.indexOf( ":" ) == -1 )
			conn += ":" + defaultPort();
		socket = (StreamConnection) Connector.open( conn, Connector.READ_WRITE, false );
		in = socket.openDataInputStream();
		out = socket.openDataOutputStream();
		emulation.putString( "OK\r\n" );

		return true;
	}

	/**
	 * Continuously read from remote host and display the data on screen.
	 */
	private void read() throws IOException {
	    byte [] buf;
//#ifndef blackberry
		buf = new byte[512]; // try a smaller buffer, maybe works better on some phones
		
		// Read at least 1 byte, and at most the number of bytes available
		int a = in.available();
		int n = in.read( buf, 0, Math.max( 1, Math.min( a, buf.length ) ) );
		while ( n != -1 ) {
			bytesRead += n;
			try {
			    filter.receiveData( buf, 0, n );
			}
			catch ( RuntimeException e ) {
			    throw new RuntimeException( "read.filter: " + e );
			}
			
			a = in.available();
			n = in.read( buf, 0, Math.max( 1, Math.min( a, buf.length ) ) );
		}
//#else
	    buf = new byte[1];
	    int c = in.read();
	    while ( c != -1 ) {
	        bytesRead++;
	        buf[0] = (byte) ( c & 0xff );
	        try {
			    filter.receiveData( buf, 0, 1 );
			}
			catch ( RuntimeException e ) {
			    throw new RuntimeException( "read.filter: " + e );
			}
			c = in.read();
	    }
//#endif
	}
	
	private void write() throws IOException {
		final byte [] empty = new byte[0];
		
		while ( !disconnecting ) {
			synchronized ( writer ) {
				while ( outputCount == 0 && !disconnecting ) {
					try {
						writer.wait( keepAliveTime );
					}
					catch ( InterruptedException e ) {
					}
					if ( outputCount == 0 ) {
						// No data to send after timeout so send an empty array through the filter which will trigger the
						// sending of a NOOP (see TelnetSession and SshSession) - this has the effect of a keepalive
						filter.sendData( empty, 0, 0 );
					}
				}
				
				if ( !disconnecting ) {
					bytesWritten += outputCount;
					out.write( outputBuffer, 0, outputCount );
					outputCount = 0;
				}
			}
		}
	}


	private void handleException( String where, Throwable t ) {
		if ( !disconnecting ) {
			t.printStackTrace();
			
			Alert alert = new Alert( "Session Error" );
			alert.setType( AlertType.ERROR );

			String msg = t.getMessage();
			if ( msg == null )
				msg = t.toString();

			alert.setString( where + ": " + msg );
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
					if ( in != null ) in.close();
					if ( out != null ) out.close();
					if ( socket != null ) socket.close();
				}
				catch ( IOException e ) {
					handleException( "Disconnect", e );
				}
				
				writer.notify();
			}
		}
	}
	
	private String bytesToString( int bytes ) {
		if ( bytes < 1024 ) {
			return bytes + " bytes";
		}
		else if ( bytes < 1024 * 1024 ) {
			return to2dp( bytes * 100 / 1024 ) + " KB";
		}
		else {
			return to2dp( bytes * 100 / ( 1024 * 1024 ) ) + " MB";
		}
	}
	
	private String to2dp( int i ) {
		String str = "" + i;
		return str.substring( 0, str.length() - 2 ) + "." + str.substring( str.length() - 2 );
	}
	
	private void sessionReport() {
		String report = "Traffic Report\n" +
			"IN " + bytesToString( bytesRead ) + "\n" +
			"OUT " + bytesToString( bytesWritten ) + "\n" +
			"TOTAL " + bytesToString( bytesRead + bytesWritten ) + "\n";
		Alert alert = new Alert( "Session Report" );
		alert.setType( AlertType.INFO );
	
		alert.setString( report );
		alert.setTimeout( Alert.FOREVER );
		Main.setDisplay( alert );
	}
	
	public void goMainMenu() {
		Main.goMainMenu();
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
				handleException( "Reader", e );
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
				sessionReport();
			}
			catch ( Exception e ) {
				handleException( "Writer", e );
				disconnect();
				terminal.disconnected();
			}
		}
	}
}