package src2;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 implements Hash {
    @Override
    public byte[] hash(byte[] message) throws NoSuchAlgorithmException{
            
        MessageDigest messageHash = MessageDigest.getInstance("SHA-1");
        messageHash.update(message);
        byte[] hash = messageHash.digest();
        
        return hash;
    }
}