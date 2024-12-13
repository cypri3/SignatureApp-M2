import java.math.BigInteger;

public class BLS implements Signatures {
    @Override
    public BigInteger[] keyGen() {
        return new BigInteger[2];
    }

    @Override
    public byte[] sign(byte[] hash, BigInteger[] privateKey) {
        return new byte[0];
    }

    @Override
    public boolean verify(byte[] signature, byte[] hash, BigInteger[] publicKey) {
        return false;
    }
}