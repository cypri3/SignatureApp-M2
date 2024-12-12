import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
                            "        <privateKey>" + null + "</privateKey>\n" +
                            "        <publicKey>" + null + "</publicKey>\n" +
                            "    </key>\n" +
                            "    <key>\n" +
                            "        <type>BLS</type>\n" +
                            "        <privateKey>" + null + "</privateKey>\n" +
                            "        <publicKey>" + null + "</publicKey>\n" +
                            "    </key>\n" +
                            "    <key>\n" +
                            "        <type>ECDSA</type>\n" +
                            "        <privateKey>" + null + "</privateKey>\n" +
                            "        <publicKey>" + null + "</publicKey>\n" +
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
    
    public static void newKeyDSA(int userId, String typeKey, String publicKey, String privateKey) {
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
                        lines.add("        <privateKey>" + privateKey + "</privateKey>");
                    }
    
                    line = reader.readLine();
                    if (line.trim().startsWith("<publicKey>")) {
                        lines.add("        <publicKey>" + publicKey + "</publicKey>");
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

    public static String getKeys(int userId, String typeKey) {
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
                    String publicKey = null;

                    line = reader.readLine().trim();
                    if (line.startsWith("<privateKey>")) {
                        privateKey = line.substring(13, line.indexOf("</privateKey>"));
                    }

                    line = reader.readLine().trim();
                    if (line.startsWith("<publicKey>")) {
                        publicKey = line.substring(13, line.indexOf("</publicKey>"));
                    }

                    reader.close();

                    return "Private Key: " + privateKey + "\nPublic Key: " + publicKey;
                }
            }

            reader.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return "Keys " + typeKey + " not found for userId: " + userId;
    }


    public static void main(String[] args) {
        String privateKey = "08026688110";
        String publicKey = "0802781126";
        int userId = PKI.newUser();
        PKI.newKeyDSA(userId, "DSA", privateKey, publicKey);
        System.out.println(PKI.getKeys(userId, "DSA"));
    }
}

