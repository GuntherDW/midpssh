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
	private int bgcolor;
	
	private int fgcolor;
	
	public Settings() {
		defaults();
	}
	
	private void defaults() {
		fgcolor = 0xffffff;
		bgcolor = 0x000000;
	}
	
	/**
	 * @return Returns the bgcolor.
	 */
	public int getBgcolor() {
		return bgcolor;
	}
	/**
	 * @param bgcolor The bgcolor to set.
	 */
	public void setBgcolor( int bgcolor ) {
		this.bgcolor = bgcolor;
	}
	/**
	 * @return Returns the fgcolor.
	 */
	public int getFgcolor() {
		return fgcolor;
	}
	/**
	 * @param fgcolor The fgcolor to set.
	 */
	public void setFgcolor( int fgcolor ) {
		this.fgcolor = fgcolor;
	}

	/**
	 * @param in
	 */
	public void read( DataInputStream in ) throws IOException {
		setFgcolor( in.readInt() );
		setBgcolor( in.readInt() );
	}
	
	public void write( DataOutputStream out ) throws IOException {
		out.writeInt( getFgcolor() );
		out.writeInt( getBgcolor() );
	}
}
