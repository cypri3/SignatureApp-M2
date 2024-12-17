import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import javax.swing.*;
import java.math.BigInteger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

public class Projet {

    private static File selectedFile = null;

    private static int selectedUser;
    private static BigInteger[] publicKey;
    private static BigInteger[] privateKey;
    private static int keyLength;
    private static int userId;

    private static Signatures selectSignatureAlgorithm(String selectedSignature) {
        switch (selectedSignature) {
            case "BLS":
                return new BLS01();
            case "DSA":
                return new DSA();
            case "RSA":
                return new RSA();
            case "ECDSA":
                return new ECDSA();
            default:
                JOptionPane.showMessageDialog(null, "Algorithme de signature inconnu", "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                return null;
        }
    }

    private static Hashs selectHashFunction(String selectedHash) {
        switch (selectedHash) {
            case "MD5":
                return new MD5();
            case "SHA1":
                return new SHA1();
            case "SHA256":
                return new SHA256();
            default:
                JOptionPane.showMessageDialog(null, "Algorithme de hash inconnu", "Erreur", JOptionPane.ERROR_MESSAGE);
                return null;
        }
    }

    public static void main(String[] args) {
        privateKey = new BigInteger[2];
        publicKey = new BigInteger[2];
        PDFdata PDFInstance = new PDFdata(selectedFile.getAbsolutePath());
        JFrame frame = new JFrame("Signature App - Gestion de la PKI");
        System.setProperty("file.encoding", "UTF-8");
        java.nio.charset.Charset.defaultCharset();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 600);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Zone d'affichage des fichiers
        JTextArea fileDisplayArea = new JTextArea("Aucun fichier sélectionné");
        fileDisplayArea.setEditable(false);
        fileDisplayArea.setLineWrap(true);
        fileDisplayArea.setWrapStyleWord(true);
        fileDisplayArea.setDropTarget(new DropTarget() {
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    java.util.List<File> files = (java.util.List<File>) evt.getTransferable()
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        selectedFile = files.get(0);
                        fileDisplayArea.setText("Fichier sélectionné : " + selectedFile.getAbsolutePath());
                        PDFdata PDFInstance = new PDFdata(selectedFile.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(new JScrollPane(fileDisplayArea), BorderLayout.CENTER);

        // Gestion des utilisateurs
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.setBorder(BorderFactory.createTitledBorder("Gestion des utilisateurs"));

        JButton newUser = new JButton("Créer un utilisateur");
        JButton loadUser = new JButton("Charger un utilisateur existant");

        newUser.addActionListener(evt -> {
            selectedUser = PKI.newUser();
            JOptionPane.showMessageDialog(frame,
                    "Utilisateur '" + selectedUser + "' créé avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        });

        loadUser.addActionListener(evt -> {
            String userInput = JOptionPane.showInputDialog(
                    userPanel,
                    "Entrez l'ID de l'utilisateur à charger :",
                    "Chargement d'utilisateur",
                    JOptionPane.QUESTION_MESSAGE);

            if (userInput == null || userInput.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "ID d'utilisateur invalide. Veuillez réessayer.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    int userId = Integer.parseInt(userInput.trim());
                    int sup = PKI.getUserId() - 1;

                    if (userId <= sup && userId >= 0) {
                        selectedUser = userId;
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

        // Configuration des fichiers
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.Y_AXIS));
        filePanel.setBorder(BorderFactory.createTitledBorder("Gestion des fichiers"));

        JButton selectFileButton = new JButton("Sélectionner un fichier");
        JButton downloadButton = new JButton(" Télécharger la sortie  ");
        downloadButton.setEnabled(false); // Désactivé par défaut

        selectFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                fileDisplayArea.setText("Fichier sélectionné : " + selectedFile.getAbsolutePath());
                downloadButton.setEnabled(true); // Activer le bouton après sélection
            }
        });

        downloadButton.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(frame,
                        "Aucun fichier traité. Veuillez effectuer une opération avant de télécharger.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JFileChooser saveChooser = new JFileChooser();
                saveChooser.setDialogTitle("Enregistrer la sortie");
                int result = saveChooser.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File outputFile = saveChooser.getSelectedFile();
                    try {
                        Files.copy(selectedFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(frame,
                                "Fichier enregistré avec succès à : " + outputFile.getAbsolutePath(),
                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame,
                                "Erreur lors de l'enregistrement.",
                                "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        filePanel.add(selectFileButton);
        filePanel.add(Box.createVerticalStrut(10));
        filePanel.add(downloadButton);

        // Opérations sur la signature
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Opérations de signature"));

        JComboBox<String> algoBox = new JComboBox<>(new String[] { "BLS", "DSA", "RSA", "ECDSA" });
        JComboBox<String> hashBox = new JComboBox<>(new String[] { "MD5", "SHA1", "SHA256" });
        JButton signButton = new JButton("Signer");
        JButton verifyButton = new JButton("Vérifier");

        signButton.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(frame,
                        "Aucun fichier sélectionné. Veuillez en sélectionner un avant de signer.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    byte[] pdfBytes = PDFdata.readPDFAsBytes(selectedFile);

                    String selectedSignature = (String) algoBox.getSelectedItem();
                    String selectedHash = (String) hashBox.getSelectedItem();

                    Signatures signatureAlgorithm = selectSignatureAlgorithm(selectedSignature);
                    Hashs hashFunction = selectHashFunction(selectedHash);

                    if (signatureAlgorithm != null && hashFunction != null) {
                        System.out.println("Algorithme de signature : " + selectedSignature);
                        System.out.println("Algorithme de hash : " + selectedHash);

                        byte[] hashValue = null;
                        try {
                            hashValue = hashFunction.hash(pdfBytes);
                        } catch (NoSuchAlgorithmException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(frame,
                                    "Erreur d'algorithme de hachage : " + ex.getMessage(),
                                    "Erreur", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        BigInteger[] publicKey = PKI.getPublicKey(userId, selectedSignature);
                        BigInteger[] privateKey = PKI.getPrivateKey(userId, selectedSignature);

                        BigInteger[] keyPair = new BigInteger[publicKey.length + privateKey.length];
                        System.arraycopy(publicKey, 0, keyPair, 0, publicKey.length);
                        System.arraycopy(privateKey, 0, keyPair, publicKey.length, privateKey.length);

                        if (keyPair.length == 0) {
                            keyPair = signatureAlgorithm.keyGen();
                            keyLength = keyPair.length;
                            PKI.newKeys(userId, selectedSignature, keyPair);
                        }

                        switch (keyLength) {
                            case 2:
                                privateKey[0] = keyPair[0];
                                publicKey[0] = keyPair[1];
                                break;
                            case 3:
                                privateKey[0] = keyPair[0];
                                publicKey[0] = keyPair[1];
                                publicKey[1] = keyPair[2];
                                break;
                            default:
                                privateKey[0] = keyPair[0];
                                privateKey[1] = keyPair[1];
                                publicKey[0] = keyPair[2];
                                publicKey[1] = keyPair[3];
                                break;
                        }
                        PKI.newKeys(userId, selectedSignature, keyPair);

                        byte[] signature = signatureAlgorithm.sign(hashValue, privateKey);
                        System.out.println("Signature générée : " + new BigInteger(1, signature).toString(16));

                        PDFInstance.addMetadata("Signature", signature.toString());

                        boolean isValid = signatureAlgorithm.verify(signature, hashValue, publicKey);
                        System.out.println("La signature est valide : " + isValid);

                        JOptionPane.showMessageDialog(frame,
                                "Fichier signé avec l'algorithme : " + algoBox.getSelectedItem(),
                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Algorithme de signature ou de hachage invalide", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException err) {
                    err.printStackTrace();
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
                JOptionPane.showMessageDialog(frame,
                        "Signature vérifiée avec succès pour le fichier sélectionné.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(new JLabel("Type de signature :"));
        buttonPanel.add(algoBox);
        buttonPanel.add(signButton);
        buttonPanel.add(verifyButton);
        buttonPanel.add(new JLabel("Type de hashage :"));
        buttonPanel.add(hashBox);

        // Ajout des panels au frame
        frame.add(panel, BorderLayout.CENTER);
        frame.add(userPanel, BorderLayout.NORTH);
        frame.add(filePanel, BorderLayout.WEST);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

}
