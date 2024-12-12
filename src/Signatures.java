import java.math.BigInteger;

public interface Signatures {
    void keyGen(int keySize);

    boolean verify(byte[] signature, byte[] message, BigInteger[] publicKey);

    byte[] sign(byte[] hash, BigInteger[] keys);

}