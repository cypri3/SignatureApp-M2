package src2;

import java.math.BigInteger;

public interface Signature {
    void keyGen(int keySize);

    boolean verify(byte[] signature, byte[] message, BigInteger[] publicKey);

}