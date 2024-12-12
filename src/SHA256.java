import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 implements Hashs {
    @Override
    public byte[] hash(byte[] message) throws NoSuchAlgorithmException {

        MessageDigest messageHash = MessageDigest.getInstance("SHA-256");
        messageHash.update(message);
        byte[] hash = messageHash.digest();

        return hash;
    }
}