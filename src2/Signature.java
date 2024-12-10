package src2;

import java.math.BigInteger;

public interface Signature {
    void keyGen(int keySize);

    byte[] sign(byte[] message, BigInteger privateKey, Hash function);

    boolean verify(byte[] signature, byte[] message, BigInteger publicKey);
}