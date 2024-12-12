import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;


public class Hash {

    static byte[] SHA1(byte[] message) throws NoSuchAlgorithmException{
            
        MessageDigest messageHash = MessageDigest.getInstance("SHA-1");
        messageHash.update(message);
        byte[] hash = messageHash.digest();
        
        return hash;
        }
    
    static byte[] SHA256(byte[] message) throws NoSuchAlgorithmException{
            
        MessageDigest messageHash = MessageDigest.getInstance("SHA-256");
        messageHash.update(message);
        byte[] hash = messageHash.digest();
        
        return hash;
        }

    static byte[] MD5(byte[] message) throws NoSuchAlgorithmException{
            
        MessageDigest messageHash = MessageDigest.getInstance("MD5");
        messageHash.update(message);
        byte[] hash = messageHash.digest();

        return hash;
        }
    
    static BigInteger bigIntToHex(String stringHex){
        BigInteger integerHex = new BigInteger(stringHex ,16);
        return integerHex;
        }

    static String hexToBigInt(BigInteger integer){
        int integerHex = integer.intValue();
        String stringHex=Integer.toHexString(integerHex);
        return stringHex;
        }


    public static void main (String[] arg) throws Exception{
        System.out.println("Que faire ?");
        Scanner scan1 = new Scanner(System.in);
        String action=scan1.next();
        String message = "HelloWorld";
        byte[] messByte = message.getBytes();
            switch(action){
                case "o" -> {
                    byte[] defaultHash = SHA1(messByte);
                    System.out.println(Arrays.toString(defaultHash));
                    break;
                }
                case "t" -> {
                    byte[] defaultHash = SHA256(messByte);
                    System.out.println(Arrays.toString(defaultHash));
                    break;
                }
                case "m" -> {
                    byte[] defaultHash = MD5(messByte);
                    System.out.println(Arrays.toString(defaultHash));
                    break;
                }
                case "v" -> {
                    byte[] defaultHash = BigInteger.valueOf(message.hashCode()).toByteArray();
                    System.out.println(Arrays.toString(defaultHash));
                    break;
                }
                case "a" -> {
                    byte[] defaultHash1 = SHA1(messByte);
                    System.out.println(Arrays.toString(defaultHash1));
                    byte[] defaultHash2 = SHA256(messByte);
                    System.out.println(Arrays.toString(defaultHash2));
                    byte[] defaultHash3 = MD5(messByte);
                    System.out.println(Arrays.toString(defaultHash3));
                    byte[] defaultHash = BigInteger.valueOf(message.hashCode()).toByteArray();
                    System.out.println(Arrays.toString(defaultHash));
                    break;
                }
                case "!" -> {
                    String hexValue = "FFF";
                    BigInteger intValue = bigIntToHex(hexValue);
                    System.out.println("Integer Value : " + intValue);
                    String valueHex = hexToBigInt(intValue);
                    System.out.println(valueHex);
                }
                default -> System.out.println("Mauvais argument"); 
            }
        }
    }
