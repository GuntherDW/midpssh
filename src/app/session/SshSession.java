/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.session;

import java.io.IOException;

import ssh.SshIO;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
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