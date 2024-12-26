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

public class Projet {

    private static File selectedFile = null;

    private static int selectedUser;
    private static BigInteger[] publicKey;
    private static BigInteger[] privateKey;
    private static int keyLength;
    private static int userId = -1;

    private static Signatures selectSignatureAlgorithm(String selectedSignature) {
        switch (selectedSignature) {
            case "BLS" -> {
                return new BLS01();
            }
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
                JOptionPane.showMessageDialog(null, "Algorithme de signature inconnu", "Erreur",
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
                JOptionPane.showMessageDialog(null, "Algorithme de hash inconnu", "Erreur", JOptionPane.ERROR_MESSAGE);
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
            System.out.println("Erreur: keyPair est null ou vide.");
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
        
        JButton signButton = new JButton("Signer");
        JButton verifyButton = new JButton("Vérifier");
        signButton.setEnabled(false);
        verifyButton.setEnabled(false);
        

        // Zone d'affichage des fichiers
        JTextArea fileDisplayArea = new JTextArea("Aucun fichier sélectionné");
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
                            fileDisplayArea.setText("Fichier sélectionné : " + selectedFile.getAbsolutePath());
                            PDFInstance.setFile(selectedFile);
                            signButton.setEnabled(true);
                            verifyButton.setEnabled(true);
                        } else {
                            JOptionPane.showMessageDialog(frame, 
                                "Seuls les fichiers JPG et PDF sont acceptés.", 
                                "Type de fichier invalide", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, 
                    "Erreur lors de la lecture de fichier.", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        panel.add(new JScrollPane(fileDisplayArea), BorderLayout.CENTER);

        // Gestion des utilisateurs
        JLabel currentUserLabel = new JLabel("Utilisateur actuel : 0");
        currentUserLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.setBorder(BorderFactory.createTitledBorder("Gestion des utilisateurs"));

        JButton newUser = new JButton("Créer un utilisateur");
        JButton loadUser = new JButton("Charger un utilisateur existant");
        
        if(PKI.getUserId() < 2){
            loadUser.setEnabled(false);
        }

        newUser.addActionListener(evt -> {
            userId = PKI.getUserId();
            selectedUser = PKI.newUser();
            loadUser.setEnabled(true);
            currentUserLabel.setText("Utilisateur actuel : " + selectedUser);
            JOptionPane.showMessageDialog(frame,
                    "Utilisateur '" + selectedUser + "' créé avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        });

        loadUser.addActionListener(evt -> {
            String userInput = JOptionPane.showInputDialog(
                    userPanel,
                    "Entrez l'ID de l'utilisateur à charger entre 0 et " + Integer.toString(PKI.getUserId() - 1) + " :",
                    "Chargement d'utilisateur",
                    JOptionPane.QUESTION_MESSAGE);

            if (userInput == null || userInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "ID d'utilisateur invalide. Veuillez réessayer.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    userId = Integer.parseInt(userInput.trim());
                    int sup = PKI.getUserId() - 1;

                    if (userId <= sup && userId >= 0) {
                        selectedUser = userId;
                        currentUserLabel.setText("Utilisateur actuel : " + selectedUser);
                        JOptionPane.showMessageDialog(frame,
                                "Utilisateur avec ID " + userId + " chargé avec succès.",
                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "ID d'utilisateur invalide. Veuillez entrer un nombre entier entre 0 et " + sup,
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(frame,
                            "ID d'utilisateur invalide. Veuillez entrer un nombre entier.",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        userPanel.add(newUser);
        userPanel.add(loadUser);
        userPanel.add(currentUserLabel);

        // Configuration des fichiers
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
        filePanel.setBorder(BorderFactory.createTitledBorder("Gestion des fichiers"));

        // Bouton pour sélectionner un fichier
        JButton selectFileButton = new JButton("Sélectionner un fichier");

        selectFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().toLowerCase().endsWith(".pdf");
                }

                @Override
                public String getDescription() {
                    return "Fichiers PDF (*.pdf)";
                }
            });

            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".pdf")) {
                    selectedFile = file;
                    fileDisplayArea.setText("Fichier sélectionné : " + selectedFile.getAbsolutePath());
                    PDFInstance.setFile(selectedFile);
                    signButton.setEnabled(true);
                    verifyButton.setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "Seuls les fichiers JPG et PDF sont acceptés.", 
                        "Type de fichier invalide", 
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
1. Créez ou chargez un utilisateur (0 par défaut).

2. Glissez-déposez ou sélectionnez un fichier PDF.

3. Paramétrer le type de signature et de hachage.

4. Signez et vérifiez vos documents.

NB Signer modifie le fichier et écrase une signature existante.
""");


        
        JScrollPane scrollPane = new JScrollPane(tutorialArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tutoriel général"));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        filePanel.add(scrollPane);

        filePanel.add(Box.createVerticalStrut(10));

        // Opérations sur la signature
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Opérations de signature"));

        JComboBox<String> algoBox = new JComboBox<>(new String[] { "DSA", "RSA", "ECDSA" }); // "BLS"
        JComboBox<String> hashBox = new JComboBox<>(new String[] { "MD5", "SHA1", "SHA256" });


        signButton.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(frame,
                        "Aucun fichier sélectionné. Veuillez en sélectionner un avant de signer.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
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
                                    "Erreur d'algorithme de hachage : " + ex.getMessage(),
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        BigInteger[] keyPair = updateKey(selectedSignature,  signatureAlgorithm);
                        byte[] signature = signatureAlgorithm.sign(hashValue, keyPair);
                        PDFInstance.addMetadata("Signature", signature);

                        privateKey = null;
                        publicKey = null;

                        JOptionPane.showMessageDialog(frame,
                                "Fichier signé avec l'algorithme : " + algoBox.getSelectedItem(),
                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Algorithme de signature ou de hachage invalide", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException err) {
                    JOptionPane.showMessageDialog(frame, "Erreur lors de la lecture du fichier", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        verifyButton.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(frame,
                        "Aucun fichier sélectionné. Veuillez en sélectionner un avant de vérifier.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
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
                                    "Erreur d'algorithme de hachage : " + ex.getMessage(),
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        boolean isValid = signatureAlgorithm.verify(signature, hashValue, publicKey);
                        PDFInstance.addMetadata("Signature", signature);
                        
                        privateKey = null;
                        publicKey = null;

                        if(isValid){
                            
                            JOptionPane.showMessageDialog(frame,
                            "La signature est valide.",
                            "Erreur", JOptionPane.INFORMATION_MESSAGE);
    
                            }else{
                                JOptionPane.showMessageDialog(frame,
                                "La signature est invalide.",
                                "Succès", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Algorithme de signature ou de hachage invalide", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } catch (IOException err) {
                    JOptionPane.showMessageDialog(frame, "Erreur lors de la lecture du fichier", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(new JLabel("Type de signature :"));
        buttonPanel.add(algoBox);
        buttonPanel.add(new JLabel("Type de hashage :"));
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
