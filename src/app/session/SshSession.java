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
import app.SessionSpec;
import app.Settings;

/**
 * @author Karl von Randow
 * 
 */
public class SshSession extends Session implements SessionIOHandler {

    private SshIO sshIO;
    
	public void connect( SessionSpec spec ) {
        sshIO = new SshIO( this );
        sshIO.login = spec.username;
        sshIO.password = spec.password;
        
		super.connect( spec, this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see telnet.Session#defaultPort()
	 */
	protected int defaultPort() {
		return 22;
	}
    /*
     * (non-Javadoc)
     * 
     * @see app.session.SessionIOListener#receiveData(byte[], int, int)
     */
    public void handleReceiveData( byte[] data, int offset, int length ) throws IOException {
        byte[] result;
        result = sshIO.handleSSH( data, offset, length );
        super.receiveData( result, 0, result.length );
    }

    /*
     * (non-Javadoc)
     * 
     * @see app.session.SessionIOListener#sendData(byte[], int, int)
     */
    public void handleSendData( byte[] data, int offset, int length ) throws IOException {
        if ( length > 0 ) {
            sshIO.sendData( data, offset, length );
        }
        else {
            sshIO.Send_SSH_NOOP();
        }
    }
    
    /*
     * Receive data send back by SshIO and send it out onto the network
     */
    public void sendData( byte [] data ) throws IOException {
        super.sendData( data, 0, data.length );
    }

	public String getTerminalID() {
		if ( Settings.terminalType.length() > 0 ) {
			return Settings.terminalType;
		}
		else {
			return emulation.getTerminalID();
		}
	}

	public int getTerminalWidth() {
		return emulation.width;
	}

	public int getTerminalHeight() {
		return emulation.height;
	}
}