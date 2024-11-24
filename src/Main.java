import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Main {
    public static void main(String[] args) {
        try {
            // Charger les paramètres de courbe depuis un fichier
            Pairing pairing = PairingFactory.getPairing("src/a.properties");

            // Générer les paramètres du système
            Element g = pairing.getG1().newRandomElement().getImmutable();

            // Générer une clé secrète
            Element x = pairing.getZr().newRandomElement();

            // Générer la clé publique correspondante
            Element pk = g.powZn(x);

            System.out.println("Clé secrète : " + x);
            System.out.println("Clé publique : " + pk);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
