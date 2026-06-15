package fr.iutnc.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

/**
 * Classe principale, initialise le serveur RMI.
 * Elle publie le service d'accès aux données pour qu'il soit joignable
 * par d'autres processus.
 */

public class RMIServer {

    /**
     * Point d'entrée du serveur.
     * Crée le registre RMI sur le port spécifié dans la configuration,
     * instancie l'implémentation du service {@link RestaurantServiceImpl}
     * et la lie au registre sous un nom précis.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(ConfigLoader.get("rmi.registry.port"));
            String serviceName = ConfigLoader.get("rmi.service.name");
            // Création du registre RMI local
            Registry registry = LocateRegistry.createRegistry(port);
            RestaurantService service = new RestaurantServiceImpl();
            registry.rebind(serviceName, service);

            System.out.println("Serveur RMI prêt et en écoute sur le port " + port);
            System.out.println("Service enregistré sous le nom : " + serviceName);

            System.out.println("Appuyez sur la touche 'Entrée' pour arrêter le serveur...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();

            System.out.println("Arrêt du serveur RMI.");
            System.exit(0);

        } catch (Exception e) {
            System.err.println("Erreur au démarrage du serveur RMI : " + e.getMessage());
            e.printStackTrace();
        }
    }
}