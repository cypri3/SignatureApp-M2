package src2;

import java.math.BigInteger;

public class DSA implements Signature {
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