import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import java.util.Base64;

public class PDFdata {
    private File file;

    public PDFdata() {
    }

    public void setFile(File file) {
        if (file != null && file.exists()) {
            this.file = file;
            System.out.println("Fichier défini : " + file.getAbsolutePath());
        } else {
            System.out.println("Fichier invalide ou inexistant.");
        }
    }
    

    public void addMetadata(String key, byte[] valueBytes) {
        if (this.file == null) {
            System.out.println("Erreur : Aucun fichier n'a été défini. Veuillez appeler setFile() avec un fichier valide.");
            return;
        }
    
        try (PDDocument document = PDDocument.load(file)) {
            String base64Value = Base64.getEncoder().encodeToString(valueBytes);
            PDDocumentInformation info = document.getDocumentInformation();
            info.setCustomMetadataValue(key, base64Value);
            document.setDocumentInformation(info);
            document.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getMetadata(String key) {
        try (PDDocument document = PDDocument.load(file)) {
            PDDocumentInformation info = document.getDocumentInformation();
            String base64Value = info.getCustomMetadataValue(key);
            if (base64Value != null) {
                return Base64.getDecoder().decode(base64Value);
            } else {
                return new byte[0];
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
