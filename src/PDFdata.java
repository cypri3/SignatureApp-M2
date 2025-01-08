import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

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
            System.out.println("Defined file: " + file.getAbsolutePath());
        } else {
            System.out.println("Invalid or inexisted file.");
        }
    }
    
    @SuppressWarnings("CallToPrintStackTrace")
    public void addMetadata(String key, byte[] valueBytes) {
        if (this.file == null) {
            System.out.println("Error: No file has been defined. Please call the setFile() function with a valid file.");
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

    @SuppressWarnings("CallToPrintStackTrace")
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

    @SuppressWarnings("CallToPrintStackTrace")
    public void displayAllMetadata() {
        try (PDDocument document = PDDocument.load(file)) {
            PDDocumentInformation info = document.getDocumentInformation();
            System.out.println("File's metadata:");
            System.out.println("Title: " + info.getTitle());
            System.out.println("Author: " + info.getAuthor());
            System.out.println("Subject: " + info.getSubject());
            System.out.println("Creator: " + info.getCreator());
            System.out.println("Producer: " + info.getProducer());
            System.out.println("Signature: " + Arrays.toString(getMetadata("Signature")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
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
