/*
 * Created on Oct 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session.macros;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import app.session.MacroSetManager;

/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MacroSet {
	
	private static final byte VERSION = 1;
	
	private String name;
	
	private Vector macros = new Vector();
	
	/**
	 * @return Returns the macros.
	 */
	public Vector getMacros() {
		return macros;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName( String name ) {
		this.name = name;
	}
	/**
	 * @param in
	 * @throws IOException
	 */
	public void read( DataInputStream in ) throws IOException {
		int version = in.readByte(); // for future use
		setName( in.readUTF() );
		
		int n = in.readByte();
		for ( int i = 0; i < n; i++ ) {
			String name = in.readUTF();
			String value = in.readUTF();
			
			Macro macro = new Macro( name, value );
			macros.addElement( macro );
		}
	}
	/**
	 * @param dout
	 * @throws IOException
	 */
	public void write( DataOutputStream out ) throws IOException {
		out.writeByte( VERSION );
		out.writeUTF( getName() );
		
		out.writeByte( macros.size() );
		for ( int i = 0; i < macros.size(); i++ ) {
			Macro macro = (Macro) macros.elementAt( i );
			out.writeUTF( macro.getName() );
			out.writeUTF( macro.getValue() );
		}
	}
	
	public Macro getMacro( int i ) {
		if ( i < 0 )
			return null;
		if ( i >= macros.size() )
			return null;
		return (Macro) macros.elementAt( i );
	}
	
	public void addMacro( Macro macro ) {
		macros.addElement( macro );
		MacroSetManager.saveMacroSets();
	}
	
	public void deleteMacro( int i ) {
		if ( i < 0 )
			return;
		if ( i >= macros.size() )
			return;
		macros.removeElementAt( i );
		MacroSetManager.saveMacroSets();
	}
	
	public void replaceMacro( int i, Macro macro ) {
		if ( i < 0 )
			return;
		if ( i >= macros.size() ) {
			macros.addElement( macro );
		}
		else {
			macros.setElementAt( macro, i );
		}
		MacroSetManager.saveMacroSets();
	}
}
