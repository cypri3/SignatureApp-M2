package src2;

import java.security.NoSuchAlgorithmException;


public interface Hash {
    byte[] hash(byte[] message) throws NoSuchAlgorithmException;
}
