package ssh.v2;




/**
 * The Digital Signature Algorithm - as described in "Handbook of Applied
 * Cryptography", pages 452 - 453.
 */
public class DSASigner
{
    private BigInteger x, y, p, q, g;

    public boolean verifySignature( byte [] message, byte [] sig ) {
      	SshPacket2 buf = new SshPacket2( null );
      	buf.putBytes( sig );
      	byte [] alg = buf.getByteString();
      	byte [] blob = buf.getByteString();
      	
      	int rslen = blob.length / 2;
        byte [] tmp = new byte[rslen];
        tmp[0] = 0;
    	System.arraycopy( blob, 0, tmp, 0, rslen );
    	BigInteger r = new BigInteger( 1, tmp );
    	System.arraycopy( blob, rslen, tmp, 0, rslen );
    	BigInteger s = new BigInteger( 1, tmp );
    	
    	return verifySignature( message, r, s );
    }
    /**
     * return true if the value r and s represent a DSA signature for
     * the passed in message for standard DSA the message should be a
     * SHA-1 hash of the real message to be verified.
     */
    public boolean verifySignature(
        byte[]      message,
        BigInteger  r,
        BigInteger  s)
    {
    	SHA1Digest digest = new SHA1Digest();
    	digest.update( message, 0, message.length );
    	byte [] hash = new byte[ digest.getDigestSize() ];
    	digest.doFinal( hash, 0 );
    	message = hash;
    	
        BigInteger      m = new BigInteger(1, message);
        BigInteger      zero = BigInteger.valueOf(0);

        if (zero.compareTo(r) >= 0 || q.compareTo(r) <= 0)
        {
            return false;
        }

        if (zero.compareTo(s) >= 0 || q.compareTo(s) <= 0)
        {
            return false;
        }

        BigInteger  w = s.modInverse(q);

        BigInteger  u1 = m.multiply(w).mod(q);
        BigInteger  u2 = r.multiply(w).mod(q);

        u1 = g.modPow(u1, p);
        u2 = y.modPow(u2, p);

        BigInteger  v = u1.multiply(u2).mod(p).mod(q);

        return v.equals(r);
    }
	/**
	 * @param x The x to set.
	 */
	public void setX( BigInteger x ) {
		this.x = x;
	}
	/**
	 * @param y The y to set.
	 */
	public void setY( BigInteger y ) {
		this.y = y;
	}
	/**
	 * @param g The g to set.
	 */
	public void setG( BigInteger g ) {
		this.g = g;
	}
	/**
	 * @param p The p to set.
	 */
	public void setP( BigInteger p ) {
		this.p = p;
	}
	/**
	 * @param q The q to set.
	 */
	public void setQ( BigInteger q ) {
		this.q = q;
	}
}
