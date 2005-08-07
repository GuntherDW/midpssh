/* This file is part of "MidpSSH".
 * 
 * This file was adapted from Bouncy Castle JCE (www.bouncycastle.org)
 * for MidpSSH by Karl von Randow
 */
package ssh.v2;

/**
 * A wrapper class that allows block ciphers to be used to process data in a
 * piecemeal fashion. The BufferedBlockCipher outputs a block only when the
 * buffer is full and more data is being added, or on a doFinal.
 * <p>
 * Note: in the case where the underlying cipher is either a CFB cipher or an
 * OFB one the last block may not be a multiple of the block size.
 */
public class BufferedDESedeCBC {
	protected byte[] buf;

	protected int bufOff;

	protected boolean partialBlockOkay;

	protected boolean pgpCFB;
	
	private byte[] IV;

	private byte[] cbcV;

	private byte[] cbcNextV;

	private int blockSize;

	private DESedeEngine cipher = null;

	private boolean encrypting;

	/**
	 * Create a buffered block cipher without padding.
	 * 
	 * @param cipher
	 *            the underlying block cipher this buffering object wraps.
	 */
	public BufferedDESedeCBC() {
		this.cipher = new DESedeEngine();
		this.blockSize = cipher.getBlockSize();

		this.IV = new byte[blockSize];
		this.cbcV = new byte[blockSize];
		this.cbcNextV = new byte[blockSize];

		buf = new byte[cipher.getBlockSize()];
		bufOff = 0;

		//
		// check if we can handle partial blocks on doFinal.
		//
		String name = cipher.getAlgorithmName();
		int idx = name.indexOf('/') + 1;

		pgpCFB = (idx > 0 && name.startsWith("PGP", idx));

		if (pgpCFB) {
			partialBlockOkay = true;
		} else {
			partialBlockOkay = (idx > 0 && (name.startsWith("CFB", idx)
					|| name.startsWith("OFB", idx) || name.startsWith(
					"OpenPGP", idx)));
		}
	}

	/**
	 * initialise the cipher.
	 * 
	 * @param forEncryption
	 *            if true the cipher is initialised for encryption, if false for
	 *            decryption.
	 * @param params
	 *            the key and other data required by the cipher.
	 * @exception IllegalArgumentException
	 *                if the params argument is inappropriate.
	 */
	public void init(boolean encrypting, byte[] iv, byte[] key)
			throws IllegalArgumentException {
		reset();

		this.encrypting = encrypting;
		System.arraycopy(iv, 0, IV, 0, blockSize);

		reset();

		cipher.init(encrypting, key);
	}

	/**
	 * return the blocksize for the underlying cipher.
	 * 
	 * @return the blocksize for the underlying cipher.
	 */
	public int getBlockSize() {
		return cipher.getBlockSize();
	}

	/**
	 * return the size of the output buffer required for an update an input of
	 * len bytes.
	 * 
	 * @param len
	 *            the length of the input.
	 * @return the space required to accommodate a call to update with len bytes
	 *         of input.
	 */
	public int getUpdateOutputSize(int len) {
		int total = len + bufOff;
		int leftOver;

		if (pgpCFB) {
			leftOver = total % buf.length - (cipher.getBlockSize() + 2);
		} else {
			leftOver = total % buf.length;
		}

		return total - leftOver;
	}

	/**
	 * return the size of the output buffer required for an update plus a
	 * doFinal with an input of len bytes.
	 * 
	 * @param len
	 *            the length of the input.
	 * @return the space required to accommodate a call to update and doFinal
	 *         with len bytes of input.
	 */
	public int getOutputSize(int len) {
		int total = len + bufOff;
		int leftOver;

		if (pgpCFB) {
			leftOver = total % buf.length - (cipher.getBlockSize() + 2);
		} else {
			leftOver = total % buf.length;
			if (leftOver == 0) {
				return total;
			}
		}

		return total - leftOver + buf.length;
	}

	/**
	 * process a single byte, producing an output block if neccessary.
	 * 
	 * @param in
	 *            the input byte.
	 * @param out
	 *            the space for any output that might be produced.
	 * @param outOff
	 *            the offset from which the output will be copied.
	 * @return the number of output bytes copied to out.
	 * @exception DataLengthException
	 *                if there isn't enough space in out.
	 * @exception IllegalStateException
	 *                if the cipher isn't initialised.
	 */
	public int processByte(byte in, byte[] out, int outOff)
			throws IllegalStateException {
		int resultLen = 0;

		buf[bufOff++] = in;

		if (bufOff == buf.length) {
			resultLen = processBlock(buf, 0, out, outOff);
			bufOff = 0;
		}

		return resultLen;
	}

	/**
	 * process an array of bytes, producing output if necessary.
	 * 
	 * @param in
	 *            the input byte array.
	 * @param inOff
	 *            the offset at which the input data starts.
	 * @param len
	 *            the number of bytes to be copied out of the input array.
	 * @param out
	 *            the space for any output that might be produced.
	 * @param outOff
	 *            the offset from which the output will be copied.
	 * @return the number of output bytes copied to out.
	 * @exception DataLengthException
	 *                if there isn't enough space in out.
	 * @exception IllegalStateException
	 *                if the cipher isn't initialised.
	 */
	public int processBytes(byte[] in, int inOff, int len, byte[] out,
			int outOff) throws IllegalStateException {
		if (len < 0) {
			throw new IllegalArgumentException(
					"Can't have a negative input length!");
		}

		int blockSize = getBlockSize();
		int length = getUpdateOutputSize(len);

		if (length > 0) {
			if ((outOff + length) > out.length) {
				throw new IllegalStateException("output buffer too short");
			}
		}

		int resultLen = 0;
		int gapLen = buf.length - bufOff;

		if (len > gapLen) {
			System.arraycopy(in, inOff, buf, bufOff, gapLen);

			resultLen += processBlock(buf, 0, out, outOff);

			bufOff = 0;
			len -= gapLen;
			inOff += gapLen;

			while (len > buf.length) {
				resultLen += processBlock(in, inOff, out, outOff
						+ resultLen);

				len -= blockSize;
				inOff += blockSize;
			}
		}

		System.arraycopy(in, inOff, buf, bufOff, len);

		bufOff += len;

		if (bufOff == buf.length) {
			resultLen += processBlock(buf, 0, out, outOff + resultLen);
			bufOff = 0;
		}

		return resultLen;
	}

	/**
	 * Process the last block in the buffer.
	 * 
	 * @param out
	 *            the array the block currently being held is copied into.
	 * @param outOff
	 *            the offset at which the copying starts.
	 * @return the number of output bytes copied to out.
	 * @exception DataLengthException
	 *                if there is insufficient space in out for the output, or
	 *                the input is not block size aligned and should be.
	 * @exception IllegalStateException
	 *                if the underlying cipher is not initialised.
	 * @exception InvalidCipherTextException
	 *                if padding is expected and not found.
	 * @exception DataLengthException
	 *                if the input is not block size aligned.
	 */
	public int doFinal(byte[] out, int outOff) throws IllegalStateException {
		int resultLen = 0;

		if (outOff + bufOff > out.length) {
			throw new IllegalStateException(
					"output buffer too short for doFinal()");
		}

		if (bufOff != 0 && partialBlockOkay) {
			processBlock(buf, 0, buf, 0);
			resultLen = bufOff;
			bufOff = 0;
			System.arraycopy(buf, 0, out, outOff, resultLen);
		} else if (bufOff != 0) {
			throw new IllegalStateException("data not block size aligned");
		}

		reset();

		return resultLen;
	}

	/**
	 * Reset the buffer and cipher. After resetting the object is in the same
	 * state as it was after the last init (if there was one).
	 */
	public void reset() {
		//
		// clean the buffer.
		//
		for (int i = 0; i < buf.length; i++) {
			buf[i] = 0;
		}

		bufOff = 0;

		//
		// reset the underlying cipher.
		//
		System.arraycopy(IV, 0, cbcV, 0, IV.length);

		cipher.reset();
	}

	/**
	 * Process one block of input from the array in and write it to the out
	 * array.
	 * 
	 * @param in
	 *            the array containing the input data.
	 * @param inOff
	 *            offset into the in array the data starts at.
	 * @param out
	 *            the array the output data will be copied into.
	 * @param outOff
	 *            the offset into the out array the output will start at.
	 * @exception DataLengthException
	 *                if there isn't enough data in in, or space in out.
	 * @exception IllegalStateException
	 *                if the cipher isn't initialised.
	 * @return the number of bytes processed and produced.
	 */
	public int processBlock(byte[] in, int inOff, byte[] out, int outOff)
			throws IllegalStateException {
		return (encrypting) ? encryptBlock(in, inOff, out, outOff)
				: decryptBlock(in, inOff, out, outOff);
	}

	/**
	 * Do the appropriate chaining step for CBC mode encryption.
	 * 
	 * @param in
	 *            the array containing the data to be encrypted.
	 * @param inOff
	 *            offset into the in array the data starts at.
	 * @param out
	 *            the array the encrypted data will be copied into.
	 * @param outOff
	 *            the offset into the out array the output will start at.
	 * @exception DataLengthException
	 *                if there isn't enough data in in, or space in out.
	 * @exception IllegalStateException
	 *                if the cipher isn't initialised.
	 * @return the number of bytes processed and produced.
	 */
	private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff)
			throws IllegalStateException {
		if ((inOff + blockSize) > in.length) {
			throw new IllegalStateException("input buffer too short");
		}

		/*
		 * XOR the cbcV and the input, then encrypt the cbcV
		 */
		for (int i = 0; i < blockSize; i++) {
			cbcV[i] ^= in[inOff + i];
		}

		int length = cipher.processBlock(cbcV, 0, out, outOff);

		/*
		 * copy ciphertext to cbcV
		 */
		System.arraycopy(out, outOff, cbcV, 0, cbcV.length);

		return length;
	}

	/**
	 * Do the appropriate chaining step for CBC mode decryption.
	 * 
	 * @param in
	 *            the array containing the data to be decrypted.
	 * @param inOff
	 *            offset into the in array the data starts at.
	 * @param out
	 *            the array the decrypted data will be copied into.
	 * @param outOff
	 *            the offset into the out array the output will start at.
	 * @exception DataLengthException
	 *                if there isn't enough data in in, or space in out.
	 * @exception IllegalStateException
	 *                if the cipher isn't initialised.
	 * @return the number of bytes processed and produced.
	 */
	private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff)
			throws IllegalStateException {
		if ((inOff + blockSize) > in.length) {
			throw new IllegalStateException("input buffer too short");
		}

		System.arraycopy(in, inOff, cbcNextV, 0, blockSize);

		int length = cipher.processBlock(in, inOff, out, outOff);

		/*
		 * XOR the cbcV and the output
		 */
		for (int i = 0; i < blockSize; i++) {
			out[outOff + i] ^= cbcV[i];
		}

		/*
		 * swap the back up buffer into next position
		 */
		byte[] tmp;

		tmp = cbcV;
		cbcV = cbcNextV;
		cbcNextV = tmp;

		return length;
	}
}