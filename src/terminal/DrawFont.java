package terminal;

import java.io.InputStream;

import javax.microedition.lcdui.Graphics;

/* This file is part of "Telnet Floyd".
 *
 * (c) Radek Polak 2003-2004. All Rights Reserved.
 *
 * Please visit project homepage at http://phoenix.inf.upol.cz/~polakr
 * 
 * This file has been modified by Karl von Randow for MidpSSH.
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

/**
 * You can draw characters using this class. Character size is 3x5 resp. 4x6
 * when taking into a count spaces between them.
 */

public class DrawFont {
	
	private static final String FONT_RESOURCE = "/font";

	public final int width = 4;

	public final int height = 6;

	public int[][] data = new int[128][];

	public DrawFont() {
		try {
			InputStream in = getClass().getResourceAsStream( FONT_RESOURCE );
			for ( int i = 33; i < 128; i++ ) {
				int b = in.read();
				int l = ( b & 3 ) + 2; // length could be 1,2,3 or 4; this is
				// len+1
				data[i] = new int[l]; // one more for template
				data[i][0] = ( b >> 2 ) - 32; // draw this template
				//        System.out.println("--- ascii "+i +"---" );
				//        System.out.println("header "+b );
				//        System.out.println("len "+(l-1) );
				//        System.out.println("template "+ data[i][0] );
				for ( int j = 1; j < l; j++ ) {
					data[i][j] = in.read();
					//          System.out.println("data["+j+"]" + data[i][j] );
				}
			}
			in.close();
		}
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	public void drawChars( Graphics g, char[] chars, int offset, int length, int x, int y ) {
		for ( int i = offset; i < offset + length; i++ ) {
			drawChar( g, chars[i], x, y );
			x += width;
		}
	}

	public void drawChar( Graphics g, char c, int x, int y ) {
		if ( c >= data.length || data[c] == null )
			return;
		for ( int j = 1; j < data[c].length; j++ ) {
			int x1 = data[c][j] & 3;
			int y1 = ( data[c][j] & 12 ) >> 2;
			int x2 = ( data[c][j] & 48 ) >> 4;
			int y2 = ( data[c][j] & 192 ) >> 6;

			if ( x1 == 3 ) {
				x1 = y1;
				y1 = 4;
			}

			if ( x2 == 3 ) {
				x2 = y2;
				y2 = 4;
			}

			//      System.out.println( "char " + c + " x1=" + x1 + " y1="+ (4-y1) +"
			// x2=" + x2 + " y2="+ (4-y2));

			g.drawLine( x + x1, y + y1, x + x2, y + y2 );
		}
		if ( data[c][0] != 0 )
			drawChar( g, (char) ( c + data[c][0] ), x, y ); // draw template
	}

}