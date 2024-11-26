import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.security.MessageDigest;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try {
            Pairing pairing = PairingFactory.getPairing("src/a.properties");

            Element g = pairing.getG1().newRandomElement().getImmutable();
            System.out.println("g : " + g);

            Element sk = pairing.getZr().newRandomElement();
            // System.out.println("sk : " + sk);

            Element pk = g.powZn(sk).getImmutable();
            // System.out.println("pk : " + pk);

            String message = "Message à signer";
            Element hashedMessage = hashToG1(message, pairing);
            // System.out.println("h : " + hashedMessage);

            Element signature = hashedMessage.powZn(sk).getImmutable();
            System.out.println("Signature : " + signature + "\n\n");

            Element left = pairing.pairing(signature, g);
            System.out.println("e(signature, g): " + left);

            Element right = pairing.pairing(hashedMessage, pk);
            System.out.println("e(h(m), pk): " + right);

            boolean isValid = left.isEqual(right);
            System.out.println("La signature est " + (isValid ? "valide" : "invalide"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Element hashToG1(String message, Pairing pairing) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fullHash = digest.digest(message.getBytes(StandardCharsets.UTF_8));

            byte[] hash48 = new byte[6];
            System.arraycopy(fullHash, 0, hash48, 0, 6);

            Element hashedMessage = pairing.getG1().newElement().setFromHash(hash48, 0, hash48.length).getImmutable();

            System.out.println("hash tronqué (hex) : " + bytesToHex(hash48));
            System.out.println("hash du message (dans G1): " + hashedMessage);

            return hashedMessage;
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du hachage du message", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
