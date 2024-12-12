import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 implements Hashs {
    @Override
    public byte[] hash(byte[] message) throws NoSuchAlgorithmException {

        MessageDigest messageHash = MessageDigest.getInstance("MD5");
        messageHash.update(message);
        byte[] hash = messageHash.digest();

        return hash;
    }
}