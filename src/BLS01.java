import it.unisa.dia.gas.crypto.jpbc.signature.bls01.engines.BLS01Signer;
import it.unisa.dia.gas.crypto.jpbc.signature.bls01.generators.BLS01KeyPairGenerator;
import it.unisa.dia.gas.crypto.jpbc.signature.bls01.generators.BLS01ParametersGenerator;
import it.unisa.dia.gas.crypto.jpbc.signature.bls01.params.BLS01KeyGenerationParameters;
import it.unisa.dia.gas.crypto.jpbc.signature.bls01.params.BLS01Parameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA256Digest;

import it.unisa.dia.gas.crypto.jpbc.signature.bls01.params.BLS01PrivateKeyParameters;
import it.unisa.dia.gas.crypto.jpbc.signature.bls01.params.BLS01PublicKeyParameters;
import it.unisa.dia.gas.jpbc.Element;
import java.math.BigInteger;
import it.unisa.dia.gas.jpbc.Pairing;

public class BLS01 {

    public BLS01() {
    }

    public AsymmetricCipherKeyPair keyGen() {

        BLS01ParametersGenerator setup = new BLS01ParametersGenerator();
        setup.init(PairingFactory.getPairingParameters("src/a.properties"));

        BLS01KeyPairGenerator keyGen = new BLS01KeyPairGenerator();
        keyGen.init(new BLS01KeyGenerationParameters(null, setup.generateParameters()));

        return keyGen.generateKeyPair();
    }

    public byte[] sign(String message, CipherParameters privateKey) {
        byte[] bytes = message.getBytes();

        BLS01Signer signer = new BLS01Signer(new SHA256Digest());
        signer.init(true, privateKey);
        signer.update(bytes, 0, bytes.length);

        byte[] signature = null;
        try {
            signature = signer.generateSignature();
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }
        return signature;
    }

    public boolean verify(byte[] signature, String message, CipherParameters publicKey) {
        byte[] bytes = message.getBytes();

        BLS01Signer signer = new BLS01Signer(new SHA256Digest());
        signer.init(false, publicKey);
        signer.update(bytes, 0, bytes.length);

        return signer.verifySignature(signature);
    }

    public BigInteger extractPrivateKey(CipherParameters privateKey) {
        if (!(privateKey instanceof BLS01PrivateKeyParameters)) {
            throw new IllegalArgumentException("Invalid private key format");
        }
        BLS01PrivateKeyParameters priv = (BLS01PrivateKeyParameters) privateKey;
        return priv.getSk().toBigInteger();
    }

    public byte[] extractPublicKey(CipherParameters publicKey) {
        if (!(publicKey instanceof BLS01PublicKeyParameters)) {
            throw new IllegalArgumentException("Invalid public key format");
        }
        BLS01PublicKeyParameters pub = (BLS01PublicKeyParameters) publicKey;
        Element g2Element = pub.getPk();
        return g2Element.toBytes(); 
    }
    

    public CipherParameters createPrivateKey(BigInteger sk, BLS01Parameters parameters) {
        Pairing pairing = PairingFactory.getPairing(parameters.getParameters()); 
        return new BLS01PrivateKeyParameters(parameters, pairing.getZr().newElement(sk));
    }
    
    public CipherParameters createPublicKey(byte[] pkBytes, BLS01Parameters parameters) {
        Pairing pairing = PairingFactory.getPairing(parameters.getParameters()); 
        Element g2Element = pairing.getG2().newElement();
        g2Element.setFromBytes(pkBytes); 
        return new BLS01PublicKeyParameters(parameters, g2Element);
    }
    
    public BigInteger bytesToBigInteger(byte[] bytes) {
        return new BigInteger(1, bytes); 
    }
    
    public byte[] bigIntegerToBytes(BigInteger bigInteger) {
        return bigInteger.toByteArray();
    }

    public static void main(String[] args) {
        BLS01 bls01 = new BLS01();
    
        AsymmetricCipherKeyPair keyPair = bls01.keyGen();
    
        BigInteger sk = bls01.extractPrivateKey(keyPair.getPrivate());
        byte[] pk = bls01.extractPublicKey(keyPair.getPublic());
    
        BigInteger pkBigInt = bls01.bytesToBigInteger(pk);
    
        System.out.println("Private Key as BigInteger: " + sk);
        System.out.println("Public Key as BigInteger: " + pkBigInt);
    
        BLS01Parameters parameters = ((BLS01PublicKeyParameters) keyPair.getPublic()).getParameters();
        byte[] pkBytes = bls01.bigIntegerToBytes(pkBigInt);
    
        CipherParameters recreatedPrivateKey = bls01.createPrivateKey(sk, parameters);
        CipherParameters recreatedPublicKey = bls01.createPublicKey(pkBytes, parameters);
    
        String message = "Hello World!";
        boolean isSameMessageValid = bls01.verify(bls01.sign(message, keyPair.getPrivate()), message, keyPair.getPublic());
        System.out.println("Verification for the same message: " + (isSameMessageValid ? "Passed" : "Failed"));

    
        boolean isDifferentMessageValid = bls01.verify(bls01.sign(message, keyPair.getPrivate()), "Hello Italy!", keyPair.getPublic());
        System.out.println("Verification for different messages: " + (isDifferentMessageValid ? "Failed" : "Passed"));
    
        byte[] signature = bls01.sign(message, recreatedPrivateKey);
        boolean isValid = bls01.verify(signature, message, recreatedPublicKey);
        System.out.println("Verification with recreated keys: " + (isValid ? "Passed" : "Failed"));
    }
    
}
