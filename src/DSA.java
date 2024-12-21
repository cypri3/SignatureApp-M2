import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class DSA implements Signatures {

    static final BigInteger l = new BigInteger("2").pow(160).add(new BigInteger("7"));
    static final BigInteger p = new BigInteger("1").add(l.multiply(new BigInteger("2").pow(864).add(new BigInteger("218"))));
    static final BigInteger g = new BigInteger("2").modPow(p.subtract(BigInteger.ONE).divide(l), p);


    @Override
    public BigInteger[] keyGen() {
        // Keys generation function
        Random rdn = new Random();
        BigInteger privateKey = new BigInteger(160, rdn);
        BigInteger publicKey = g.modPow(privateKey, p);
        BigInteger[] keys = { privateKey, publicKey };
        return keys;
    }

    @Override
    public byte[] sign(byte[] hash, BigInteger[] keys) {
        // Sign function
        Random rdnrdn = new Random();
        BigInteger k = new BigInteger(159, rdnrdn).mod(l);
        BigInteger h = new BigInteger(hash);
        BigInteger r = (g.modPow(k, p)).mod(l);
        BigInteger s = k.modInverse(l).multiply(h.add(keys[0].multiply(r))).mod(l);
        ByteArrayOutputStream si = new ByteArrayOutputStream();
        try {
            si.write(k.toByteArray());
            si.write(s.toByteArray());
        } catch (IOException ex) {
        }
        byte[] signature = si.toByteArray();
        return signature;
    }

    @Override
    public boolean verify(byte[] signature, byte[] message, BigInteger[] publicKey) {
        // Verification function
        byte[] rand = new byte[20];
        System.arraycopy(signature, 0, rand, 0, 20);
        BigInteger K = new BigInteger(rand);
        BigInteger r = (g.modPow(K, p)).mod(l);
        int len = signature.length;
        byte[] sign = new byte[len - 20];
        System.arraycopy(signature, 20, sign, 0, (len - 20));
        BigInteger s = new BigInteger(sign);
        if (r.compareTo(l) == 1 && s.compareTo(l) == 1) {
            return false;
        } else {
            BigInteger m = new BigInteger(message);
            BigInteger t = s.modInverse(l);
            BigInteger u = (t.multiply(m)).mod(l);
            BigInteger v = (t.multiply(r)).mod(l);
            BigInteger R = ((g.modPow(u, p)).multiply(publicKey[0].modPow(v, p))).mod(p).mod(l);
            return R.equals(r);
        }
    }
}