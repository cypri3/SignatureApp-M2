import java.math.BigInteger;

public class BLS implements Signatures {
    @Override
    public void keyGen(int keySize) {
    }

    @Override
    public byte[] sign(byte[] message, BigInteger privateKey, Hashs function) {
    }

    @Override
    public boolean verify(byte[] signature, byte[] message, BigInteger publicKey) {
    }
}