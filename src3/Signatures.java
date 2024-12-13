import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Signatures {

    /*public class DSA {
        // DSA parameters
        static final BigInteger l = new BigInteger("2").pow(160).add(new BigInteger("7"));
        static final BigInteger p = new BigInteger("1")
                .add(l.multiply(new BigInteger("2").pow(864).add(new BigInteger("218"))));
        static final BigInteger g = new BigInteger("2").modPow(p.subtract(BigInteger.ONE).divide(l), p);

        // Keys generation function
        static BigInteger[] keyGen() {
            Random rdn = new Random();
            BigInteger privateKey = new BigInteger(160, rdn);
            BigInteger publicKey = g.modPow(privateKey, p);
            BigInteger[] keys = { privateKey, publicKey };
            return keys;
        }

        // Sign function
        // sign(byte[] hash,BigInteger[] keys)
        static byte[] sign(byte[] hash, BigInteger[] keys) { // BigInteger[] sign(String m, BigInteger ka){
            Random rdnrdn = new Random();
            BigInteger k = new BigInteger(159, rdnrdn).mod(l);
            BigInteger h = new BigInteger(hash);
            // BigInteger H = BigInteger.valueOf(m.hashCode());
            BigInteger r = (g.modPow(k, p)).mod(l);
            BigInteger s = k.modInverse(l).multiply(h.add(keys[0].multiply(r))).mod(l);// ((k.modInverse(p)).multiply(h.add(keys[0].multiply(r)))).mod(l);
            ByteArrayOutputStream si = new ByteArrayOutputStream();
            try {
                si.write(k.toByteArray());
                si.write(s.toByteArray());
            } catch (IOException ex) {
            }
            byte[] signature = si.toByteArray();
            System.out.println((Arrays.toString(s.toByteArray())));
            return signature;
        }

        // Verification function
        static boolean verify(byte[] signature, byte[] message, BigInteger publicKey) {
            byte[] rand = new byte[20];
            System.arraycopy(signature, 0, rand, 0, 20);
            BigInteger K = new BigInteger(rand);
            BigInteger r = (g.modPow(K, p)).mod(l);
            int len = signature.length;
            byte[] sign = new byte[len - 20];
            System.arraycopy(signature, 20, sign, 0, (len - 20));
            System.out.println(Arrays.toString(sign));
            BigInteger s = new BigInteger(sign);
            if (r.compareTo(l) == 1 && s.compareTo(l) == 1) {
                return false;
            } else {
                // BigInteger H = BigInteger.valueOf(m.hashCode());
                BigInteger m = new BigInteger(message);
                BigInteger t = s.modInverse(l);
                BigInteger u = (t.multiply(m)).mod(l);
                BigInteger v = (t.multiply(r)).mod(l);
                BigInteger R = ((g.modPow(u, p)).multiply(publicKey.modPow(v, p))).mod(p).mod(l);
                // g.pow(u.intValue()).multiply(publicKey.pow(v.intValue())).mod(p).mod(l);
                return R.equals(r);
            }
        }
    }

    public class RSA {
        // keys : publicKey, privateKey

        static BigInteger[] keyGen() {
            BigInteger p = new BigInteger(2 ^ 50, 25, new Random());
            BigInteger q = new BigInteger(2 ^ 50, 25, new Random());
            BigInteger phi = (p.add(BigInteger.valueOf(-1))).multiply(q.add(BigInteger.valueOf(-1)));
            BigInteger e = BigInteger.valueOf(65535);

            while (!(e.gcd(phi).equals(BigInteger.ONE))) {
                p = new BigInteger(2 ^ 50, 25, new Random());
                q = new BigInteger(2 ^ 50, 25, new Random());
                phi = (p.add(BigInteger.valueOf(-1))).multiply(q.add(BigInteger.valueOf(-1)));
            }
            BigInteger N = p.multiply(q);
            BigInteger[] keys = { p, q, e, N };
            return keys;
        }

        static byte[] sign(byte[] hash, BigInteger[] keys) {
            BigInteger h = new BigInteger(hash);
            BigInteger phi = (keys[0].add(BigInteger.valueOf(-1))).multiply(keys[1].add(BigInteger.valueOf(-1)));
            BigInteger d = keys[2].modInverse(phi);
            BigInteger s = h.modPow(d, keys[3]);
            byte[] signature = s.toByteArray();
            return signature;
        }

        static boolean verify(byte[] signature, byte[] message, BigInteger[] publicKey) {
            BigInteger s = new BigInteger(signature);
            BigInteger verif = s.modPow(publicKey[0], publicKey[1]);
            BigInteger m = new BigInteger(message);
            return verif.equals(m);
        }
    }

    public class ECDSA1 {

        static KeyPair keyGen() throws Exception {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
            keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
            KeyPair keys = keyGen.generateKeyPair();

            return keys;
        }

        static byte[] sign(byte[] hash, KeyPair keys) throws Exception {

            PrivateKey privateKey = keys.getPrivate();
            // PublicKey pub = keys.getPublic();
            Signature ECDSA = Signature.getInstance("SHA256withECDSA");//"SHA1withECDSA");
            ECDSA.initSign(privateKey);
            ECDSA.update(hash);
            byte[] signature = ECDSA.sign();

            return signature;
        }

        static boolean verify(byte[] signature, byte[] message, PublicKey publicKey) throws Exception {

            Signature ECDSA = Signature.getInstance("SHA1withECDSA");
            ECDSA.initVerify(publicKey);
            ECDSA.update(message);

            return ECDSA.verify(signature);
        }
    }

    
     * Signature ecdsa = Signature.getInstance("SHA256withECDSA");
     * ecdsa.initSign(priv);
     * String str = "This is string to sign";
     * byte[] strByte = str.getBytes("UTF-8");
     * ecdsa.update(strByte);
     * byte[] realSig = ecdsa.sign();
     * System.out.println("Signature: " + new BigInteger(1, realSig).toString(16));
     */

        //BigInteger n = new BigInteger("1046183622564446793972631570523898309550287400092908001756");

    /*public BigInteger[] add(BigInteger[] coordP,BigInteger[] coordQ){
        BigInteger lambda;
        BigInteger xP = coordP[0];
        BigInteger yP = coordP[1];
        BigInteger xQ = coordQ[0];
        BigInteger yQ = coordQ[1];
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
        BigInteger[] resP = {};
        for (int i = bitLength - 1; i >= 0; --i) {
            resP = add(resP, resP);
            if (k.testBit(i)) {
                resP = add(resP, coord);
                }
            }
        return resP;
    }*/

    public class ECDSA {

        static final BigInteger a = new BigInteger("-3");        
        static final BigInteger b = new BigInteger("2455155546008943817740293915197451784769108058161191238065");
        static final BigInteger p = new BigInteger("6277101735386680763835789423207666416083908700390324961279");
        static final BigInteger n = new BigInteger("6277101735386680763835789423176059013767194773182842284081");
        static final BigInteger xP = new BigInteger("602046282375688656758213480587526111916698976636884684818");
        static final BigInteger yP = new BigInteger("174050332293622031404857552280219410364023488927386650641");

        public static BigInteger[] add(BigInteger[] coordP,BigInteger[] coordQ){
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
            
                public static BigInteger[] multByK(BigInteger[] coord,BigInteger k){  
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
                
                public static BigInteger[] keyGen(){
                                        
                                                    /*KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
                                                    keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
                                                    KeyPair defaultKeys = keyGen.generateKeyPair();
                                                    PublicKey publicKey = defaultKeys.getPublic();
                                                    byte[] xPri = new byte[77];
                                                    System.arraycopy((publicKey.toString()).getBytes(), 46, xPri, 0, 77);
                                                    BigInteger xCoord = new BigInteger(new String(xPri, StandardCharsets.UTF_8));
                                                    byte[] yPri = new byte[77];
                                                    System.arraycopy((publicKey.toString()).getBytes(), 142, yPri, 0, 77);
                                                    BigInteger yCoord = new BigInteger(new String(yPri, StandardCharsets.UTF_8));*/
                            Random rdnrdn = new Random();
                            BigInteger s = new BigInteger(20, rdnrdn);
                            BigInteger[] coordP = {xP,yP};
                            BigInteger[] coordR = multByK(coordP,s);
                    BigInteger[] keys = {s,coordR[0],coordR[1]};
                        
                    return keys;
                    }
                        
                public static byte[] sign(byte[] hash, BigInteger[] keys){
                                                    
                                    Random rdnrdn = new Random();
                                    BigInteger k = new BigInteger(23, rdnrdn);
                                    BigInteger[] coordP = {xP,yP};
                                    BigInteger[] coordG = multByK(coordP, k);
                                    BigInteger x = coordG[0];
                                    BigInteger y = k.modInverse(n).multiply(new BigInteger(hash).add(keys[0].multiply(x))).mod(n);
                                                    //byte[] signature = y.toByteArray();
                                    ByteArrayOutputStream si = new ByteArrayOutputStream();
                                    try {
                                        si.write(k.toByteArray());
                                        si.write(y.toByteArray());
                                        } catch (IOException ex) {
                                                    }
                                        byte[] signature = si.toByteArray();
                                                    
                                        return signature;
                                    }
                                            
                                public static boolean verify(byte[] signature, byte[] hash, BigInteger[] publicKey){
                                                                        
                                                                    byte[] rand = new byte[3];
                                                                    System.arraycopy(signature, 0, rand, 0, 3);
                                                                    BigInteger K = new BigInteger(rand);
                                                                    BigInteger h = new BigInteger(hash);
                                                                    BigInteger[] coordP = {xP,yP};
                                                                    BigInteger[] coordG = multByK(coordP, K);
                                                                    BigInteger x = coordG[0];
                                                                    int len = signature.length;
                                                                    byte[] sign = new byte[len - 3];
                                                                    System.arraycopy(signature, 3, sign, 0, (len - 3));
                                                                    BigInteger y = new BigInteger(sign);
                                                                    BigInteger u = h.multiply(y.modInverse(n)).mod(n);
                                                                    BigInteger v = x.multiply(y.modInverse(n)).mod(n);
                                                                    BigInteger[] res = add(multByK(coordP, u),multByK(publicKey, v));
                                                                        
                                                                    return (res[0].equals(x));
                                                                    }
                                                                        
                                                                }
                                                                        
                                                            public static void main(String[] arg) throws Exception {
                                                                System.out.println("Que faire ?");
                                                                Scanner scan1 = new Scanner(System.in);
                                                                String action = scan1.next();
                                                                switch (action) {
                                                                    /*case "r" -> {
                                                                        BigInteger[] defaultKeys = RSA.keyGen();
                                                                        BigInteger e = defaultKeys[2];
                                                                        BigInteger N = defaultKeys[3];
                                                                        BigInteger p = defaultKeys[0];
                                                                        BigInteger q = defaultKeys[1];
                                                                        BigInteger[] publicKeys = { e, N };
                                                                        System.out.println("Clefs privées:  " + p + " " + q);
                                                                        System.out.println("Clefs publiques:  " + e + " " + N);
                                                                        System.out.println("Message?");
                                                                        Scanner scan8 = new Scanner(System.in);
                                                                        String m = scan8.nextLine();
                                                                        byte[] defaultRes = RSA.sign(m.getBytes(), defaultKeys);
                                                                        System.out.println("Signature:  " + Arrays.toString(defaultRes));
                                                                        System.out.println("Vérification:  " + RSA.verify(defaultRes, m.getBytes(), publicKeys));
                                                                        break;
                                                                    }
                                                                    case "t" -> {
                                                                        BigInteger[] coordP = {BigInteger.valueOf(1),BigInteger.valueOf(2)};
                                                                        //int num = BigInteger.valueOf(2).bitLength();
                                                                        //System.out.println(num);
                                                                        //BigInteger[] coordQ = {BigInteger.valueOf(1),BigInteger.valueOf(2)}; 
                                                                        BigInteger[] res1 = ECDSA.multByK(coordP, BigInteger.valueOf(2));
                                                                        System.out.println(res1[0].toString() + res1[1].toString());
                                                                        break;
                                                                    }*/
                                                                    
                                                                    case "t" -> {
                                                                        BigInteger[] defaultKeys = ECDSA.keyGen();
                                                                        BigInteger yR = defaultKeys[2];
                                                                        BigInteger s = defaultKeys[0];
                                                                        BigInteger xR = defaultKeys[1];
                                                                        BigInteger[] publicKeys = {xR,yR};
                                                                        System.out.println("Clefs privées:  " + s);
                                                                        System.out.println("Clefs publiques:  " + xR + " " + yR);                                                
                                                                        byte[] mess = ("HelloWorld").getBytes();
                                                                        byte[] defaultRes = ECDSA.sign(mess, defaultKeys);
                                                                        System.out.println("Signature:  " + Arrays.toString(defaultRes));
                                                                        System.out.println("Vérification:  " + ECDSA.verify(defaultRes, mess, publicKeys));
                                                                        /*BigInteger s = new BigInteger("1019389");
                                                                        BigInteger[] coordP = {ECDSA.xP,ECDSA.yP};
                                                                        BigInteger[] res = ECDSA.multByK(coordP,BigInteger.TWO);
                                                                        //BigInteger[] infPoint = {BigInteger.ZERO,BigInteger.ZERO};
                                                                        BigInteger[] res1 = ECDSA.add(coordP,coordP);
                                                                        System.out.println(res[0]+"    "+res[1]);
                                                                        System.out.println(res1[0]+"    "+res1[1]);*/

                break;
            }
            /*case "d" -> {
                BigInteger[] defaultKeys = DSA.keyGen();
                BigInteger ka = defaultKeys[0];
                BigInteger KA = defaultKeys[1];
                System.out.println("Clef privée:  " + ka);
                System.out.println("Clef publique:  " + KA);
                System.out.println("Message?");
                Scanner scan8 = new Scanner(System.in);
                String m = scan8.nextLine();
                byte[] defaultRes = DSA.sign(m.getBytes(), defaultKeys);
                System.out.println("Signatures:  " + Arrays.toString(defaultRes));
                System.out.println("Vérification:  " + DSA.verify(defaultRes, m.getBytes(), KA));
                break;
            }*/
            /*case "e" -> {
                KeyPair defaultKeys = ECDSA1.keyGen();
                //PrivateKey privateKey = defaultKeys.getPrivate();
                PublicKey publicKey = defaultKeys.getPublic();
                //System.out.println("Clef privée : " + privateKey.toString());//Arrays.toString((publicKey.toString()).getBytes())
                System.out.println("Clef publique : " + publicKey.toString());
                byte[] test1 = (publicKey.toString()).getBytes();
                byte[] xPri = new byte[77];
                System.arraycopy(test1, 46, xPri, 0, 77);
                BigInteger xCoord = new BigInteger(new String(xPri, StandardCharsets.UTF_8));
                System.out.println(xCoord);
                byte[] yPri = new byte[77];
                System.arraycopy(test1, 142, yPri, 0, 77);
                BigInteger yCoord = new BigInteger(new String(yPri, StandardCharsets.UTF_8));
                System.out.println(yCoord);
                //System.out.println(new BigInteger(yPri));
                //System.out.println(new String(yPri, StandardCharsets.UTF_8));
                /*System.out.println("Message?");
                Scanner scan8 = new Scanner(System.in);
                String m = scan8.nextLine();
                byte[] defaultRes = ECDSA.sign(m.getBytes(), defaultKeys);
                System.out.println("Signature:  " + Arrays.toString(defaultRes));
                System.out.println("Vérification:  " + ECDSA.verify(defaultRes, m.getBytes(), publicKey));
                break;
            }*/
            default -> System.out.println("Mauvais argument");
            }
    }
    
}