package terminal;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
//#ifdef midp2
import javax.microedition.lcdui.game.Sprite;
//#endif

import app.Main;

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
 * Class that acts as terminal. It can basicly draw input from emulation (see
 * variable "buffer"), execute and store actions defined by user.
 */

public class Terminal extends Canvas {

	/** the VDU buffer */
	protected vt320 buffer;

	/** first top and left character in buffer, that is displayed */
	protected int top, left;
	
	protected int width, height;
	
	protected boolean rotated;

	/** display size in characters */
	public int rows, cols;

	private Image backingStore = null;

	private DrawFont font;

	public int fgcolor = 0x000000;

	public int bgcolor = 0xffffff;

	/** A list of colors used for representation of the display */
	private int color[] = {
			0x000000, 0xff0000, 0x00ff00, 0xffff00, // yellow
			0x0000ff, // blue
			0x00ffff, // magenta
			0x00ffff, // cyan
			0xffffff, // white
			0xffffff, // bold color
			0xffffff, // inverted color
	};

	public final static int COLOR_INVERT = 9;

	public Terminal( vt320 buffer ) {
		this( buffer, false );
	}
	
	public Terminal( vt320 buffer, boolean rotated ) {
		setVDUBuffer( buffer );

		if ( Main.useColors ) {
			fgcolor = 0xffffff;
			bgcolor = 0x000000;
		}

		font = new DrawFont();

//#ifdef midp2
		this.rotated = rotated;
//#else
		this.rotated = rotated = false;
//#endif
		
		width = getWidth();
		height = getHeight();
		if ( rotated ) {
			width = getHeight();
			height = getWidth();
		}
		cols = width / font.width;
		rows = height / font.height;
		backingStore = Image.createImage( width, height );
		
		System.out.println( "ROWS " + rows + " COLS " + cols );

		buffer.setScreenSize( cols, rows );

		top = 0;
		left = 0;
	}

	/**
	 * Create a color representation that is brighter than the standard color
	 * but not what we would like to use for bold characters.
	 * 
	 * @param clr
	 *            the standard color
	 * @return the new brighter color
	 */
	private int brighten( int color ) {
		int r = ( color & 0xff0000 ) >> 16;
		int g = ( color & 0x00ff00 ) >> 8;
		int b = ( color & 0x0000ff );

		r *= 12;
		r /= 10;
		if ( r > 255 ) {
			r = 255;
		}
		g *= 12;
		g /= 10;
		if ( g > 255 ) {
			g = 255;
		}
		b *= 12;
		b /= 10;
		if ( b > 255 ) {
			b = 255;
		}
		return b | ( g << 8 ) | ( r << 16 );
	}

	/**
	 * Create a color representation that is darker than the standard color but
	 * not what we would like to use for bold characters.
	 * 
	 * @param clr
	 *            the standard color
	 * @return the new darker color
	 */
	private int darken( int color ) {
		int r = ( color & 0xff0000 ) >> 16;
		int g = ( color & 0x00ff00 ) >> 8;
		int b = ( color & 0x0000ff );

		r *= 8;
		r /= 10;
		g *= 8;
		g /= 10;
		b *= 8;
		b /= 10;
		return b | ( g << 8 ) | ( r << 16 );
	}
	
	private Object paintMutex = new Object();

	protected void paint( Graphics g ) {
		
		// Erase display
		g.setColor( bgcolor );
		g.fillRect( 0, 0, getWidth(), getHeight() );

		// Draw terminal image
		synchronized ( paintMutex ) {
			// Redraw backing store if necessary
			redrawBackingStore();
			
			if ( !rotated ) {
				g.drawImage( backingStore, 0, 1, Graphics.TOP | Graphics.LEFT );
			}
			else {
//#ifdef midp2
				g.drawRegion( backingStore, 0, 0, width - 1, height, Sprite.TRANS_ROT270, 0, 1, Graphics.TOP | Graphics.LEFT );
//#endif
			}
			// KARL the y coord 1 is because with 0 it sometimes fails to draw
			// on my SonyEricsson K700i
		}
	}

	private boolean invalid = true;

	public void redraw() {
	    synchronized ( paintMutex ) {
	        invalid = true;
	        repaint();
	    }
	}

	/** Required paint implementation */
	/*
	 * protected void paint(Graphics g) { g.setColor(bgcolor); g.fillRect( 0, 0,
	 * getWidth(), getHeight() ); g.drawImage(backingStore, 0, 0, 0); }
	 */

	protected void redrawBackingStore() {
		// Only redraw if we've been marked as invalid by a call to redraw
		// The idea is that if multiple calls to redraw occur before the call to
		// paint then we save
		// time not redrawing our backingStore each time
		if ( invalid ) {
			Graphics g = backingStore.getGraphics();
			g.setColor( fgcolor );
			g.fillRect( 0, 0, width, height );

			for ( int l = top; l < buffer.height && l < ( top + rows ); l++ ) {
				if ( !buffer.update[0] && !buffer.update[l + 1] ) {
					continue;
				}
				buffer.update[l + 1] = false;
				for ( int c = left; c < buffer.width && c < ( left + cols ); c++ ) {
					int addr = 0;
					int currAttr = buffer.charAttributes[buffer.windowBase + l][c];

					int fg = darken( fgcolor );
					int bg = darken( bgcolor );

					if ( ( currAttr & VDUBuffer.COLOR_FG ) != 0 ) {
						fg = darken( color[( ( currAttr & VDUBuffer.COLOR_FG ) >> 4 ) - 1] );
					}
					if ( ( currAttr & VDUBuffer.COLOR_BG ) != 0 ) {
						bg = darken( darken( color[( ( currAttr & VDUBuffer.COLOR_BG ) >> 8 ) - 1] ) );

						// bold font handling was DELETED

					}
					if ( ( currAttr & VDUBuffer.LOW ) != 0 ) {
						fg = darken( fg );
					}
					if ( ( currAttr & VDUBuffer.INVERT ) != 0 ) {
						int swapc = bg;
						bg = fg;
						fg = swapc;
					}

					// determine the maximum of characters we can print in one
					// go
					while ( ( c + addr < buffer.width )
							&& ( ( buffer.charArray[buffer.windowBase + l][c + addr] < ' ' ) || ( buffer.charAttributes[buffer.windowBase
									+ l][c + addr] == currAttr ) ) ) {
						if ( buffer.charArray[buffer.windowBase + l][c + addr] < ' ' ) {
							buffer.charArray[buffer.windowBase + l][c + addr] = ' ';
							buffer.charAttributes[buffer.windowBase + l][c + addr] = 0;
							continue;
						}
						addr++;
					}

					// clear the part of the screen we want to change (fill
					// rectangle)
					if ( Main.useColors )
						g.setColor( bg );
					else
						g.setColor( bgcolor );

					g.fillRect( ( c - left ) * font.width, ( l - top ) * font.height, addr * font.width, font.height );

					if ( Main.useColors )
						g.setColor( fg );
					else
						g.setColor( fgcolor );

					// draw the characters
					font.drawChars( g, buffer.charArray[buffer.windowBase + l], c, addr, ( c - left ) * font.width,
							( l - top ) * font.height );

					c += addr - 1;
				}
			}

			// draw cursor
			if ( buffer.showcursor
					&& ( buffer.screenBase + buffer.cursorY >= buffer.windowBase && buffer.screenBase + buffer.cursorY < buffer.windowBase
							+ buffer.height ) ) {
				g.setColor( fgcolor );
				g.fillRect( ( buffer.cursorX - left ) * font.width,
						( buffer.cursorY - top + buffer.screenBase - buffer.windowBase ) * font.height, font.width,
						font.height );
			}

			invalid = false;
		}
	}

	/**
	 * Set a new terminal (VDU) buffer.
	 * 
	 * @param buffer
	 *            new buffer
	 */
	public void setVDUBuffer( vt320 buffer ) {
		this.buffer = buffer;
		buffer.setDisplay( this );
	}
	
	public vt320 getVDUBuffer() {
		return buffer;
	}

}