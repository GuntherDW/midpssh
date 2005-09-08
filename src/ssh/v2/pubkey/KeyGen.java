package ssh.v2.pubkey;

import ssh.v2.BigInteger;
import ssh.v2.SHA1Digest;
import ssh.v2.SshPacket2;

public class KeyGen {
    private int                        strength = 1024;
    private int                        certainty = 20;
    private SecureRandom random = new SecureRandom();
    
    private BigInteger p, q, g, x, y;

    private static BigInteger ZERO = BigInteger.valueOf(0);
    private static BigInteger ONE = BigInteger.valueOf(1);
    private static BigInteger TWO = BigInteger.valueOf(2);
    private static final byte[] sshdss="ssh-dss".getBytes();
    
    public static void main(String[] argv) {
    	KeyGen kg = new KeyGen();
    	kg.generateKeyPair();
    	System.out.println(kg.getPublicKeyText());
    }

    public String getPublicKeyText() {
        byte[] pubblob=getPublicKeyBlob();
        byte[] pub=toBase64(pubblob, 0, pubblob.length);
        return new String(pub);
    }
    
    public byte[] getPublicKeyBlob(){
        SshPacket2 buf = new SshPacket2(null);
        buf.putString(sshdss);
        buf.putMpInt(p.toByteArray());
        buf.putMpInt(q.toByteArray());
        buf.putMpInt(g.toByteArray());
        buf.putMpInt(y.toByteArray());
        return buf.getData();
      }
    
    public void generateKeyPair()
    {
        System.out.println("GENERATE 1");
        generateParameters();
        System.out.println("GENERATE 2");
        
        do
        {
            x = new BigInteger(160, random);
        }
        while (x.equals(ZERO)  || x.compareTo(q) >= 0);
        System.out.println("GENERATE 3");

        //
        // calculate the public key.
        //
        y = g.modPow(x, p);
        System.out.println("GENERATE 4");
    }
    


    /**
     * add value to b, returning the result in a. The a value is treated
     * as a BigInteger of length (a.length * 8) bits. The result is
     * modulo 2^a.length in case of overflow.
     */
    private void add(
        byte[]  a,
        byte[]  b,
        int     value)
    {
        int     x = (b[b.length - 1] & 0xff) + value;

        a[b.length - 1] = (byte)x;
        x >>>= 8;

        for (int i = b.length - 2; i >= 0; i--)
        {
            x += (b[i] & 0xff);
            a[i] = (byte)x;
            x >>>= 8;
        }
    }

    /**
     * which generates the p and g values from the given parameters,
     * returning the DSAParameters object.
     * <p>
     * Note: can take a while...
     */
    public void generateParameters()
    {
        byte[]          seed = new byte[20];
        byte[]          part1 = new byte[20];
        byte[]          part2 = new byte[20];
        byte[]          u = new byte[20];
        SHA1Digest      sha1 = new SHA1Digest();
        int             n = (strength - 1) / 160;
        byte[]          w = new byte[strength / 8];

        BigInteger      q = null, p = null, g = null;
        int             counter = 0;
        boolean         primesFound = false;

        while (!primesFound)
        {
            do
            {
                random.nextBytes(seed);

                sha1.update(seed, 0, seed.length);

                sha1.doFinal(part1, 0);

                System.arraycopy(seed, 0, part2, 0, seed.length);

                add(part2, seed, 1);

                sha1.update(part2, 0, part2.length);

                sha1.doFinal(part2, 0);

                for (int i = 0; i != u.length; i++)
                {
                    u[i] = (byte)(part1[i] ^ part2[i]);
                }

                u[0] |= (byte)0x80;
                u[19] |= (byte)0x01;

                q = new BigInteger(1, u);
            }
            while (!q.isProbablePrime(certainty));

            counter = 0;

            int offset = 2;

            while (counter < 4096)
            {
                for (int k = 0; k < n; k++)
                {
                    add(part1, seed, offset + k);
                    sha1.update(part1, 0, part1.length);
                    sha1.doFinal(part1, 0);
                    System.arraycopy(part1, 0, w, w.length - (k + 1) * part1.length, part1.length);
                }

                add(part1, seed, offset + n);
                sha1.update(part1, 0, part1.length);
                sha1.doFinal(part1, 0);
                System.arraycopy(part1, part1.length - ((w.length - (n) * part1.length)), w, 0, w.length - n * part1.length);

                w[0] |= (byte)0x80;

                BigInteger  x = new BigInteger(1, w);

                BigInteger  c = x.mod(q.multiply(TWO));

                p = x.subtract(c.subtract(ONE));

                if (p.testBit(strength - 1))
                {
                    if (p.isProbablePrime(certainty))
                    {
                        primesFound = true;
                        break;
                    }
                }

                counter += 1;
                offset += n + 1;
            }
        }

        //
        // calculate the generator g
        //
        BigInteger  pMinusOneOverQ = p.subtract(ONE).divide(q);

        for (;;)
        {
            BigInteger h = new BigInteger(strength, random);
            
            if (h.compareTo(ONE) <= 0 || h.compareTo(p.subtract(ONE)) >= 0)
            {
                continue;
            }

            g = h.modPow(pMinusOneOverQ, p);
            if (g.compareTo(ONE) <= 0)
            {
                continue;
            }

            break;
        }

        this.p = p;
        this.q = q;
        this.g = g;
    }
    
    private static final byte[] b64 ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".getBytes();
    
    static byte[] toBase64(byte[] buf, int start, int length){

        byte[] tmp=new byte[length*2];
        int i,j,k;
        
        int foo=(length/3)*3+start;
        i=0;
        for(j=start; j<foo; j+=3){
          k=(buf[j]>>>2)&0x3f;
          tmp[i++]=b64[k];
          k=(buf[j]&0x03)<<4|(buf[j+1]>>>4)&0x0f;
          tmp[i++]=b64[k];
          k=(buf[j+1]&0x0f)<<2|(buf[j+2]>>>6)&0x03;
          tmp[i++]=b64[k];
          k=buf[j+2]&0x3f;
          tmp[i++]=b64[k];
        }

        foo=(start+length)-foo;
        if(foo==1){
          k=(buf[j]>>>2)&0x3f;
          tmp[i++]=b64[k];
          k=((buf[j]&0x03)<<4)&0x3f;
          tmp[i++]=b64[k];
          tmp[i++]=(byte)'=';
          tmp[i++]=(byte)'=';
        }
        else if(foo==2){
          k=(buf[j]>>>2)&0x3f;
          tmp[i++]=b64[k];
          k=(buf[j]&0x03)<<4|(buf[j+1]>>>4)&0x0f;
          tmp[i++]=b64[k];
          k=((buf[j+1]&0x0f)<<2)&0x3f;
          tmp[i++]=b64[k];
          tmp[i++]=(byte)'=';
        }
        byte[] bar=new byte[i];
        System.arraycopy(tmp, 0, bar, 0, i);
        return bar;

//        return sun.misc.BASE64Encoder().encode(buf);
      }
}
