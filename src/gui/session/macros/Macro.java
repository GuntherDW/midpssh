/*
 * Created on Oct 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gui.session.macros;


/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Macro {
	private String name;
	
	private String value;
	
	/**
	 * @param name
	 * @param value
	 */
	public Macro( String name, String value ) {
		super();
		this.name = name;
		this.value = value;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}
}
