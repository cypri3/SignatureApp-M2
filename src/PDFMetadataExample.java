import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import java.io.File;
import java.io.IOException;

public class PDFMetadataExample {
    public static void main(String[] args) {
        System.out.println("Hello, PDF!");

        File inputFile = new File("tests/example.pdf");

        // Chargement du fichier PDF
        try (PDDocument document = PDDocument.load(inputFile)) {
            // Récupérer et modifier les métadonnées
            PDDocumentInformation info = document.getDocumentInformation();
            info.setCustomMetadataValue("Signature", "SignatureTest12345");
            document.setDocumentInformation(info);

            // Sauvegarder le fichier avec les nouvelles métadonnées
            document.save("tests/example_with_metadata.pdf");
            System.out.println("Mise à jour des métadonnées réussie !");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Lecture des métadonnées
        try (PDDocument document = PDDocument.load(new File("tests/example_with_metadata.pdf"))) {
            PDDocumentInformation info = document.getDocumentInformation();
            String signature = info.getCustomMetadataValue("Signature");
            System.out.println("Signature récupérée : " + signature);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PDDocument document = PDDocument.load(new File("tests/example.pdf"))) {
            PDDocumentInformation info = document.getDocumentInformation();
            String signature = info.getCustomMetadataValue("Signature");
            System.out.println("Signature récupérée : " + signature);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
