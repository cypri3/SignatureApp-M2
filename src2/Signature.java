package src2;

import java.math.BigInteger;

public interface Signature {
    BigInteger[] keyGen();

    byte[] sign(byte[] hash, BigInteger[] keys);

    boolean verify(byte[] signature, byte[] message, BigInteger[] publicKey);

}