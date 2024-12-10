package src2;

import java.math.BigInteger;

public class Projet {
    private char mode;
    private byte[] message;
    private byte[] signature;
    private int keySize;
    private BigInteger publicKey;
    private BigInteger privateKey;

    public Projet(char mode, byte[] message, int keySize) {
        this.mode = mode;
        this.message = message;
        this.keySize = keySize;
    }

    public void generateKeys(Signature signatureAlgorithm) {
        signatureAlgorithm.keyGen(this.keySize);
        System.out.println("Keys generated for mode: " + mode);
    }

    public void signMessage(Signature signatureAlgorithm, Hash hashFunction) {
        this.signature = signatureAlgorithm.sign(this.message, this.privateKey, hashFunction);
        System.out.println("Message signed.");
    }

    public boolean verifyMessage(Signature signatureAlgorithm, Hash hashFunction) {
        return signatureAlgorithm.verify(this.signature, this.message, this.publicKey);
    }
}
