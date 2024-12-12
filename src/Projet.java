import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

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

    public void generateKeys(Signature2 signatureAlgorithm) {
        signatureAlgorithm.keyGen(this.keySize);
        System.out.println("Keys generated for mode: " + mode);
    }

    public void signMessage(Signature2 signatureAlgorithm, Hashs hashFunction) {
        this.signature = signatureAlgorithm.sign(this.message, this.privateKey, hashFunction);
        System.out.println("Message signed.");
    }

    public boolean verifyMessage(Signature2 signatureAlgorithm, Hashs hashFunction) {
        return signatureAlgorithm.verify(this.signature, this.message, this.publicKey);
    }

    public static void main(String[] args) {
        System.out.println("Hello, PDF!");

    }
}
