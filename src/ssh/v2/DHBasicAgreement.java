/* This file is part of "MidpSSH".
 * 
 * This file was adapted from Bouncy Castle JCE (www.bouncycastle.org)
 * for MidpSSH by Karl von Randow
 */
package ssh.v2;

/**
 * a Diffie-Hellman key agreement class.
 * <p>
 * note: This is only the basic algorithm, it doesn't take advantage of long
 * term public keys if they are available. See the DHAgreement class for a
 * "better" implementation.
 */
public class DHBasicAgreement {
	private BigInteger x;

	private BigInteger p;

	public void init(BigInteger x, BigInteger p) {
		this.x = x;
		this.p = p;
	}

	/**
	 * given a short term public key from a given party calculate the next
	 * message in the agreement sequence.
	 */
	public BigInteger calculateAgreement(BigInteger y) {
		return y.modPow(x, p);
	}
}