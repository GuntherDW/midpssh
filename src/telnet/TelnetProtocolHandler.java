/*
 * This file is part of "The Java Telnet Application".
 *
 * (c) Matthias L. Jugel, Marcus Mei�ner 1996-2002. All Rights Reserved.
 *
 * Please visit http://javatelnet.org/ for updates and contact.
 * The file was changed by Radek Polak to work as midlet in MIDP 1.0
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

package telnet;

import java.io.IOException;

/**
 * This is a telnet protocol handler. The handler needs implementations for
 * several methods to handle the telnet options and to be able to read and write
 * the buffer.
 * <P>
 * <B>Maintainer: </B> Marcus Mei�ner
 * 
 * @version $Id$
 * @author Matthias L. Jugel, Marcus Mei�ner
 */
public abstract class TelnetProtocolHandler {

	/** temporary buffer for data-telnetstuff-data transformation */
	private byte[] tempbuf = new byte[0];

	/** the data sent on pressing <RETURN>\n */
	private byte[] crlf = new byte[2];

	/** the data sent on pressing <LineFeed>\r */
	private byte[] cr = new byte[2];

	/**
	 * Create a new telnet protocol handler.
	 */
	public TelnetProtocolHandler() {
		reset();

		crlf[0] = 13;
		crlf[1] = 10;
		cr[0] = 13;
		cr[1] = 0;
	}

	/**
	 * Get the current terminal type for TTYPE telnet option.
	 * 
	 * @return the string id of the terminal
	 */
	protected abstract String getTerminalType();

	/**
	 * Get the current window size of the terminal for the NAWS telnet option.
	 * 
	 * @return the size of the terminal as Dimension
	 */
	protected abstract Dimension getWindowSize();

	/**
	 * Set the local echo option of telnet.
	 * 
	 * @param echo
	 *            true for local echo, false for no local echo
	 */
	protected abstract void setLocalEcho( boolean echo );

	/**
	 * Generate an EOR (end of record) request. For use by prompt displaying.
	 */
	protected abstract void notifyEndOfRecord();

	/**
	 * Send data to the remote host.
	 * 
	 * @param b
	 *            array of bytes to send
	 */
	protected abstract void write( byte[] b ) throws IOException;

	/**
	 * Reset the protocol handler. This may be necessary after the connection
	 * was closed or some other problem occured.
	 */
	public void reset() {
		neg_state = 0;
		receivedDX = new byte[256];
		sentDX = new byte[256];
		receivedWX = new byte[256];
		sentWX = new byte[256];
	}

	// ===================================================================
	// the actual negotiation handling for the telnet protocol follows:
	// ===================================================================

	/** state variable for telnet negotiation reader */
	private byte neg_state = 0;

	/** constants for the negotiation state */
	private final static byte STATE_DATA = 0;

	private final static byte STATE_IAC = 1;

	private final static byte STATE_IACSB = 2;

	private final static byte STATE_IACWILL = 3;

	private final static byte STATE_IACDO = 4;

	private final static byte STATE_IACWONT = 5;

	private final static byte STATE_IACDONT = 6;

	private final static byte STATE_IACSBIAC = 7;

	private final static byte STATE_IACSBDATA = 8;

	private final static byte STATE_IACSBDATAIAC = 9;

	/** What IAC SB <xx>we are handling right now */
	private byte current_sb;

	/** IAC - init sequence for telnet negotiation. */
	private final static byte IAC = (byte) 255;

	/** [IAC] End Of Record */
	private final static byte EOR = (byte) 239;

	/** [IAC] WILL */
	private final static byte WILL = (byte) 251;

	/** [IAC] WONT */
	private final static byte WONT = (byte) 252;

	/** [IAC] DO */
	private final static byte DO = (byte) 253;

	/** [IAC] DONT */
	private final static byte DONT = (byte) 254;

	/** [IAC] Sub Begin */
	private final static byte SB = (byte) 250;

	/** [IAC] Sub End */
	private final static byte SE = (byte) 240;

	/** Telnet option: binary mode */
	private final static byte TELOPT_BINARY = (byte) 0; /* binary mode */

	/** Telnet option: echo text */
	private final static byte TELOPT_ECHO = (byte) 1; /* echo on/off */

	/** Telnet option: sga */
	private final static byte TELOPT_SGA = (byte) 3; /* supress go ahead */

	/** Telnet option: End Of Record */
	private final static byte TELOPT_EOR = (byte) 25; /* end of record */

	/** Telnet option: Negotiate About Window Size */
	private final static byte TELOPT_NAWS = (byte) 31; /* NA-WindowSize */

	/** Telnet option: Terminal Type */
	private final static byte TELOPT_TTYPE = (byte) 24; /* terminal type */

	private final static byte[] IACWILL = {
			IAC, WILL
	};

	private final static byte[] IACWONT = {
			IAC, WONT
	};

	private final static byte[] IACDO = {
			IAC, DO
	};

	private final static byte[] IACDONT = {
			IAC, DONT
	};

	private final static byte[] IACSB = {
			IAC, SB
	};

	private final static byte[] IACSE = {
			IAC, SE
	};

	/** Telnet option qualifier 'IS' */
	private final static byte TELQUAL_IS = (byte) 0;

	/** Telnet option qualifier 'SEND' */
	private final static byte TELQUAL_SEND = (byte) 1;

	/** What IAC DO(NT) request do we have received already ? */
	private byte[] receivedDX;

	/** What IAC WILL/WONT request do we have received already ? */
	private byte[] receivedWX;

	/** What IAC DO/DONT request do we have sent already ? */
	private byte[] sentDX;

	/** What IAC WILL/WONT request do we have sent already ? */
	private byte[] sentWX;

	/**
	 * Send a Telnet Escape character (IAC <code>)
	 */
	public void sendTelnetControl( byte code ) throws IOException {
		byte[] b = new byte[2];

		b[0] = IAC;
		b[1] = code;
		write( b );
	}

	/**
	 * Handle an incoming IAC SB &lt;type&gt; &lt;bytes&gt; IAC SE
	 * 
	 * @param type
	 *            type of SB
	 * @param sbata
	 *            byte array as &lt;bytes&gt;
	 * @param sbcount
	 *            nr of bytes. may be 0 too.
	 */
	private void handle_sb( byte type, byte[] sbdata, int sbcount ) throws IOException {
		switch ( type ) {
			case TELOPT_TTYPE:
				if ( sbcount > 0 && sbdata[0] == TELQUAL_SEND ) {
					write( new byte[] {
							IAC, SB, TELOPT_TTYPE, TELQUAL_IS
					} );
					/*
					 * FIXME: need more logic here if we use more than one
					 * terminal type
					 */
					String ttype = getTerminalType();
					write( ttype.getBytes() );
					write( IACSE );
				}

		}
	}

	/**
	 * Do not send any notifications at startup. We do not know, whether the
	 * remote client understands telnet protocol handling, so we are silent.
	 * (This used to send IAC WILL SGA, but this is false for a compliant
	 * client.)
	 */
	public void startup() throws IOException {
	}

	/**
	 * Transpose special telnet codes like 0xff or newlines to values that are
	 * compliant to the protocol. This method will also send the buffer
	 * immediately after transposing the data.
	 * 
	 * @param buf
	 *            the data buffer to be sent
	 */
	public void transpose( byte[] buf ) throws IOException {
		int i;

		byte[] nbuf, xbuf;
		int nbufptr = 0;
		nbuf = new byte[buf.length * 2]; // FIXME: buffer overflows possible

		for ( i = 0; i < buf.length; i++ ) {
			switch ( buf[i] ) {
				// Escape IAC twice in stream ... to be telnet protocol
				// compliant
				// this is there in binary and non-binary mode.
				case IAC:
					nbuf[nbufptr++] = IAC;
					nbuf[nbufptr++] = IAC;
					break;
				// We need to heed RFC 854. LF (\n) is 10, CR (\r) is 13
				// we assume that the Terminal sends \n for lf+cr and \r for
				// just cr
				// linefeed+carriage return is CR LF */
				case 10: // \n
					if ( receivedDX[TELOPT_BINARY + 128] != DO ) {
						while ( nbuf.length - nbufptr < crlf.length ) {
							xbuf = new byte[nbuf.length * 2];
							System.arraycopy( nbuf, 0, xbuf, 0, nbufptr );
							nbuf = xbuf;
						}
						for ( int j = 0; j < crlf.length; j++ )
							nbuf[nbufptr++] = crlf[j];
						break;
					}
					else {
						// copy verbatim in binary mode.
						nbuf[nbufptr++] = buf[i];
					}
					break;
				// carriage return is CR NUL */
				case 13: // \r
					if ( receivedDX[TELOPT_BINARY + 128] != DO ) {
						while ( nbuf.length - nbufptr < cr.length ) {
							xbuf = new byte[nbuf.length * 2];
							System.arraycopy( nbuf, 0, xbuf, 0, nbufptr );
							nbuf = xbuf;
						}
						for ( int j = 0; j < cr.length; j++ )
							nbuf[nbufptr++] = cr[j];
					}
					else {
						// copy verbatim in binary mode.
						nbuf[nbufptr++] = buf[i];
					}
					break;
				// all other characters are just copied
				default:
					nbuf[nbufptr++] = buf[i];
					break;
			}
		}
		xbuf = new byte[nbufptr];
		System.arraycopy( nbuf, 0, xbuf, 0, nbufptr );
		write( xbuf );
	}

	public void setCRLF( String xcrlf ) {
		crlf = xcrlf.getBytes();
	}

	public void setCR( String xcr ) {
		cr = xcr.getBytes();
	}

	/**
	 * Handle telnet protocol negotiation. The buffer will be parsed and
	 * necessary actions are taken according to the telnet protocol. See <A
	 * HREF="RFC-Telnet-URL">RFC-Telnet </A>
	 * 
	 * @param buf
	 *            the byte buffer used for negotiation
	 * @param count
	 *            the amount of bytes in the buffer
	 * @return a new buffer after negotiation
	 */
	public int negotiate( byte nbuf[], int noffset, int length ) throws IOException {
		byte sbbuf[] = new byte[tempbuf.length];
		int count = tempbuf.length;
		byte[] buf = tempbuf;
		byte sendbuf[] = new byte[3];
		byte b, reply;
		int sbcount = 0;
		int boffset = 0;
		int orignoffset = noffset;
		boolean dobreak = false;
		int noffsetend = noffset + length;
		
		if ( count == 0 ) // buffer is empty.
			return -1;

		while ( !dobreak && ( boffset < count ) && ( noffset < noffsetend ) ) {
			b = buf[boffset++];
			// of course, byte is a signed entity (-128 -> 127)
			// but apparently the SGI Netscape 3.0 doesn't seem
			// to care and provides happily values up to 255
			if ( b >= 128 )
				b = (byte) ( (int) b - 256 );
			switch ( neg_state ) {
				case STATE_DATA:
					if ( b == IAC ) {
						neg_state = STATE_IAC;
						dobreak = true; // leave the loop so we can sync.
					}
					else
						nbuf[noffset++] = b;
					break;
				case STATE_IAC:
					switch ( b ) {
						case IAC:
							neg_state = STATE_DATA;
							nbuf[noffset++] = IAC;
							break;
						case WILL:
							neg_state = STATE_IACWILL;
							break;
						case WONT:
							neg_state = STATE_IACWONT;
							break;
						case DONT:
							neg_state = STATE_IACDONT;
							break;
						case DO:
							neg_state = STATE_IACDO;
							break;
						case EOR:
							notifyEndOfRecord();
							dobreak = true; // leave the loop so we can sync.
							neg_state = STATE_DATA;
							break;
						case SB:
							neg_state = STATE_IACSB;
							sbcount = 0;
							break;
						default:
							neg_state = STATE_DATA;
							break;
					}
					break;
				case STATE_IACWILL:
					switch ( b ) {
						case TELOPT_ECHO:
							reply = DO;
							setLocalEcho( false );
							break;
						case TELOPT_SGA:
							reply = DO;
							break;
						case TELOPT_EOR:
							reply = DO;
							break;
						case TELOPT_BINARY:
							reply = DO;
							break;
						default:
							reply = DONT;
							break;
					}
					if ( reply != sentDX[b + 128] || WILL != receivedWX[b + 128] ) {
						sendbuf[0] = IAC;
						sendbuf[1] = reply;
						sendbuf[2] = b;
						write( sendbuf );
						sentDX[b + 128] = reply;
						receivedWX[b + 128] = WILL;
					}
					neg_state = STATE_DATA;
					break;
				case STATE_IACWONT:
					switch ( b ) {
						case TELOPT_ECHO:
							setLocalEcho( true );
							reply = DONT;
							break;
						case TELOPT_SGA:
							reply = DONT;
							break;
						case TELOPT_EOR:
							reply = DONT;
							break;
						case TELOPT_BINARY:
							reply = DONT;
							break;
						default:
							reply = DONT;
							break;
					}
					if ( reply != sentDX[b + 128] || WONT != receivedWX[b + 128] ) {
						sendbuf[0] = IAC;
						sendbuf[1] = reply;
						sendbuf[2] = b;
						write( sendbuf );
						sentDX[b + 128] = reply;
						receivedWX[b + 128] = WILL;
					}
					neg_state = STATE_DATA;
					break;
				case STATE_IACDO:
					switch ( b ) {
						case TELOPT_ECHO:
							reply = WILL;
							setLocalEcho( true );
							break;
						case TELOPT_SGA:
							reply = WILL;
							break;
						case TELOPT_TTYPE:
							reply = WILL;
							break;
						case TELOPT_BINARY:
							reply = WILL;
							break;
						case TELOPT_NAWS:
							Dimension size = getWindowSize();
							receivedDX[b] = DO;
							if ( size == null ) {
								// this shouldn't happen
								write( new byte[] {
										IAC, WONT, TELOPT_NAWS
								} );
								reply = WONT;
								sentWX[b] = WONT;
								break;
							}
							reply = WILL;
							sentWX[b] = WILL;
							sendbuf[0] = IAC;
							sendbuf[1] = WILL;
							sendbuf[2] = TELOPT_NAWS;
							write( sendbuf );
							write( new byte[] {
									IAC, SB, TELOPT_NAWS, (byte) ( size.width >> 8 ), (byte) ( size.width & 0xff ),
									(byte) ( size.height >> 8 ), (byte) ( size.height & 0xff ), IAC, SE
							} );
							break;
						default:
							reply = WONT;
							break;
					}
					if ( reply != sentWX[128 + b] || DO != receivedDX[128 + b] ) {
						sendbuf[0] = IAC;
						sendbuf[1] = reply;
						sendbuf[2] = b;
						write( sendbuf );
						sentWX[b + 128] = reply;
						receivedDX[b + 128] = DO;
					}
					neg_state = STATE_DATA;
					break;
				case STATE_IACDONT:
					switch ( b ) {
						case TELOPT_ECHO:
							reply = WONT;
							setLocalEcho( false );
							break;
						case TELOPT_SGA:
							reply = WONT;
							break;
						case TELOPT_NAWS:
							reply = WONT;
							break;
						case TELOPT_BINARY:
							reply = WONT;
							break;
						default:
							reply = WONT;
							break;
					}
					if ( reply != sentWX[b + 128] || DONT != receivedDX[b + 128] ) {
						write( new byte[] {
								IAC, reply, b
						} );
						sentWX[b + 128] = reply;
						receivedDX[b + 128] = DONT;
					}
					neg_state = STATE_DATA;
					break;
				case STATE_IACSBIAC:
					if ( b == IAC ) {
						sbcount = 0;
						current_sb = b;
						neg_state = STATE_IACSBDATA;
					}
					else {
						System.err.println( "(bad) " + b + " " );
						neg_state = STATE_DATA;
					}
					break;
				case STATE_IACSB:
					switch ( b ) {
						case IAC:
							neg_state = STATE_IACSBIAC;
							break;
						default:
							current_sb = b;
							sbcount = 0;
							neg_state = STATE_IACSBDATA;
							break;
					}
					break;
				case STATE_IACSBDATA:
					switch ( b ) {
						case IAC:
							neg_state = STATE_IACSBDATAIAC;
							break;
						default:
							sbbuf[sbcount++] = b;
							break;
					}
					break;
				case STATE_IACSBDATAIAC:
					switch ( b ) {
						case IAC:
							neg_state = STATE_IACSBDATA;
							sbbuf[sbcount++] = IAC;
							break;
						case SE:
							handle_sb( current_sb, sbbuf, sbcount );
							current_sb = 0;
							neg_state = STATE_DATA;
							break;
						case SB:
							handle_sb( current_sb, sbbuf, sbcount );
							neg_state = STATE_IACSB;
							break;
						default:
							neg_state = STATE_DATA;
							break;
					}
					break;
				default:
					neg_state = STATE_DATA;
					break;
			}
		}
		
		// shrink tempbuf to new processed size.
		byte[] xb = new byte[count - boffset];
		System.arraycopy( tempbuf, boffset, xb, 0, count - boffset );
		tempbuf = xb;
		
		return ( noffset - orignoffset );
	}

	public void inputfeed( byte[] b, int offset, int len ) {
		/*byte[] xb = new byte[tempbuf.length + len];

		System.arraycopy( tempbuf, 0, xb, 0, tempbuf.length );
		System.arraycopy( b, 0, xb, tempbuf.length, len );
		tempbuf = xb;*/
		byte[] xb = new byte[tempbuf.length + len];

		System.arraycopy( tempbuf, 0, xb, 0, tempbuf.length );
		System.arraycopy( b, offset, xb, tempbuf.length, len );
		tempbuf = xb;
	}
}