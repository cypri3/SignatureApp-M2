import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PKI {

    private static String filename = "tests/pki.txt";

    public static int newUser() {
        try {
            FileWriter file = new FileWriter(filename,true);
            BufferedWriter buffered = new BufferedWriter(file);
            int userId = getUserId();
            String newKey = "<user>"+ userId + "</user>\n" +
                            "    <key>\n" +
                            "        <type>DSA</type>\n" +
                            "        <privateKey>" + null + "</privateKey>\n" +
                            "        <publicKey>" + null + "</publicKey>\n" +
                            "    </key>\n" +
                            "    <key>\n" +
                            "        <type>RSA</type>\n" +
                            "        <privateKey>" + null + "," + null + "</privateKey>\n" +
                            "        <publicKey>" + null + "," + null + "</publicKey>\n" +
                            "    </key>\n" +
                            "    <key>\n" +
                            "        <type>BLS</type>\n" +
                            "        <privateKey>" + null + "</privateKey>\n" +
                            "        <publicKey>" + null + "</publicKey>\n" +
                            "    </key>\n" +
                            "    <key>\n" +
                            "        <type>ECDSA</type>\n" +
                            "        <privateKey>" + null + "</privateKey>\n" +
                            "        <publicKey>"  + null + "</publicKey>\n" +
                            "    </key>\n" +
                            "</user>\n";           
            buffered.write(newKey);
            buffered.newLine();
            buffered.close();
            return userId;
        } 
        catch(IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    public static int getUserId() {
        try {
            File file = new File(filename);
            if (file.length() == 0) {
                return 0; 
            }
            int lastUserId = -1;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim(); 
                if (line.startsWith("<user>") && line.endsWith("</user>")) {
                    String idStr = line.substring(6, line.indexOf("</user>")); 
                    int userId = Integer.parseInt(idStr);
                    if (userId > lastUserId) {
                        lastUserId = userId; 
                    }
                }
            }
            return lastUserId + 1;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0; 
    }
    
    public static void newKeys(int userId, String typeKey, BigInteger[] keys) {
        if (typeKey == "RSA") {
            String privateKey1 = bigIntToHex(keys[0]);
            String privateKey2 = bigIntToHex(keys[1]);
            String publicKey1 = bigIntToHex(keys[2]);
            String publicKey1 = bigIntToHex(keys[3]);
        }
        else {
            String privateKey = bigIntToHex(keys[0]);
            String publicKey = bigIntToHex(keys[1]);
        }

        try {
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            List<String> lines = new ArrayList<>();
            String line;
            boolean isTargetUser = false;
    
            while ((line = reader.readLine()) != null) {
                String originalLine = line;
    
                if (line.trim().startsWith("<user>") && line.trim().endsWith("</user>")) {
                    String idStr = line.substring(6, line.indexOf("</user>"));
                    int currentUserId = Integer.parseInt(idStr);
                    isTargetUser = (currentUserId == userId);
                }
    
                if (isTargetUser && originalLine.trim().startsWith("<type>"+typeKey+"</type>")) {
                    lines.add(originalLine); 
                    line = reader.readLine();
                    if (line.trim().startsWith("<privateKey>")) {
                        if (typeKey == "RSA") {
                            lines.add("        <privateKey>" + privateKey1 + "," + privateKey2 + "</privateKey>");
                        }
                        else {
                            lines.add("        <privateKey>" + privateKey + "</privateKey>");
                        }
                    }
    
                    line = reader.readLine();
                    if (line.trim().startsWith("<publicKey>")) {
                        if (typeKey = "RSA") {
                            lines.add("        <publicKey>" + publicKey1 + "," + publicKey2 + "</publicKey>");
                        }
                        else {
                            lines.add("        <publicKey>" + publicKey + "</publicKey>");
                        }
                    }
                    continue;
                }
    
                lines.add(originalLine);
            }
            reader.close();
    
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String modifiedLine : lines) {
                writer.write(modifiedLine);
                writer.newLine();
            }
            writer.close();
    
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static BigInteger[] getPublicKey(int userId, String typeKey) {
        try {
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean isTargetUser = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("<user>") && line.endsWith("</user>")) {
                    String idStr = line.substring(6, line.indexOf("</user>"));
                    int currentUserId = Integer.parseInt(idStr);
                    isTargetUser = (currentUserId == userId);
                }

                if (isTargetUser && line.startsWith("<type>" + typeKey + "</type>")) {
                    String publicKey1 = null;
                    String publicKey2 = null;

                    line = reader.readLine().trim();
                    if (line.startsWith("<publicKey>")) {
                        publicKey1 = line.substring(13, line.indexOf("</publicKey>'"));
                        publicKey2 = 
                    }

                    reader.close();
                    BigInteger publicK = hexToBigInt(publicKey);
                    return publicK;
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        throw new IllegalArgumentException("Keys " + typeKey + " not found for userId: " + userId);
    }

    public static BigInteger getPrivateKey(int userId, String typeKey) {
        try {
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean isTargetUser = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("<user>") && line.endsWith("</user>")) {
                    String idStr = line.substring(6, line.indexOf("</user>"));
                    int currentUserId = Integer.parseInt(idStr);
                    isTargetUser = (currentUserId == userId);
                }

                if (isTargetUser && line.startsWith("<type>" + typeKey + "</type>")) {
                    String privateKey = null;

                    line = reader.readLine().trim();
                    if (line.startsWith("<privateKey>")) {
                        privateKey = line.substring(13, line.indexOf("</privateKey>"));
                    }

                    reader.close();
                    if (privateKey != null) {
                        BigInteger privateK = hexToBigInt(privateKey);
                        return privateK;
                    }
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        throw new IllegalArgumentException("Keys " + typeKey + " not found for userId: " + userId);
    }

    public static void main(String[] args) {
        BigInteger privateKey = new BigInteger("708026688110");
        BigInteger publicKey = new BigInteger("50802781126");
        int userId = PKI.newUser();
        PKI.newKeys(userId, "DSA", privateKey, publicKey);
        BigInteger pk = PKI.getPublicKey(userId, "DSA");
        BigInteger sk = PKI.getPrivateKey(userId, "DSA");
    }
}

