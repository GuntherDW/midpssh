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
package app;

import javax.microedition.rms.RecordComparator;

/**
 * Bytewise comparator for stored record sets. Ensure that the sort key is the first element in the
 * byte array for the record and this should handle the sorting for us quickly.
 * @author Karl von Randow
 */
public class BytewiseRecordComparator implements RecordComparator {

	/* (non-Javadoc)
	 * @see javax.microedition.rms.RecordComparator#compare(byte[], byte[])
	 */
	public int compare( byte[] a, byte[] b ) {
		for ( int i = 0; i < a.length && i < b.length; i++ ) {
			if ( a[i] < b[i] ) {
				return PRECEDES;
			}
			else if ( a[i] > b[i] ) {
				return FOLLOWS;
			}
		}
		if ( a.length < b.length ) {
			return PRECEDES;
		}
		else if ( a.length > b.length ) {
			return FOLLOWS;
		}
		else {
			return EQUIVALENT;
		}
	}
}
