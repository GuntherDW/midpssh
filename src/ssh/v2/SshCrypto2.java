/*
 * Created on Oct 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ssh.v2;

import ssh.v1.SshCrypto;


/**
 * @author Karl
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SshCrypto2 extends SshCrypto {
	private BufferedDESedeCBC sndCipher, rcvCipher;
	
	private HMACSHA1 sndHmac, rcvHmac;

	public SshCrypto2( byte [] IVc2s, byte [] IVs2c, byte [] Ec2s, byte [] Es2c, byte [] MACc2s, byte [] MACs2c ) {
		sndCipher = new BufferedDESedeCBC();
		sndCipher.init( true, IVc2s, Ec2s );
		rcvCipher = new BufferedDESedeCBC();
		rcvCipher.init( false, IVs2c, Es2c );
		
		sndHmac = new HMACSHA1();
		sndHmac.init( MACc2s );
		
		rcvHmac = new HMACSHA1();
		rcvHmac.init( MACs2c );
	}
	
	public byte[] encrypt( byte[] src ) {
		byte[] dest = new byte[src.length];
		sndCipher.processBytes( src, 0, src.length, dest, 0 );
		return dest;
	}

	public byte[] decrypt( byte[] src ) {
		byte[] dest = new byte[src.length];
		rcvCipher.processBytes( src, 0, src.length, dest, 0 );
		return dest;
	}
	
	
	/**
	 * @return Returns the rcvHmac.
	 */
	public HMACSHA1 getRcvHmac() {
		return rcvHmac;
	}
	/**
	 * @return Returns the sndHmac.
	 */
	public HMACSHA1 getSndHmac() {
		return sndHmac;
	}
}
