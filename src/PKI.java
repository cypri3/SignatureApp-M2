import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PKI {

    private static final String FILENAME = "bin/pki.xml";

    public static int newUser() {
        try (BufferedWriter buffered = new BufferedWriter(new FileWriter(FILENAME, true))) {
            int userId = getUserId();
            String newKey = "<user>" + userId + "</user>\n" +
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
                    "        <type>ECDSA</type>\n" +
                    "        <privateKey>" + null + "</privateKey>\n" +
                    "        <publicKey>" + null + "," + null + "</publicKey>\n" +
                    "    </key>\n" +
                    "</user>\n";
            buffered.write(newKey);
            buffered.newLine();
            return userId;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return 0;
    }

    public static int getUserId() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            if (reader.read() == -1) {
                return 0;
            }
            int lastUserId = 0;
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
        }
        return 0;
    }

    public static void newKeys(int userId, String typeKey, BigInteger[] keys) {
        String privateKey1 = null;
        String privateKey2 = null;
        String publicKey1 = null;
        String publicKey2 = null;

        if (typeKey.equals("RSA")) {
            privateKey1 = Utils.bigIntToHex(keys[0]);
            privateKey2 = Utils.bigIntToHex(keys[1]);
            publicKey1 = Utils.bigIntToHex(keys[2]);
            publicKey2 = Utils.bigIntToHex(keys[3]);
        }
        if (typeKey.equals("ECDSA")) {
            privateKey1 = Utils.bigIntToHex(keys[0]);
            publicKey1 = Utils.bigIntToHex(keys[1]);
            publicKey2 = Utils.bigIntToHex(keys[2]);
        }
        if (typeKey.equals("DSA")) {
            privateKey1 = Utils.bigIntToHex(keys[0]);
            publicKey1 = Utils.bigIntToHex(keys[1]);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
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

                if (isTargetUser && originalLine.trim().startsWith("<type>" + typeKey + "</type>")) {
                    lines.add(originalLine);
                    line = reader.readLine();
                    if (line.trim().startsWith("<privateKey>")) {
                        if (typeKey.equals("RSA")) {
                            lines.add("        <privateKey>" + privateKey1 + "," + privateKey2 + "</privateKey>");
                        } else {
                            lines.add("        <privateKey>" + privateKey1 + "</privateKey>");
                        }
                    }

                    line = reader.readLine();
                    if (line.trim().startsWith("<publicKey>")) {
                        if (typeKey.equals("RSA") || typeKey.equals("ECDSA")) {
                            lines.add("        <publicKey>" + publicKey1 + "," + publicKey2 + "</publicKey>");
                        } else {
                            lines.add("        <publicKey>" + publicKey1 + "</publicKey>");
                        }
                    }
                    continue;
                }

                lines.add(originalLine);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILENAME))) {
                for (String modifiedLine : lines) {
                    writer.write(modifiedLine);
                    writer.newLine();
                }
                writer.close();

            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static BigInteger[] getPublicKey(int userId, String typeKey) {
        BigInteger[] publicK = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
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
                    reader.readLine();
                    line = reader.readLine().trim();
                    if (line.startsWith("<publicKey>")) {
                        if (typeKey.equals("RSA") || typeKey.equals("ECDSA")) {
                            String publicKeys = line.substring(11, line.indexOf("</publicKey>"));
                            String[] publicKeyParts = publicKeys.split(",");
                            if (((!"null".equals(publicKeyParts[0])) && publicKeyParts[0] != null)) {
                                BigInteger publicKey1 = Utils.hexToBigInt(publicKeyParts[0]);
                                BigInteger publicKey2 = Utils.hexToBigInt(publicKeyParts[1]);
                                publicK = new BigInteger[2];
                                publicK[0] = publicKey1;
                                publicK[1] = publicKey2;
                            }
                        } else {
                            String publicKey = line.substring(11, line.indexOf("</publicKey>"));
                            if (!"null".equals(publicKey) && publicKey != null) {
                                BigInteger publicKey1 = Utils.hexToBigInt(publicKey);
                                publicK = new BigInteger[1];
                                publicK[0] = publicKey1;
                            }
                        }
                    }
                }
            }
            reader.close();
            return publicK;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return new BigInteger[0];
    }

    public static BigInteger[] getPrivateKey(int userId, String typeKey) {
        BigInteger[] privateK = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
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

                    line = reader.readLine().trim();

                    if (line.startsWith("<privateKey>")) {
                        if (typeKey.equals("RSA")) {
                            String privateKeys = line.substring(12, line.indexOf("</privateKey>"));
                            String[] privateKeyParts = privateKeys.split(",");
                            if (!"null".equals(privateKeyParts[0]) && privateKeyParts[0] != null) {
                                BigInteger privateKey1 = Utils.hexToBigInt(privateKeyParts[0]);
                                BigInteger privateKey2 = Utils.hexToBigInt(privateKeyParts[1]);
                                privateK = new BigInteger[2];
                                privateK[0] = privateKey1;
                                privateK[1] = privateKey2;
                            }
                        } else {
                            String privateKey = line.substring(12, line.indexOf("</privateKey>"));
                            if (!"null".equals(privateKey) && privateKey != null) {
                                BigInteger privateKey1 = Utils.hexToBigInt(privateKey);
                                privateK = new BigInteger[1];
                                privateK[0] = privateKey1;
                            }
                        }
                    }
                }
            }
            reader.close();
            return privateK;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return new BigInteger[0];
    }
}
