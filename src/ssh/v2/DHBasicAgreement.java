package ssh.v2;

/**
 * a Diffie-Hellman key agreement class.
 * <p>
 * note: This is only the basic algorithm, it doesn't take advantage of
 * long term public keys if they are available. See the DHAgreement class
 * for a "better" implementation.
 */
public class DHBasicAgreement
{
    private BigInteger x;
    private BigInteger p;
    
    public void init(
        BigInteger x, BigInteger p )
    {
    	this.x = x;
    	this.p = p;
    }

    /**
     * given a short term public key from a given party calculate the next
     * message in the agreement sequence. 
     */
    public BigInteger calculateAgreement(
        BigInteger   y)
    {
    	//System.out.println( "CALC AGREEMENT y=" + y + " x=" + x + " p=" + p );
        return y.modPow(x, p);
    }
}
