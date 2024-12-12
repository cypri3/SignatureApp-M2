import java.security.NoSuchAlgorithmException;

public interface Hashs {
    byte[] hash(byte[] message) throws NoSuchAlgorithmException;
}
