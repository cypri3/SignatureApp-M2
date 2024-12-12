public class Main2 {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Veuillez spécifier le chemin du fichier PDF.");
            return;
        }

        String filePath = args[0];
        PDFdata manager = new PDFdata(filePath);
        manager.addMetadata("Signature", "SignatureTest12345");
        String signature = manager.getMetadata("Signature");
        System.out.println("Signature récupérée : " + signature);
        manager.displayAllMetadata();

    }
}
