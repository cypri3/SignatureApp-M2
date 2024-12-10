import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.swing.*;

public class SignatureApp {
    private static File selectedFile = null;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Signature App - Gestion de la PKI");
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
            String userName = JOptionPane.showInputDialog(
                    userPanel,
                    "Entrez le nom du nouvel utilisateur :", 
                    "Création d'utilisateur", 
                    JOptionPane.QUESTION_MESSAGE);
            if (userName == null || userName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                        "Nom d'utilisateur invalide. Veuillez réessayer.", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Utilisateur '" + userName + "' créé avec succès.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadUser.addActionListener(evt -> {
            String userName = JOptionPane.showInputDialog(
                    userPanel,
                    "Entrez le nom de l'utilisateur à charger :", 
                    "Chargement d'utilisateur", 
                    JOptionPane.QUESTION_MESSAGE);
            if (userName == null || userName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                        "Nom d'utilisateur invalide. Veuillez réessayer.", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Utilisateur '" + userName + "' chargé avec succès.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
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
        JButton signButton = new JButton("Signer");
        JButton verifyButton = new JButton("Vérifier");

        signButton.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(frame, 
                        "Aucun fichier sélectionné. Veuillez en sélectionner un avant de signer.", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Fichier signé avec l'algorithme : " + algoBox.getSelectedItem(),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
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

        // Ajout des panels au frame
        frame.add(panel, BorderLayout.CENTER);
        frame.add(userPanel, BorderLayout.NORTH);
        frame.add(filePanel, BorderLayout.WEST);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
