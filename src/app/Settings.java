/*
 * Created on Oct 4, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Settings {
	
	public static final int DEFAULT_BGCOLOR = 0x000000, DEFAULT_FGCOLOR = 0xffffff;
	
	public int bgcolor, fgcolor;
	
	public int screenColumns, screenRows;
	
	public Settings() {
		defaults();
	}

	/**
	 * @param in
	 */
	public void read( DataInputStream in ) throws IOException {
		fgcolor = in.readInt();
		bgcolor = in.readInt();
		screenColumns = in.readInt();
		screenRows = in.readInt();
	}
	
	public void write( DataOutputStream out ) throws IOException {
		out.writeInt( fgcolor );
		out.writeInt( bgcolor );
		out.writeInt( screenColumns );
		out.writeInt( screenRows );
	}
	
	private void defaults() {
		fgcolor = DEFAULT_FGCOLOR;
		bgcolor = DEFAULT_BGCOLOR;
		screenColumns = 0;
		screenRows = 0;
	}
}
