/* -*-mode:java; c-basic-offset:2; -*- */
/*
 Copyright (c) 2002,2003,2004 ymnk, JCraft,Inc. All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice,
 this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright 
 notice, this list of conditions and the following disclaimer in 
 the documentation and/or other materials provided with the distribution.

 3. The names of the authors may not be used to endorse or promote products
 derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JCRAFT,
 INC. OR ANY CONTRIBUTORS TO THIS SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package ssh.v2;

import java.util.Random;

public class DH {
	BigInteger p;

	BigInteger g;

	BigInteger e; // my public key

	byte[] e_array;

	BigInteger f; // your public key

	BigInteger K; // shared secret key

	byte[] K_array;

	private DHBasicKeyPairGenerator myKpairGen;

	private DHBasicAgreement myKeyAgree;
	
	private static Random random = new Random();

	public DH() {
		myKpairGen = new DHBasicKeyPairGenerator();
		myKeyAgree = new DHBasicAgreement();
	}

	public BigInteger getE() {
		if ( e == null ) {
			myKpairGen.generateKeyPair( random, p, g );
			myKeyAgree.init( myKpairGen.getPrivate(), p );
			e = myKpairGen.getPublic();
			//e_array = e.toByteArray();
		}
		return e;
	}

	public BigInteger getK() {
		if ( K == null ) {
			K = myKeyAgree.calculateAgreement( f );
			//K_array = K.toByteArray();
		}
		return K;
	}

	public void setP( byte[] p ) {
		setP( new BigInteger( p ) );
	}

	public void setG( byte[] g ) {
		setG( new BigInteger( g ) );
	}

	public void setF( byte[] f ) {
		setF( new BigInteger( f ) );
	}

	void setP( BigInteger p ) {
		this.p = p;
	}

	void setG( BigInteger g ) {
		this.g = g;
	}

	public void setF( BigInteger f ) {
		this.f = f;
	}
}