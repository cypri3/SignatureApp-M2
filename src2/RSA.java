import java.math.BigInteger;

public class RSA implements Signature {
    @Override
    public void keyGen(int keySize) {
    }

    @Override
    public byte[] sign(byte[] message, BigInteger privateKey, Hash function) {
    }

    @Override
    public boolean verify(byte[] signature, byte[] message, BigInteger publicKey) {
    }
}