import java.math.BigInteger;
import java.util.Random;
public class RSA implements Signatures {
    @Override
    public BigInteger[] keyGen(){
        BigInteger p = new BigInteger(2 ^ 50, 25, new Random());
        BigInteger q = new BigInteger(2 ^ 50, 25, new Random());
        BigInteger phi = (p.add(BigInteger.valueOf(-1))).multiply(q.add(BigInteger.valueOf(-1)));
        BigInteger e = BigInteger.valueOf(65535);

        while (!(e.gcd(phi).equals(BigInteger.ONE))) {
            p = new BigInteger(2 ^ 50, 25, new Random());
            q = new BigInteger(2 ^ 50, 25, new Random());
            phi = (p.add(BigInteger.valueOf(-1))).multiply(q.add(BigInteger.valueOf(-1)));
        }
        BigInteger N = p.multiply(q);
        BigInteger[] keys = { p, q, e, N };
        return keys;
    }

    @Override
    public byte[] sign(byte[] hash, BigInteger[] keys){
        BigInteger h = new BigInteger(hash);
        BigInteger phi = (keys[0].add(BigInteger.valueOf(-1))).multiply(keys[1].add(BigInteger.valueOf(-1)));
        BigInteger d = keys[2].modInverse(phi);
        BigInteger s = h.modPow(d, keys[3]);
        byte[] signature = s.toByteArray();
        return signature;
    }

    @Override
    public boolean verify(byte[] signature, byte[] message, BigInteger[] publicKey){
        BigInteger s = new BigInteger(signature);
        BigInteger verif = s.modPow(publicKey[0], publicKey[1]);
        BigInteger m = new BigInteger(message);
        return verif.equals(m);
    }
}