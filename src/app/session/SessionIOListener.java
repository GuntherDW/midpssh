/*
 * Created on Oct 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.session;

import java.io.IOException;

/**
 * @author Karl
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface SessionIOListener {
	public void sendData( byte[] data, int offset, int length ) throws IOException;

	public void receiveData( byte[] data, int offset, int length ) throws IOException;
}