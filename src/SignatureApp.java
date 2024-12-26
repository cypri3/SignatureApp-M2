import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class SignatureApp {

    private static File selectedFile = null;

    private static int selectedUser;
    private static BigInteger[] publicKey;
    private static BigInteger[] privateKey;
    private static int keyLength;
    private static int userId = -1;

    private static Signatures selectSignatureAlgorithm(String selectedSignature) {
        switch (selectedSignature) {
            case "DSA" -> {
                return new DSA();
            }
            case "RSA" -> {
                return new RSA();
            }
            case "ECDSA" -> {
                return new ECDSA();
            }
            default -> {
                JOptionPane.showMessageDialog(null, "Unknown signature algorithm", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
    }

    private static Hashs selectHashFunction(String selectedHash) {
        switch (selectedHash) {
            case "MD5" -> {
                return new MD5();
            }
            case "SHA1" -> {
                return new SHA1();
            }
            case "SHA256" -> {
                return new SHA256();
            }
            default -> {
                JOptionPane.showMessageDialog(null, "Unknown hash function", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
    }

    private static BigInteger[] updateKey(String selectedSignature, Signatures signatureAlgorithm){
        if (userId == -1) {
            if (PKI.getUserId() < 1) {
                PKI.newUser();
            }
            userId = 0;
        }
        publicKey = PKI.getPublicKey(userId, selectedSignature);
        privateKey = PKI.getPrivateKey(userId, selectedSignature);
        int publicLen = 0;
        int privateLen = 0;
        keyLength = 0;
        if(publicKey != null){ 
            publicLen = publicKey.length;
            privateLen = privateKey.length;
            keyLength = publicLen + privateLen;
        }


        BigInteger[] keyPair = new BigInteger[keyLength];
        if(publicLen > 0){
            System.arraycopy(privateKey, 0, keyPair, 0,privateLen );
            System.arraycopy(publicKey, 0, keyPair, privateLen, publicLen);
        }

        if (keyPair != null && keyPair.length == 0) {
            keyPair = signatureAlgorithm.keyGen();
            keyLength = keyPair.length;
            PKI.newKeys(userId, selectedSignature, keyPair);
        }
        if (keyPair != null && keyPair.length > 0) {
            switch (keyLength) {
                case 2 -> {
                    privateKey = new BigInteger[1];
                    publicKey = new BigInteger[1];
                    privateKey[0] = keyPair[0];
                    publicKey[0] = keyPair[1];
                }
                case 3 -> {
                    privateKey = new BigInteger[1];
                    publicKey = new BigInteger[2];
                    privateKey[0] = keyPair[0];
                    publicKey[0] = keyPair[1];
                    publicKey[1] = keyPair[2];
                }
                default -> {
                    privateKey = new BigInteger[2];
                    publicKey = new BigInteger[2];
                    privateKey[0] = keyPair[0];
                    privateKey[1] = keyPair[1];
                    publicKey[0] = keyPair[2];
                    publicKey[1] = keyPair[3];
                }
            }
        } else {
            System.out.println("Error: keyPair is null or empty.");
        }
        return keyPair;
    }

    public static void main(String[] args) {
        PDFdata PDFInstance = new PDFdata();
        JFrame frame = new JFrame("Signature App");
        System.setProperty("file.encoding", "UTF-8");
        java.nio.charset.Charset.defaultCharset();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 600);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JButton signButton = new JButton("Sign");
        JButton verifyButton = new JButton("Verify");
        signButton.setEnabled(false);
        verifyButton.setEnabled(false);
        

        // Zone d'affichage des fichiers
        JTextArea fileDisplayArea = new JTextArea("No selected file");
        fileDisplayArea.setEditable(false);
        fileDisplayArea.setLineWrap(true);
        fileDisplayArea.setWrapStyleWord(true);
        fileDisplayArea.setDropTarget(new DropTarget() {
            @Override
            @SuppressWarnings("UseSpecificCatch")
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    @SuppressWarnings("unchecked")
                    java.util.List<File> files = (java.util.List<File>) evt.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        File file = files.get(0);
                        String fileName = file.getName().toLowerCase();
                        if (fileName.endsWith(".pdf")) {
                            selectedFile = file;
                            fileDisplayArea.setText("Selected file : " + selectedFile.getAbsolutePath());
                            PDFInstance.setFile(selectedFile);
                            signButton.setEnabled(true);
                            verifyButton.setEnabled(true);
                        } else {
                            JOptionPane.showMessageDialog(frame, 
                                "Only PDF files are handled.", 
                                "Invalid file's type", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, 
                    "Error during the file's opening.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        panel.add(new JScrollPane(fileDisplayArea), BorderLayout.CENTER);

        // Gestion des utilisateurs
        JLabel currentUserLabel = new JLabel("Actual user : 0");
        currentUserLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.setBorder(BorderFactory.createTitledBorder("Users management"));

        JButton newUser = new JButton("Create a user");
        JButton loadUser = new JButton("Load an existing user");
        
        if(PKI.getUserId() < 2){
            loadUser.setEnabled(false);
        }

        newUser.addActionListener(evt -> {
            userId = PKI.getUserId();
            selectedUser = PKI.newUser();
            loadUser.setEnabled(true);
            currentUserLabel.setText("Actual user : " + selectedUser);
            JOptionPane.showMessageDialog(frame,
                    "User '" + selectedUser + "' has been successfully created.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        loadUser.addActionListener(evt -> {
            String userInput = JOptionPane.showInputDialog(
                    userPanel,
                    "Enter the user's ID, value bewtween 0 and " + Integer.toString(PKI.getUserId() - 1) + " :",
                    "User's loading",
                    JOptionPane.QUESTION_MESSAGE);

            if (userInput == null || userInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Invalid user's ID. Please retry",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    userId = Integer.parseInt(userInput.trim());
                    int sup = PKI.getUserId() - 1;

                    if (userId <= sup && userId >= 0) {
                        selectedUser = userId;
                        currentUserLabel.setText("Actual user : " + selectedUser);
                        JOptionPane.showMessageDialog(frame,
                                "User with ID " + userId + " successfully loaded.",
                                "success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Invalid user's ID. Please enter an integer between 0 and " + sup,
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(frame,
                            "Invalid user's ID. Please enter an integer.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        userPanel.add(newUser);
        userPanel.add(loadUser);
        userPanel.add(currentUserLabel);

        // Configuration des fichiers
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
        filePanel.setBorder(BorderFactory.createTitledBorder("Files management"));

        // Bouton pour sélectionner un fichier
        JButton selectFileButton = new JButton("Select a file");

        selectFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().toLowerCase().endsWith(".pdf");
                }

                @Override
                public String getDescription() {
                    return "PDF files (*.pdf)";
                }
            });

            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".pdf")) {
                    selectedFile = file;
                    fileDisplayArea.setText("Selected file : " + selectedFile.getAbsolutePath());
                    PDFInstance.setFile(selectedFile);
                    signButton.setEnabled(true);
                    verifyButton.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "Only PDF files are handled.", 
                                "Invalid file's type", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        

        filePanel.add(selectFileButton);
        filePanel.add(Box.createVerticalStrut(10));

        JTextArea tutorialArea = new JTextArea(5, 3);

        tutorialArea.setEditable(false);
        tutorialArea.setLineWrap(true);
        tutorialArea.setWrapStyleWord(true);
        tutorialArea.setText("""
1. Create or load a user (default : 0).

2. Drag and drop or select a PDF file.

3. Select the signature algorithm and hash function.

4. Sign and verify your files.

NB The sign function will modify the file and erase an already existing signature.
""");


        
        JScrollPane scrollPane = new JScrollPane(tutorialArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("General tutorial"));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        filePanel.add(scrollPane);

        filePanel.add(Box.createVerticalStrut(10));

        // Opérations sur la signature
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Signature's parameters"));

        JComboBox<String> algoBox = new JComboBox<>(new String[] { "DSA", "RSA", "ECDSA" });
        JComboBox<String> hashBox = new JComboBox<>(new String[] { "MD5", "SHA1", "SHA256" });


        signButton.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(frame,
                        "No file selected. Please select a file before signing it.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    PDFInstance.removeMetadata("Signature"); // Remise à nu de notre PDF


                    byte[] pdfBytes = PDFdata.readPDFAsBytes(selectedFile);

                    String selectedSignature = (String) algoBox.getSelectedItem();
                    String selectedHash = (String) hashBox.getSelectedItem();

                    Signatures signatureAlgorithm = selectSignatureAlgorithm(selectedSignature);
                    Hashs hashFunction = selectHashFunction(selectedHash);

                    if (signatureAlgorithm != null && hashFunction != null) {

                        byte[] hashValue;
                        try {
                            hashValue = hashFunction.hash(pdfBytes);
                        } catch (NoSuchAlgorithmException ex) {
                            JOptionPane.showMessageDialog(frame,
                                    "Hash function's error : " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        BigInteger[] keyPair = updateKey(selectedSignature,  signatureAlgorithm);
                        byte[] signature = signatureAlgorithm.sign(hashValue, keyPair);
                        PDFInstance.addMetadata("Signature", signature);

                        privateKey = null;
                        publicKey = null;

                        JOptionPane.showMessageDialog(frame,
                                "The file has been signed with : " + algoBox.getSelectedItem(),
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid signature algorithm or hash function", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(frame, "Error during the file's opening.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        verifyButton.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(frame,
                        "No file selected. Please select a file before verifying it.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                byte[] pdfBytes;
                try {

                    String selectedSignature = (String) algoBox.getSelectedItem();
                    String selectedHash = (String) hashBox.getSelectedItem();

                    Signatures signatureAlgorithm = selectSignatureAlgorithm(selectedSignature);
                    Hashs hashFunction = selectHashFunction(selectedHash);

                    if (signatureAlgorithm != null && hashFunction != null) {

                        updateKey(selectedSignature,  signatureAlgorithm);

                        byte[] signature = PDFInstance.getMetadata("Signature");
                        PDFInstance.removeMetadata("Signature");

                        byte[] hashValue;
                        
                        pdfBytes = PDFdata.readPDFAsBytes(selectedFile);
                        try {
                            hashValue = hashFunction.hash(pdfBytes);
                        } catch (NoSuchAlgorithmException ex) {
                            JOptionPane.showMessageDialog(frame,
                                    "Hash function's error : " + ex.getMessage(),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        boolean isValid = signatureAlgorithm.verify(signature, hashValue, publicKey);
                        PDFInstance.addMetadata("Signature", signature);
                        
                        privateKey = null;
                        publicKey = null;

                        if(isValid){
                            
                            JOptionPane.showMessageDialog(frame,
                            "Valid signature.",
                            "Error", JOptionPane.INFORMATION_MESSAGE);
    
                            }else{
                                JOptionPane.showMessageDialog(frame,
                                "Invalid signature.",
                                "Success", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid signature algorithm or hash function", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } catch (IOException err) {
                    JOptionPane.showMessageDialog(frame, "Error during the file's opening.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(new JLabel("Signature's type :"));
        buttonPanel.add(algoBox);
        buttonPanel.add(new JLabel("Hash's type :"));
        buttonPanel.add(hashBox);
        buttonPanel.add(signButton);
        buttonPanel.add(verifyButton);

        // Ajout des panels au frame
        frame.add(panel, BorderLayout.CENTER);
        frame.add(userPanel, BorderLayout.NORTH);
        frame.add(filePanel, BorderLayout.WEST);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

}
