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

import java.io.IOException;

import ssh.SshIO;

/**
 * @author Karl von Randow
 * 
 */
public class SshSession extends Session {

	public void connect( String host, String username, String password ) {
		super.connect( host, new SshIOFilter( username, password ) );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see telnet.Session#defaultPort()
	 */
	protected int defaultPort() {
		return 22;
	}

	public class SshIOFilter implements SessionIOListener {

		private SshIO sshIO;

		public SshIOFilter( String username, String password ) {
			sshIO = new SshIO() {
				public void write( byte[] data ) throws IOException {
					SshSession.this.sendData( data, 0, data.length );
				}

				public String getTerminalID() {
					return emulation.getTerminalID();
				}

				public int getTerminalWidth() {
					return emulation.width;
				}

				public int getTerminalHeight() {
					return emulation.height;
				}
			};
			sshIO.login = username;
			sshIO.password = password;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see app.session.SessionIOListener#receiveData(byte[], int, int)
		 */
		public void receiveData( byte[] data, int offset, int length ) throws IOException {
			byte[] result;
			result = sshIO.handleSSH( data, offset, length );
			
			SshSession.this.receiveData( result, 0, result.length );
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see app.session.SessionIOListener#sendData(byte[], int, int)
		 */
		public void sendData( byte[] data, int offset, int length ) throws IOException {
			sshIO.sendData( data, offset, length );
		}
	}
}