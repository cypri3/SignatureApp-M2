import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PDFMetadataManager {

    private File file;

    public PDFMetadataManager(String filePath) {
        this.file = new File(filePath);
    }

    public void addMetadata(String key, String value) {
        try (PDDocument document = PDDocument.load(file)) {
            PDDocumentInformation info = document.getDocumentInformation();
            info.setCustomMetadataValue(key, value);
            document.setDocumentInformation(info);
            document.save(file);
            System.out.println("Mise à jour des métadonnées réussie !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMetadata(String key) {
        try (PDDocument document = PDDocument.load(file)) {
            PDDocumentInformation info = document.getDocumentInformation();
            String value = info.getCustomMetadataValue(key);
            if (value != null) {
                return value;
            } else {
                return "Aucune donnée trouvée pour cette clé";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void displayAllMetadata() {
        try (PDDocument document = PDDocument.load(file)) {
            PDDocumentInformation info = document.getDocumentInformation();
            System.out.println("Métadonnées du document :");
            System.out.println("Titre: " + info.getTitle());
            System.out.println("Auteur: " + info.getAuthor());
            System.out.println("Sujet: " + info.getSubject());
            System.out.println("Créateur: " + info.getCreator());
            System.out.println("Producteur: " + info.getProducer());
            System.out.println("Signature: " + getMetadata("Signature"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeMetadata(String key) {
        try (PDDocument document = PDDocument.load(file)) {
            PDDocumentInformation info = document.getDocumentInformation();
            info.setCustomMetadataValue(key, null);
            document.setDocumentInformation(info);
            document.save(file);
            System.out.println("Métadonnée supprimée avec succès !");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO A supprimer
    private static byte[] SHA1(byte[] message) throws NoSuchAlgorithmException {

        MessageDigest messageHash = MessageDigest.getInstance("SHA-1");
        messageHash.update(message);
        byte[] hash = messageHash.digest();

        return hash;
    }

    public static byte[] readPDFAsBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            return fileBytes;
        }
    }

    public static void main(String[] args) {
        /*
         * PDFMetadataManager manager = new
         * PDFMetadataManager("tests/example_with_metadata.pdf");
         * PDFMetadataManager manager2 = new PDFMetadataManager("tests/example.pdf");
         * 
         * manager.addMetadata("Signature", "SignatureTest12345");
         * String signature = manager.getMetadata("Signature");
         * System.out.println("Signature récupérée : " + signature);
         * manager.displayAllMetadata();
         * 
         * manager.removeMetadata("Signature");
         * manager.displayAllMetadata();
         * 
         * System.out.println(manager == manager2);
         */
        try {
            // Charger le document PDF
            File inputFile = new File("tests/example_with_metadata.pdf");
            PDDocument document = PDDocument.load(inputFile);

            // Créer un nouvel objet d'informations vide
            PDDocumentInformation emptyMetadata = new PDDocumentInformation();
            document.setDocumentInformation(emptyMetadata);

            // Supprimer également les métadonnées XMP si elles existent
            document.getDocumentCatalog().setMetadata(null);

            // Sauvegarder le document sans métadonnées
            File outputFile = new File("tests/example_without_metadata.pdf");
            document.save(outputFile);

            document.close();

            System.out.println("Les métadonnées ont été supprimées avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // Charger le document PDF
            File inputFile = new File("tests/example.pdf");
            PDDocument document = PDDocument.load(inputFile);

            // Créer un nouvel objet d'informations vide
            PDDocumentInformation emptyMetadata = new PDDocumentInformation();
            document.setDocumentInformation(emptyMetadata);

            // Supprimer également les métadonnées XMP si elles existent
            document.getDocumentCatalog().setMetadata(null);

            // Sauvegarder le document sans métadonnées
            File outputFile = new File("tests/example_without.pdf");
            document.save(outputFile);

            document.close();

            System.out.println("Les métadonnées ont été supprimées avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String file1Path = "tests/example_without_metadata.pdf";
        String file2Path = "tests/example_without.pdf";

        try (FileInputStream fis1 = new FileInputStream(file1Path);
                FileInputStream fis2 = new FileInputStream(file2Path)) {

            int byte1, byte2;
            boolean areEqual = true;

            while ((byte1 = fis1.read()) != -1 && (byte2 = fis2.read()) != -1) {
                if (byte1 != byte2) {
                    areEqual = false;
                    break;
                }
            }

            // Vérification si les fichiers ont la même taille
            if (areEqual && (fis1.read() == -1) && (fis2.read() == -1)) {
                System.out.println("Les fichiers PDF sont identiques.");
            } else {
                System.out.println("Les fichiers PDF sont différents.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File inputFile = new File("tests/example_with_metadata.pdf");
            byte[] fileBytes = readPDFAsBytes(inputFile);
            byte[] defaultHash = SHA1(fileBytes);

            // Afficher le hash en hexadécimal
            StringBuilder hexHash = new StringBuilder();
            for (byte b : defaultHash) {
                hexHash.append(String.format("%02x", b));
            }
            System.out.println("SHA-1 du fichier : " + hexHash);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
}