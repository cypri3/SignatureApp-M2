import java.math.BigInteger;

public class Utils {
    static BigInteger hexToBigInt(String stringHex) {
        BigInteger integerHex = new BigInteger(stringHex, 16);
        return integerHex;
    }

    static String bigIntToHex(BigInteger bigInteger) {
        int integerHex = bigInteger.intValue();
        String stringHex = Integer.toHexString(integerHex);
        return stringHex;
    }
}
