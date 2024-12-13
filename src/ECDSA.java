import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

public class ECDSA implements Signatures {

    static final BigInteger n = new BigInteger("6277101735386680763835789423176059013767194773182842284081");
    static final BigInteger xP = new BigInteger("602046282375688656758213480587526111916698976636884684818");
    static final BigInteger yP = new BigInteger("174050332293622031404857552280219410364023488927386650641");

    @Override
    public BigInteger[] keyGen(){

        Random rdnrdn = new Random();
        BigInteger s = new BigInteger(20, rdnrdn);
        BigInteger[] coordP = {xP,yP};
        BigInteger[] coordR = Utils.multByK(coordP,s);
        BigInteger[] keys = {s,coordR[0],coordR[1]};
                        
        return keys;
    }
                        
    @Override
    public byte[] sign(byte[] hash, BigInteger[] keys){
                                                    
        Random rdnrdn = new Random();
        BigInteger k = new BigInteger(23, rdnrdn);
        BigInteger[] coordP = {xP,yP};
        BigInteger[] coordG = Utils.multByK(coordP, k);
        BigInteger x = coordG[0];
        BigInteger y = k.modInverse(n).multiply(new BigInteger(hash).add(keys[0].multiply(x))).mod(n);
        ByteArrayOutputStream si = new ByteArrayOutputStream();
        try {
            si.write(k.toByteArray());
            si.write(y.toByteArray());
        } 
        catch (IOException ex) {
        }
        byte[] signature = si.toByteArray();
                                                    
        return signature;
    }
                                            
    @Override
    public boolean verify(byte[] signature, byte[] hash, BigInteger[] publicKey){
                                                                        
        byte[] rand = new byte[3];
        System.arraycopy(signature, 0, rand, 0, 3);
        BigInteger K = new BigInteger(rand);
        BigInteger h = new BigInteger(hash);
        BigInteger[] coordP = {xP,yP};
        BigInteger[] coordG = Utils.multByK(coordP, K);
        BigInteger x = coordG[0];
        int len = signature.length;
        byte[] sign = new byte[len - 3];
        System.arraycopy(signature, 3, sign, 0, (len - 3));
        BigInteger y = new BigInteger(sign);
        BigInteger u = h.multiply(y.modInverse(n)).mod(n);
        BigInteger v = x.multiply(y.modInverse(n)).mod(n);
        BigInteger[] res = Utils.add(Utils.multByK(coordP, u),Utils.multByK(publicKey, v));
                                                                        
        return (res[0].equals(x));
    }
                                                                        
}