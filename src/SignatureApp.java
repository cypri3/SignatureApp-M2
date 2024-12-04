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
        JFrame frame = new JFrame("Signature App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

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

        // Config signature
        JPanel paramsPanel = new JPanel();
        paramsPanel.setLayout(new FlowLayout());
        JComboBox<String> algoBox = new JComboBox<>(new String[] { "BLS", "RSA", "DSA", "ECDSA" });
        paramsPanel.add(new JLabel("Type de signature :"));
        paramsPanel.add(algoBox);

        // selection fichier
        JButton selectFileButton = new JButton("Sélectionner un fichier");
        selectFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                fileDisplayArea.setText("Fichier sélectionné : " + selectedFile.getAbsolutePath());
            }
        });
        paramsPanel.add(selectFileButton);

        // Boutons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton signButton = new JButton("Signer");
        JButton verifyButton = new JButton("Vérifier");
        JButton downloadButton = new JButton("Télécharger la sortie");

        signButton.addActionListener(e -> {
            if (selectedFile == null) {

                JOptionPane.showMessageDialog(frame, "Aucun fichier sélectionné.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Fichier signé avec l'algorithme : " + algoBox.getSelectedItem(),
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        verifyButton.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(frame, "Aucun fichier sélectionné.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Signature vérifiée avec succès pour le fichier sélectionné.",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        downloadButton.addActionListener(e -> {
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(frame, "Aucun fichier traité.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } else {
                JFileChooser saveChooser = new JFileChooser();
                saveChooser.setDialogTitle("Enregistrer la sortie");
                int result = saveChooser.showSaveDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File outputFile = saveChooser.getSelectedFile();
                    try {
                        // TODO Mettre le vrai fichier
                        Files.copy(selectedFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(frame,
                                "Fichier enregistré avec succès à : " + outputFile.getAbsolutePath(),
                                "Succès", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Erreur lors de l'enregistrement.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        buttonPanel.add(signButton);
        buttonPanel.add(verifyButton);
        buttonPanel.add(downloadButton);

        // Panels
        frame.add(panel, BorderLayout.CENTER);
        frame.add(paramsPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
}
