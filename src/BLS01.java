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
        System.out.println("=== Début de keyGen ===");

        // Initialisation des paramètres
        BLS01ParametersGenerator setup = new BLS01ParametersGenerator();
        setup.init(PairingFactory.getPairingParameters("src/a.properties"));
        System.out.println("Paramètres de pairing générés.");

        // Génération des clés
        BLS01KeyPairGenerator keyGen = new BLS01KeyPairGenerator();
        keyGen.init(new BLS01KeyGenerationParameters(null, setup.generateParameters()));
        AsymmetricCipherKeyPair keyPair = keyGen.generateKeyPair();

        // Extraction des clés
        BigInteger privateKey = ((BLS01PrivateKeyParameters) keyPair.getPrivate()).getSk().toBigInteger();
        byte[] publicKeyBytes = ((BLS01PublicKeyParameters) keyPair.getPublic()).getPk().toBytes();
        BigInteger publicKey = new BigInteger(1, publicKeyBytes);

        System.out.println("Clé privée générée : " + privateKey);
        System.out.println("Clé publique générée : " + publicKey);

        System.out.println("=== Fin de keyGen ===");
        return new BigInteger[]{privateKey, publicKey};
    }

    @Override
    public byte[] sign(byte[] hash, BigInteger[] keys) {
        System.out.println("=== Début de sign ===");
        System.out.println("Hash à signer : " + new BigInteger(1, hash));
        System.out.println("Clé privée utilisée : " + keys[0]);

        // Initialisation des paramètres
        BLS01ParametersGenerator setup = new BLS01ParametersGenerator();
        setup.init(PairingFactory.getPairingParameters("src/a.properties"));
        BLS01Parameters blsParams = setup.generateParameters();

        // Création de la clé privée
        Pairing pairing = PairingFactory.getPairing(blsParams.getParameters());
        CipherParameters privateKey = new BLS01PrivateKeyParameters(blsParams, pairing.getZr().newElement(keys[0]));

        // Signature
        BLS01Signer signer = new BLS01Signer(new SHA256Digest());
        signer.init(true, privateKey);
        signer.update(hash, 0, hash.length);

        try {
            byte[] signature = signer.generateSignature();
            System.out.println("Signature générée : " + new BigInteger(1, signature));
            System.out.println("=== Fin de sign ===");
            return signature;
        } catch (CryptoException e) {
            System.err.println("Erreur lors de la génération de la signature : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la génération de la signature", e);
        }
    }

    @Override
    public boolean verify(byte[] signature, byte[] hash, BigInteger[] publicKey) {
        System.out.println("=== Début de verify ===");
        System.out.println("Signature à vérifier : " + new BigInteger(1, signature));
        System.out.println("Hash à vérifier : " + new BigInteger(1, hash));
        System.out.println("Clé publique utilisée : " + publicKey[0]);

        // Initialisation des paramètres
        BLS01ParametersGenerator setup = new BLS01ParametersGenerator();
        setup.init(PairingFactory.getPairingParameters("src/a.properties"));
        BLS01Parameters blsParams = setup.generateParameters();

        // Recréation de la clé publique
        Pairing pairing = PairingFactory.getPairing(blsParams.getParameters());
        Element g2Element = pairing.getG2().newElement();
        g2Element.setFromBytes(publicKey[0].toByteArray());
        System.out.println("g2Element (toString) : " + g2Element);
        CipherParameters publicKeyParam = new BLS01PublicKeyParameters(blsParams, g2Element);

        // Vérification
        BLS01Signer signer = new BLS01Signer(new SHA256Digest());
        signer.init(false, publicKeyParam);
        signer.update(hash, 0, hash.length);

        boolean isValid = signer.verifySignature(signature);
        System.out.println("Résultat de la vérification : " + isValid);
        System.out.println("=== Fin de verify ===");
        return isValid;
    }
}
