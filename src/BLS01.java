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

public class BLS01 implements Signatures {

    @Override
    public BigInteger[] keyGen() {
        BLS01ParametersGenerator setup = new BLS01ParametersGenerator();
        setup.init(PairingFactory.getPairingParameters("src/a.properties"));

        BLS01KeyPairGenerator keyGen = new BLS01KeyPairGenerator();
        keyGen.init(new BLS01KeyGenerationParameters(null, setup.generateParameters()));

        AsymmetricCipherKeyPair keyPair = keyGen.generateKeyPair();
        
        BigInteger privateKey = extractPrivateKey(keyPair.getPrivate());
        byte[] publicKey = extractPublicKey(keyPair.getPublic());
        BigInteger publicKeyBigInt = bytesToBigInteger(publicKey);

        return new BigInteger[]{privateKey, publicKeyBigInt};
    }

    @Override
    public byte[] sign(byte[] hash, BigInteger[] keys) {
        BLS01PrivateKeyParameters privateKey = (BLS01PrivateKeyParameters) createPrivateKey(keys[0], 
                (BLS01Parameters) PairingFactory.getPairingParameters("src/a.properties"));
        return sign(new String(hash), privateKey);
    }
    
    @Override
    public boolean verify(byte[] signature, byte[] hash, BigInteger[] publicKey) {
        BLS01PublicKeyParameters publicKeyParam = (BLS01PublicKeyParameters) createPublicKey(
                bigIntegerToBytes(publicKey[1]), (BLS01Parameters) PairingFactory.getPairingParameters("src/a.properties"));
        return verify(signature, new String(hash), publicKeyParam);
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
}
