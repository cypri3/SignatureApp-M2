import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
public class Signatures {

    public class DSA {
    //DSA parameters
        static final BigInteger l = new BigInteger("2").pow(160).add(new BigInteger("7"));
        static final BigInteger p = new BigInteger("1").add(l.multiply(new BigInteger("2").pow(864).add(new BigInteger("218"))));
        static final BigInteger g = new BigInteger("2").modPow(p.subtract(BigInteger.ONE).divide(l), p);

    //Keys generation function
        static BigInteger[] keyGen(){
            Random rdn = new Random();
            BigInteger privateKey = new BigInteger(160,rdn);
            BigInteger publicKey = g.modPow(privateKey,p);
            BigInteger[] keys = {privateKey,publicKey};
            return keys;
        }

    //Sign function
        //sign(byte[] hash,BigInteger[] keys)
        static byte[] sign(byte[] hash,BigInteger[] keys){ //BigInteger[] sign(String m, BigInteger ka){
            Random rdnrdn = new Random();
            BigInteger k = new BigInteger(159,rdnrdn).mod(l);
            BigInteger h = new BigInteger(hash);
            //BigInteger H = BigInteger.valueOf(m.hashCode());
            BigInteger r = (g.modPow(k,p)).mod(l);
            BigInteger s = k.modInverse(l).multiply(h.add(keys[0].multiply(r))).mod(l);//((k.modInverse(p)).multiply(h.add(keys[0].multiply(r)))).mod(l);
            ByteArrayOutputStream si = new ByteArrayOutputStream();
            try {
                si.write(k.toByteArray());
                si.write(s.toByteArray());
            } 
            catch (IOException ex) {}
            byte[] signature = si.toByteArray();
            System.out.println((Arrays.toString(s.toByteArray())));
            return signature;
        }

    //Verification function
        static boolean verify(byte[] signature,byte[] message, BigInteger publicKey){
            byte[] rand = new byte[20];
            System.arraycopy(signature,0, rand,0,20); 
            BigInteger K = new BigInteger(rand);
            BigInteger r = (g.modPow(K,p)).mod(l);
            int len = signature.length;
            byte[] sign = new byte[len-20];
            System.arraycopy(signature,20, sign,0,(len-20));
            System.out.println(Arrays.toString(sign));
            BigInteger s = new BigInteger(sign);
            if (r.compareTo(l)==1 && s.compareTo(l)==1){
                return false;
            }
            else{
                //BigInteger H = BigInteger.valueOf(m.hashCode());
                BigInteger m = new BigInteger(message);
                BigInteger t = s.modInverse(l);
                BigInteger u = (t.multiply(m)).mod(l);
                BigInteger v = (t.multiply(r)).mod(l);
                BigInteger R = ((g.modPow(u,p)).multiply(publicKey.modPow(v,p))).mod(p).mod(l);
                //g.pow(u.intValue()).multiply(publicKey.pow(v.intValue())).mod(p).mod(l);
                return R.equals(r);
                }
            }
        }

    public class RSA { 
        //keys : publicKey, privateKey

        static BigInteger[] keyGen(){
            BigInteger p = new BigInteger(2^50,25, new Random());
            BigInteger q = new BigInteger(2^50,25, new Random());
            BigInteger phi = (p.add(BigInteger.valueOf(-1))).multiply(q.add(BigInteger.valueOf(-1)));
            BigInteger e = BigInteger.valueOf(65535);

            while (!(e.gcd(phi).equals(BigInteger.ONE))){
                p = new BigInteger(2^50,25, new Random());
                q = new BigInteger(2^50,25, new Random());
                phi = (p.add(BigInteger.valueOf(-1))).multiply(q.add(BigInteger.valueOf(-1)));
            }
            BigInteger N = p.multiply(q);
            BigInteger[] keys = {e,N,p,q};
            return keys;
        }
        static byte[] sign(byte[] hash,BigInteger[] keys){
            BigInteger h = new BigInteger(hash);
            BigInteger phi = (keys[2].add(BigInteger.valueOf(-1))).multiply(keys[3].add(BigInteger.valueOf(-1)));
            BigInteger d = keys[0].modInverse(phi);
            BigInteger s = h.modPow(d,keys[1]);
            byte[] signature = s.toByteArray();
            return signature;
        }

        static boolean verify(byte[] signature,byte[] message, BigInteger[] publicKey){
            BigInteger s = new BigInteger(signature);
            BigInteger verif = s.modPow(publicKey[0],publicKey[1]);
            BigInteger m = new BigInteger(message);
            return verif.equals(m);
        }
    }
    
    public class ECDSA {
    
            static KeyPair keyGen()throws Exception{

                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
                keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
                KeyPair keys = keyGen.generateKeyPair();

            return keys;
            }
       
            static byte[] sign(byte[] hash,KeyPair keys) throws Exception {

                PrivateKey privateKey = keys.getPrivate();
                //PublicKey pub = keys.getPublic();
                Signature ECDSA = Signature.getInstance("SHA1withECDSA");
                ECDSA.initSign(privateKey);
                ECDSA.update(hash);
                byte[] signature = ECDSA.sign();

                return signature;
            }
            
            static boolean verify(byte[] signature, byte[] message,PublicKey publicKey) throws Exception {

                Signature ECDSA = Signature.getInstance("SHA1withECDSA");
                ECDSA.initVerify(publicKey);
                ECDSA.update(message);

                return ECDSA.verify(signature);
            }
        }
            
            /*Signature ecdsa = Signature.getInstance("SHA256withECDSA");
            ecdsa.initSign(priv);
            String str = "This is string to sign";
            byte[] strByte = str.getBytes("UTF-8");
            ecdsa.update(strByte);
            byte[] realSig = ecdsa.sign();
            System.out.println("Signature: " + new BigInteger(1, realSig).toString(16));
            */





    public static void main (String[] arg) throws Exception{
        System.out.println("Que faire ?");
        Scanner scan1 = new Scanner(System.in);
        String action=scan1.next();
        switch(action){
            case "r" -> {
                //To generate keys, sign a message and verify the signature
                BigInteger[] defaultKeys = RSA.keyGen();
                BigInteger e = defaultKeys[0];
                BigInteger N = defaultKeys[1];
                BigInteger p = defaultKeys[2];
                BigInteger q = defaultKeys[3];
                BigInteger[] publicKeys = {e,N};
                System.out.println("Clefs privées:  " + p + " " + q);
                System.out.println("Clefs publiques:  " + e + " " + N);
                System.out.println("Message?");
                Scanner scan8 = new Scanner(System.in);
                String m = scan8.nextLine();
                byte[] defaultRes = RSA.sign(m.getBytes(),defaultKeys);
                System.out.println("Signature:  " + Arrays.toString(defaultRes));
                System.out.println("Vérification:  " + RSA.verify(defaultRes,m.getBytes(),publicKeys));
                break;    
                }
            case "d" -> {
                BigInteger[] defaultKeys = DSA.keyGen();
                BigInteger ka=defaultKeys[0];
                BigInteger KA=defaultKeys[1];
                System.out.println("Clef privée:  " + ka);
                System.out.println("Clef publique:  " + KA);
                System.out.println("Message?");
                Scanner scan8 = new Scanner(System.in);
                String m = scan8.nextLine();
                byte[] defaultRes = DSA.sign(m.getBytes(),defaultKeys);
                System.out.println("Signatures:  " + Arrays.toString(defaultRes));
                System.out.println("Vérification:  " + DSA.verify(defaultRes,m.getBytes(),KA)); 
                break;
            }
            case "e" -> {
                KeyPair defaultKeys = ECDSA.keyGen();
                PrivateKey privateKey = defaultKeys.getPrivate();
                PublicKey publicKey = defaultKeys.getPublic();
                System.out.println("Clef privée : " + privateKey.toString());
                System.out.println("Clef publique : " + publicKey.toString());
                System.out.println("Message?");
                Scanner scan8 = new Scanner(System.in);
                String m = scan8.nextLine();
                byte[] defaultRes = ECDSA.sign(m.getBytes(),defaultKeys);
                System.out.println("Signature:  " + Arrays.toString(defaultRes));
                System.out.println("Vérification:  " + ECDSA.verify(defaultRes,m.getBytes(),publicKey)); 
                break;
            }
            /*case "w"->{
                BigInteger[] defaultKeys = DSA.keyGen();
                BigInteger ka=defaultKeys[0];
                BigInteger KA=defaultKeys[1];
                System.out.println("Clef privée:  " + ka);
                System.out.println("Clef publique:  " + KA);
                System.out.println("Message?");
                Scanner scan8 = new Scanner(System.in);
                String m = scan8.nextLine();
                byte[] defaultRes = DSA.sign(m.getBytes(),defaultKeys);
                break;
            }*/
            default -> System.out.println("Mauvais argument");
        }              
    }
}

    /*public static void main (String[] arg){
        System.out.println("Que faire ? Pour plus d'informations, taper 'aide'.");
        Scanner scan1 = new Scanner(System.in);
        String action=scan1.next();
        switch(action){
            case "g": //To generate keys 
                BigInteger[] generatedKeys = gen();
                System.out.println(Arrays.toString(generatedKeys));
                break;
            case "s": //To sign a message
                System.out.println("Message?");
                Scanner scan2 = new Scanner(System.in);
                String m1 = scan2.nextLine();
                System.out.println("Clef privée?");
                Scanner scan3 = new Scanner(System.in);
                long ka1 = scan3.nextLong();
                System.out.println(Arrays.toString(sign(m1,BigInteger.valueOf(ka1))));
                break;
            case "v": //To verify the signature
                System.out.println("Message?");
                Scanner scan4 = new Scanner(System.in);
                String m2 = scan4.nextLine();
                System.out.println("r?");
                Scanner scan5 = new Scanner(System.in);
                long r2 = scan5.nextLong();
                System.out.println("s?");
                Scanner scan6 = new Scanner(System.in);
                long s2 = scan6.nextLong();
                System.out.println("Clef publique?");
                Scanner scan7 = new Scanner(System.in);
                long KA2 = scan7.nextLong();
                System.out.println(verif(m2,BigInteger.valueOf(r2),BigInteger.valueOf(s2),BigInteger.valueOf(KA2))); 
                break;
            case "t": //To get the time needed to sign a message and verify a signature 10 000 times
                String M = "HelloWorld";
                BigInteger[] testKeys = gen();
                BigInteger ka3 = testKeys[0];
                BigInteger KA3 = testKeys[1];
                long t1 = System.currentTimeMillis();
                for (int i=0;i<10000;i++){
                    sign(M,ka3);
                }
                long t2 = System.currentTimeMillis();
                BigInteger[] testRes = sign(M,ka3);
                BigInteger S3 = testRes[0];
                BigInteger R3 = testRes[1];
                long t3 = (System.currentTimeMillis());
                for (int j=0;j<10000;j++){
                    verif(M,R3,S3,KA3);
                }
                long t4= (System.currentTimeMillis());
                System.out.println("Temps d'éxecution de 10 000 signatures: " + (long) (t2-t1) + " millisecondes.");
                System.out.println("Temps de vérification de 10 000 signatures: " + (long) (t4-t3) + " millisecondes.");
                break;
            case "h": //To hash a message
                System.out.println("Message?");
                Scanner scan11 = new Scanner(System.in);
                String TXT = scan11.nextLine();
                System.out.println(Hash(TXT));
                break;
            case "aide": //To print the help
                System.out.println("Taper 'g' pour générer des clefs privée et publique.");
                System.out.println("Taper 's' pour signer un message avec une clef privée.");
                System.out.println("Taper 'v' pour vérifier la signature d'un message.");
                System.out.println("Taper 't' pour obtenir le temps nécessaire à la vérification de 100 signatures.");
                System.out.println("Taper 'h' pour obtenir le résultat de la fonction de hachage sur un message.");
                System.out.println("Taper 'd' pour générer des clefs, signer un message avec celles-ci et vérifier la signature.");
                break;
            case "d": //To generate keys, sign a message and verify the signature
                BigInteger[] defaultKeys = gen();
                BigInteger ka=defaultKeys[0];
                BigInteger KA=defaultKeys[1];
                System.out.println("Clef privée:  " + ka);
                System.out.println("Clef publique:  " + KA);
                System.out.println("Message?");
                Scanner scan8 = new Scanner(System.in);
                String m = scan8.nextLine();
                BigInteger[] defaultRes = sign(m,ka);
                BigInteger R = defaultRes[0];
                BigInteger S = defaultRes[1];
                System.out.println("Signatures:  " + Arrays.toString(defaultRes));
                System.out.println("Vérification:  " + verif(m,R,S,KA)); 
                break;
            default:
                System.out.println("Mauvais argument");
        }              
    }*/

        //Hash function
        /*static BigInteger Hash(String m){
            String vide = new String();
            for (int i=0;i<m.length();i++){
                vide=vide+Integer.toBinaryString((int) m.charAt(i));
            }
            BigInteger fX = BigInteger.valueOf(Long.parseLong(vide.substring(0,Math.min(58,vide.length())),2)); 
            return fX;
        }*/