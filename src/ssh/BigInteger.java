/* java.math.BigInteger -- Arbitary precision integers
 Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003 Free Software Foundation, Inc.
 The file was changed by Radek Polak to work as midlet in MIDP 1.0

 This file is part of GNU Classpath.

 GNU Classpath is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2, or (at your option)
 any later version.

 GNU Classpath is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with GNU Classpath; see the file COPYING.  If not, write to the
 Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 02111-1307 USA.

 Linking this library statically or dynamically with other modules is
 making a combined work based on this library.  Thus, the terms and
 conditions of the GNU General Public License cover the whole
 combination.

 As a special exception, the copyright holders of this library give you
 permission to link this library with independent modules to produce an
 executable, regardless of the license terms of these independent
 modules, and to copy and distribute the resulting executable under
 terms of your choice, provided that you also meet, for each linked
 independent module, the terms and conditions of the license of that
 module.  An independent module is a module which is not derived from
 or based on this library.  If you modify this library, you may extend
 this exception to your version of the library, but you are not
 obligated to do so.  If you do not wish to do so, delete this
 exception statement from your version. */

package ssh;

/**
 * @author Warren Levy <warrenl@cygnus.com>
 * @date December 20, 1999.
 */

/**
 * Written using on-line Java Platform 1.2 API Specification, as well as "The
 * Java Class Libraries", 2nd edition (Addison-Wesley, 1998) and "Applied
 * Cryptography, Second Edition" by Bruce Schneier (Wiley, 1996).
 * 
 * Based primarily on IntNum.java BitOps.java by Per Bothner <per@bothner.com>
 * (found in Kawa 1.6.62).
 * 
 * Status: Believed complete and correct.
 */

public class BigInteger {
	/**
	 * All integers are stored in 2's-complement form. If words == null, the
	 * ival is the value of this BigInteger. Otherwise, the first ival elements
	 * of words make the value of this BigInteger, stored in little-endian
	 * order, 2's-complement form.
	 */
	transient private int ival;

	transient private int[] words;

	/** We pre-allocate integers in the range minFixNum..maxFixNum. */
	private static final int minFixNum = -100;

	private static final int maxFixNum = 1024;

	private static final int numFixNum = maxFixNum - minFixNum + 1;

	private static final BigInteger[] smallFixNums = new BigInteger[numFixNum];

	static {
		for ( int i = numFixNum; --i >= 0; )
			smallFixNums[i] = new BigInteger( i + minFixNum );
	}

	// JDK1.2
	public static final BigInteger ZERO = smallFixNums[-minFixNum];

	// JDK1.2
	public static final BigInteger ONE = smallFixNums[1 - minFixNum];

	/* Rounding modes: RADEK: just floor */
	//  private static final int FLOOR = 1;
	private BigInteger() {
	}

	/* Create a new (non-shared) BigInteger, and initialize to an int. */
	private BigInteger( int value ) {
		ival = value;
	}

	// RADEK: cunstruct new BigInteger assuming that signum = 1
	public BigInteger( byte[] magnitude ) {

		// Magnitude is always positive, so don't ever pass a sign of -1.
		words = byteArrayToIntArray( magnitude, 0 );
		BigInteger result = make( words, words.length );
		this.ival = result.ival;
		this.words = result.words;
	}

	/** Return a (possibly-shared) BigInteger with a given long value. */
	public static BigInteger valueOf( long val ) {
		if ( val >= minFixNum && val <= maxFixNum )
			return smallFixNums[(int) val - minFixNum];
		int i = (int) val;
		if ( (long) i == val )
			return new BigInteger( i );
		BigInteger result = alloc( 2 );
		result.ival = 2;
		result.words[0] = i;
		result.words[1] = (int) ( val >> 32 );
		return result;
	}

	/**
	 * Make a canonicalized BigInteger from an array of words. The array may be
	 * reused (without copying).
	 */
	private static BigInteger make( int[] words, int len ) {
		if ( words == null )
			return valueOf( len );
		len = BigInteger.wordsNeeded( words, len );
		if ( len <= 1 )
			return len == 0 ? ZERO : valueOf( words[0] );
		BigInteger num = new BigInteger();
		num.words = words;
		num.ival = len;
		return num;
	}

	/** Convert a big-endian byte array to a little-endian array of words. */
	private static int[] byteArrayToIntArray( byte[] bytes, int sign ) {
		// Determine number of words needed.
		int[] words = new int[bytes.length / 4 + 1];
		int nwords = words.length;

		// Create a int out of modulo 4 high order bytes.
		int bptr = 0;
		int word = sign;
		for ( int i = bytes.length % 4; i > 0; --i, bptr++ )
			word = ( word << 8 ) | ( bytes[bptr] & 0xff );
		words[--nwords] = word;

		// Elements remaining in byte[] are a multiple of 4.
		while ( nwords > 0 )
			words[--nwords] = bytes[bptr++] << 24 | ( bytes[bptr++] & 0xff ) << 16 | ( bytes[bptr++] & 0xff ) << 8
					| ( bytes[bptr++] & 0xff );
		return words;
	}

	/**
	 * Allocate a new non-shared BigInteger.
	 * 
	 * @param nwords
	 *            number of words to allocate
	 */
	private static BigInteger alloc( int nwords ) {
		BigInteger result = new BigInteger();
		if ( nwords > 1 )
			result.words = new int[nwords];
		return result;
	}

	/**
	 * Change words.length to nwords. We allow words.length to be upto nwords+2
	 * without reallocating.
	 */
	private void realloc( int nwords ) {
		if ( nwords == 0 ) {
			if ( words != null ) {
				if ( ival > 0 )
					ival = words[0];
				words = null;
			}
		}
		else if ( words == null || words.length < nwords || words.length > nwords + 2 ) {
			int[] new_words = new int[nwords];
			if ( words == null ) {
				new_words[0] = ival;
				ival = 1;
			}
			else {
				if ( nwords < ival )
					ival = nwords;
				System.arraycopy( words, 0, new_words, 0, ival );
			}
			words = new_words;
		}
	}

	private static int compareTo( BigInteger x, BigInteger y ) {
		if ( x.words == null && y.words == null )
			return x.ival < y.ival ? -1 : x.ival > y.ival ? 1 : 0;
		int x_len = x.words == null ? 1 : x.ival;
		int y_len = y.words == null ? 1 : y.ival;
		if ( x_len != y_len )
			return ( x_len > y_len ) ? 1 : -1;
		return MPN.cmp( x.words, y.words, x_len );
	}

	public int compareTo( BigInteger val ) {
		return compareTo( this, val );
	}

	private final boolean isZero() {
		return words == null && ival == 0;
	}

	private final boolean isOne() {
		return words == null && ival == 1;
	}

	/**
	 * Calculate how many words are significant in words[0:len-1]. Returns the
	 * least value x such that x>0 && words[0:x-1]==words[0:len-1], when words
	 * is viewed as a 2's complement integer.
	 */
	private static int wordsNeeded( int[] words, int len ) {
		int i = len;
		if ( i > 0 ) {
			int word = words[--i];
			if ( word == -1 ) {
				while ( i > 0 && ( word = words[i - 1] ) < 0 ) {
					i--;
					if ( word != -1 )
						break;
				}
			}
			else {
				while ( word == 0 && i > 0 && ( word = words[i - 1] ) >= 0 )
					i--;
			}
		}
		return i + 1;
	}

	private BigInteger canonicalize() {
		if ( words != null && ( ival = BigInteger.wordsNeeded( words, ival ) ) <= 1 ) {
			if ( ival == 1 )
				ival = words[0];
			words = null;
		}
		if ( words == null && ival >= minFixNum && ival <= maxFixNum )
			return smallFixNums[ival - minFixNum];
		return this;
	}

	/** Add two ints, yielding a BigInteger. */
	private static final BigInteger add( int x, int y ) {
		return valueOf( (long) x + (long) y );
	}

	/** Add a BigInteger and an int, yielding a new BigInteger. */
	private static BigInteger add( BigInteger x, int y ) {
		if ( x.words == null )
			return BigInteger.add( x.ival, y );
		BigInteger result = new BigInteger( 0 );
		result.setAdd( x, y );
		return result.canonicalize();
	}

	/**
	 * Set this to the sum of x and y. OK if x==this.
	 */
	private void setAdd( BigInteger x, int y ) {
		if ( x.words == null ) {
			set( (long) x.ival + (long) y );
			return;
		}
		int len = x.ival;
		realloc( len + 1 );
		long carry = y;
		for ( int i = 0; i < len; i++ ) {
			carry += ( (long) x.words[i] & 0xffffffffL );
			words[i] = (int) carry;
			carry >>= 32;
		}
		if ( x.words[len - 1] < 0 )
			carry--;
		words[len] = (int) carry;
		ival = wordsNeeded( words, len + 1 );
	}

	/** Destructively set the value of this to a long. */
	private final void set( long y ) {
		int i = (int) y;
		if ( (long) i == y ) {
			ival = i;
			words = null;
		}
		else {
			realloc( 2 );
			words[0] = i;
			words[1] = (int) ( y >> 32 );
			ival = 2;
		}
	}

	/**
	 * Destructively set the value of this to the given words. The words array
	 * is reused, not copied.
	 */
	private final void set( int[] words, int length ) {
		this.ival = length;
		this.words = words;
	}

	/** Destructively set the value of this to that of y. */
	private final void set( BigInteger y ) {
		if ( y.words == null )
			set( y.ival );
		else if ( this != y ) {
			realloc( y.ival );
			System.arraycopy( y.words, 0, words, 0, y.ival );
			ival = y.ival;
		}
	}

	/** Add two BigIntegers, yielding their sum as another BigInteger. */
	private static BigInteger add( BigInteger x, BigInteger y, int k ) {
		if ( x.words == null && y.words == null )
			return valueOf( (long) k * (long) y.ival + (long) x.ival );
		if ( k != 1 )
			y = BigInteger.times( y, valueOf( k ) );
		if ( x.words == null )
			return BigInteger.add( y, x.ival );
		if ( y.words == null )
			return BigInteger.add( x, y.ival );
		// Both are big
		if ( y.ival > x.ival ) { // Swap so x is longer then y.
			BigInteger tmp = x;
			x = y;
			y = tmp;
		}
		BigInteger result = alloc( x.ival + 1 );
		int i = y.ival;
		long carry = MPN.add_n( result.words, x.words, y.words, i );
		long y_ext = y.words[i - 1] < 0 ? 0xffffffffL : 0;
		for ( ; i < x.ival; i++ ) {
			carry += ( (long) x.words[i] & 0xffffffffL ) + y_ext;
			;
			result.words[i] = (int) carry;
			carry >>>= 32;
		}
		if ( x.words[i - 1] < 0 )
			y_ext--;
		result.words[i] = (int) ( carry + y_ext );
		result.ival = i + 1;
		return result.canonicalize();
	}

	private static final BigInteger times( BigInteger x, int y ) {
		if ( y == 0 )
			return ZERO;
		if ( y == 1 )
			return x;
		int[] xwords = x.words;
		int xlen = x.ival;
		if ( xwords == null )
			return valueOf( (long) xlen * (long) y );
		BigInteger result = BigInteger.alloc( xlen + 1 );
		result.words[xlen] = MPN.mul_1( result.words, xwords, xlen, y );
		result.ival = xlen + 1;
		return result.canonicalize();
	}

	private static final BigInteger times( BigInteger x, BigInteger y ) {
		if ( y.words == null )
			return times( x, y.ival );
		if ( x.words == null )
			return times( y, x.ival );
		int[] xwords;
		int[] ywords;
		int xlen = x.ival;
		int ylen = y.ival;
		xwords = x.words;
		ywords = y.words;
		// Swap if x is shorter then y.
		if ( xlen < ylen ) {
			int[] twords = xwords;
			xwords = ywords;
			ywords = twords;
			int tlen = xlen;
			xlen = ylen;
			ylen = tlen;
		}
		BigInteger result = BigInteger.alloc( xlen + ylen );
		MPN.mul( result.words, xwords, xlen, ywords, ylen );
		result.ival = xlen + ylen;
		return result.canonicalize();
	}

	private static void divide( long x, long y, BigInteger quotient, BigInteger remainder ) {
		boolean xNegative, yNegative;
		if ( x < 0 ) {
			xNegative = true;
			if ( x == Long.MIN_VALUE ) {
				divide( valueOf( x ), valueOf( y ), quotient, remainder );
				return;
			}
			x = -x;
		}
		else
			xNegative = false;

		if ( y < 0 ) {
			yNegative = true;
			if ( y == Long.MIN_VALUE ) {
				divide( valueOf( x ), valueOf( y ), quotient, remainder );
				return;
			}
			y = -y;
		}
		else
			yNegative = false;

		long q = x / y;
		long r = x % y;
		boolean qNegative = xNegative ^ yNegative;

		boolean add_one = false;
		if ( r != 0 ) {
			if ( qNegative )
				add_one = true;
		}
		if ( quotient != null ) {
			if ( add_one )
				q++;
			if ( qNegative )
				q = -q;
			quotient.set( q );
		}
		if ( remainder != null ) {
			// The remainder is by definition: X-Q*Y
			if ( add_one ) {
				// Subtract the remainder from Y.
				r = y - r;
				// In this case, abs(Q*Y) > abs(X).
				// So sign(remainder) = -sign(X).
				xNegative = !xNegative;
			}
			else {
				// If !add_one, then: abs(Q*Y) <= abs(X).
				// So sign(remainder) = sign(X).
			}
			if ( xNegative )
				r = -r;
			remainder.set( r );
		}
	}

	/**
	 * Divide two integers, yielding quotient and remainder.
	 * 
	 * @param x
	 *            the numerator in the division
	 * @param y
	 *            the denominator in the division
	 * @param quotient
	 *            is set to the quotient of the result (iff quotient!=null)
	 * @param remainder
	 *            is set to the remainder of the result (iff remainder!=null)
	 * @param rounding_mode
	 *            one of FLOOR, CEILING, TRUNCATE, or ROUND.
	 */
	private static void divide( BigInteger x, BigInteger y, BigInteger quotient, BigInteger remainder ) {
		if ( ( x.words == null || x.ival <= 2 ) && ( y.words == null || y.ival <= 2 ) ) {
			long x_l = x.longValue();
			long y_l = y.longValue();
			if ( x_l != Long.MIN_VALUE && y_l != Long.MIN_VALUE ) {
				divide( x_l, y_l, quotient, remainder );
				return;
			}
		}

		int ylen = y.words == null ? 1 : y.ival;
		int[] ywords = new int[ylen];
		y.getAbsolute( ywords );
		while ( ylen > 1 && ywords[ylen - 1] == 0 )
			ylen--;

		int xlen = x.words == null ? 1 : x.ival;
		int[] xwords = new int[xlen + 2];
		x.getAbsolute( xwords );
		while ( xlen > 1 && xwords[xlen - 1] == 0 )
			xlen--;

		int qlen, rlen;

		int cmpval = MPN.cmp( xwords, xlen, ywords, ylen );
		if ( cmpval < 0 ) // abs(x) < abs(y)
		{ // quotient = 0; remainder = num.
			int[] rwords = xwords;
			xwords = ywords;
			ywords = rwords;
			rlen = xlen;
			qlen = 1;
			xwords[0] = 0;
		}
		else if ( cmpval == 0 ) // abs(x) == abs(y)
		{
			xwords[0] = 1;
			qlen = 1; // quotient = 1
			ywords[0] = 0;
			rlen = 1; // remainder = 0;
		}
		else if ( ylen == 1 ) {
			qlen = xlen;
			// Need to leave room for a word of leading zeros if dividing by 1
			// and the dividend has the high bit set. It might be safe to
			// increment qlen in all cases, but it certainly is only necessary
			// in the following case.
			if ( ywords[0] == 1 && xwords[xlen - 1] < 0 )
				qlen++;
			rlen = 1;
			ywords[0] = MPN.divmod_1( xwords, xwords, xlen, ywords[0] );
		}
		else // abs(x) > abs(y)
		{
			// Normalize the denominator, i.e. make its most significant bit set
			// by
			// shifting it normalization_steps bits to the left. Also shift the
			// numerator the same number of steps (to keep the quotient the
			// same!).

			int nshift = MPN.count_leading_zeros( ywords[ylen - 1] );
			if ( nshift != 0 ) {
				// Shift up the denominator setting the most significant bit of
				// the most significant word.
				MPN.lshift( ywords, 0, ywords, ylen, nshift );

				// Shift up the numerator, possibly introducing a new most
				// significant word.
				int x_high = MPN.lshift( xwords, 0, xwords, xlen, nshift );
				xwords[xlen++] = x_high;
			}

			if ( xlen == ylen )
				xwords[xlen++] = 0;
			MPN.divide( xwords, xlen, ywords, ylen );
			rlen = ylen;
			MPN.rshift0( ywords, xwords, 0, rlen, nshift );

			qlen = xlen + 1 - ylen;
			if ( quotient != null ) {
				for ( int i = 0; i < qlen; i++ )
					xwords[i] = xwords[i + ylen];
			}
		}

		if ( ywords[rlen - 1] < 0 ) {
			ywords[rlen] = 0;
			rlen++;
		}

		// Now the quotient is in xwords, and the remainder is in ywords.

		if ( quotient != null )
			quotient.set( xwords, qlen );
		if ( remainder != null )
			// The remainder is by definition: X-Q*Y
			remainder.set( ywords, rlen );
	}

	public BigInteger mod( BigInteger m ) {
		BigInteger rem = new BigInteger();
		divide( this, m, null, rem );
		return rem.canonicalize();
	}

	public BigInteger modPow( BigInteger exponent, BigInteger m ) {
		if ( exponent.isOne() )
			return mod( m );

		// To do this naively by first raising this to the power of exponent
		// and then performing modulo m would be extremely expensive, especially
		// for very large numbers. The solution is found in Number Theory
		// where a combination of partial powers and moduli can be done easily.
		//
		// We'll use the algorithm for Additive Chaining which can be found on
		// p. 244 of "Applied Cryptography, Second Edition" by Bruce Schneier.
		BigInteger s = ONE;
		BigInteger t = this;
		BigInteger u = exponent;

		while ( !u.isZero() ) {
			if ( u.and( ONE ).isOne() )
				s = times( s, t ).mod( m );
			//	u = u.shiftRight(1);
			u = valueOf( u.ival >> 1 );
			t = times( t, t ).mod( m );
		}

		return s;
	}

	public long longValue() {
		if ( words == null )
			return ival;
		if ( ival == 1 )
			return words[0];
		return ( (long) words[1] << 32 ) + ( (long) words[0] & 0xffffffffL );
	}

	/**
	 * Copy the abolute value of this into an array of words. Assumes
	 * words.length >= (this.words == null ? 1 : this.ival). Result is
	 * zero-extended, but need not be a valid 2's complement number.
	 */
	private void getAbsolute( int[] words ) {
		int len;
		if ( this.words == null ) {
			len = 1;
			words[0] = this.ival;
		}
		else {
			len = this.ival;
			for ( int i = len; --i >= 0; )
				words[i] = this.words[i];
		}
		for ( int i = words.length; --i > len; )
			words[i] = 0;
	}

	/**
	 * Calculates ceiling(log2(this < 0 ? -this : this+1)) See Common Lisp: the
	 * Language, 2nd ed, p. 361.
	 */
	public int bitLength() {
		if ( words == null )
			return MPN.intLength( ival );
		return MPN.intLength( words, ival );
	}

	public byte[] toByteArray() {
		// Determine number of bytes needed. The method bitlength returns
		// the size without the sign bit, so add one bit for that and then
		// add 7 more to emulate the ceil function using integer math.
		byte[] bytes = new byte[( bitLength() + 1 + 7 ) / 8];
		int nbytes = bytes.length;

		int wptr = 0;
		int word;

		// Deal with words array until one word or less is left to process.
		// If BigInteger is an int, then it is in ival and nbytes will be <= 4.
		while ( nbytes > 4 ) {
			word = words[wptr++];
			for ( int i = 4; i > 0; --i, word >>= 8 )
				bytes[--nbytes] = (byte) word;
		}

		// Deal with the last few bytes. If BigInteger is an int, use ival.
		word = ( words == null ) ? ival : words[wptr];
		for ( ; nbytes > 0; word >>= 8 )
			bytes[--nbytes] = (byte) word;

		return bytes;
	}

	/** Return the logical (bit-wise) "and" of a BigInteger and an int. */
	private static BigInteger and( BigInteger x, int y ) {
		if ( x.words == null )
			return valueOf( x.ival & y );
		if ( y >= 0 )
			return valueOf( x.words[0] & y );
		int len = x.ival;
		int[] words = new int[len];
		words[0] = x.words[0] & y;
		while ( --len > 0 )
			words[len] = x.words[len];
		return make( words, x.ival );
	}

	/** Return the logical (bit-wise) "and" of two BigIntegers. */
	public BigInteger and( BigInteger y ) {
		if ( y.words == null )
			return and( this, y.ival );
		else if ( words == null )
			return and( y, ival );

		BigInteger x = this;
		if ( ival < y.ival ) {
			BigInteger temp = this;
			x = y;
			y = temp;
		}
		int i;
		int len = y.ival;
		int[] words = new int[len];
		for ( i = 0; i < y.ival; i++ )
			words[i] = x.words[i] & y.words[i];
		for ( ; i < len; i++ )
			words[i] = x.words[i];
		return make( words, len );
	}
}