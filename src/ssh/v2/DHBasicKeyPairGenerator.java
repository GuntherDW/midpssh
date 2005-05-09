/* This file is part of "MidpSSH".
 * 
 * This file was adapted from Bouncy Castle JCE (www.bouncycastle.org)
 * for MidpSSH by Karl von Randow
 */
package ssh.v2;

import java.util.Random;

/**
 * a basic Diffie-Helman key pair generator.
 * 
 * This generates keys consistent for use with the basic algorithm for
 * Diffie-Helman.
 */
public class DHBasicKeyPairGenerator {

	private BigInteger x, y;

	public void generateKeyPair(Random random, BigInteger p, BigInteger g) {
		int qLength = p.bitLength() - 1 - 1;

		// Use a smaller qLength so that's it's quicker to generate
		qLength = 32;

		//System.out.println( "Generating private key" );
		//
		// calculate the private key
		//
		this.x = new BigInteger(qLength, random);

		//System.out.println( "PRIVATE KEY=" + this.x );
		//System.out.println( "Generating public key" );
		//
		// calculate the public key.
		//
		this.y = g.modPow(x, p);
		//System.out.println( "PUBLIC KEY=" + this.y );
		//System.out.println( "Generated both keys" );
	}

	/**
	 * @return Returns the x.
	 */
	public BigInteger getPrivate() {
		return x;
	}

	/**
	 * @return Returns the y.
	 */
	public BigInteger getPublic() {
		return y;
	}
}