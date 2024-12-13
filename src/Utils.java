import java.math.BigInteger;

public class Utils {
    static BigInteger hexToBigInt(String stringHex) {
        BigInteger integerHex = new BigInteger(stringHex, 16);
        return integerHex;
    }

    static String bigIntToHex(BigInteger bigInteger) {
        String stringHex = bigInteger.toString(16);
        return stringHex;
    }
}
