import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PDFdata {
    private File file;

    public PDFdata(String filePath) {
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

    public static byte[] readPDFAsBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            return fileBytes;
        }
    }
}
