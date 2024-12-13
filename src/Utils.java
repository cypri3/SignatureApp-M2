import java.math.BigInteger;
import java.util.Arrays;

public class Utils {

    static final BigInteger a = new BigInteger("-3");        
    static final BigInteger b = new BigInteger("2455155546008943817740293915197451784769108058161191238065");
    static final BigInteger p = new BigInteger("6277101735386680763835789423207666416083908700390324961279");

    public BigInteger hexToBigInt(String stringHex) {
        BigInteger integerHex = new BigInteger(stringHex, 16);
        return integerHex;
    }

    static String bigIntToHex(BigInteger bigInteger) {
        String stringHex = bigInteger.toString(16);
        return stringHex;
    }

    public BigInteger[] add(BigInteger[] coordP,BigInteger[] coordQ){
        BigInteger lambda;
        BigInteger xP = coordP[0];
        BigInteger yP = coordP[1];
        BigInteger xQ = coordQ[0];
        BigInteger yQ = coordQ[1];
        BigInteger[] infPoint = {BigInteger.ZERO,BigInteger.ZERO};

        if (Arrays.equals(coordP,infPoint) && Arrays.equals(coordQ, infPoint)){
            return infPoint;
        }                
        if (Arrays.equals(coordP,infPoint)) {
            return coordQ;
        } 
        else if (Arrays.equals(coordQ, infPoint)) {
            return coordP;
        }
        if (xP.subtract(xQ).mod(p).compareTo(BigInteger.ZERO) == 0) {
            BigInteger nom = xP.multiply(xP).multiply(BigInteger.valueOf(3)).add(a);
            BigInteger den = yP.add(yP);
            lambda = nom.multiply(den.modInverse(p));
        } 
        else {
            BigInteger nom = yQ.subtract(yP);
            BigInteger den = xQ.subtract(xP);
            lambda = nom.multiply(den.modInverse(p));
        }
        BigInteger xR = lambda.multiply(lambda).subtract(xP).subtract(xQ).mod(p);
        BigInteger yR = lambda.multiply(xP.subtract(xR)).subtract(yP).mod(p);
        BigInteger[] coordR = {xR,yR};

        return coordR;
    }
            
    public BigInteger[] multByK(BigInteger[] coord,BigInteger k){  
        int bitLength = k.bitLength();
        BigInteger[] resP = {BigInteger.ZERO,BigInteger.ZERO};
        for (int i = bitLength-1; i >= 0; --i) {
            resP = add(resP, resP);
            if (k.testBit(i)) {
                resP = add(resP, coord);
                }
            }

        return resP;
    }
}
