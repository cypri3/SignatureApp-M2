import java.math.BigInteger;

public interface Signatures {
    public BigInteger[] keyGen();

    public byte[] sign(byte[] hash, BigInteger[] keys);

    public boolean verify(byte[] signature, byte[] hash, BigInteger[] publicKey);
}