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
package gui.session.macros;


/**
 * @author Karl von Randow
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
        if ( value.endsWith( "\n" ) && !value.endsWith( "\r\n" ) ) {
            // Fix old macro values that just ended \n
            return value.substring( 0, value.length() - 1 ) + "\r\n";
        }
        else {
            return value;
        }
	}
}
