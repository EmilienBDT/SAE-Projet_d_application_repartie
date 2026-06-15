package fr.iutnc.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Classe de test permettant de vérifier le bon fonctionnement
 * du serveur RMI et l'accès à la base de données, sans avoir besoin
 * de lancer le proxy HTTP ou l'interface web.
 */

public class RMIClientTest {
    /**
     * Point d'entrée de l'application de test.
     * Se connecte au registre RMI, récupère le service 
     * via son nom public, puis invoque les méthodes de test pour l'affichage et la réservation.
     *
     * @param args 
     */
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(ConfigLoader.get("rmi.registry.port"));
            String serviceName = ConfigLoader.get("rmi.service.name");
            String serverIp = ConfigLoader.get("rmi.server.ip");

            // Connexion au registre local ou distant
            Registry registry = LocateRegistry.getRegistry(serverIp, port);
            RestaurantService service = (RestaurantService) registry.lookup(serviceName);

            System.out.println("--- Test : Récupération des restaurants ---");
            String jsonRestaurants = service.getRestaurants();
            System.out.println(jsonRestaurants);

            System.out.println("\n--- Test : Réservation ---");
            // Remplacer par un ID existant dans la BDD
            String jsonReservation = service.reserverTable(1, "Doe", "John", 2, "0601020304");
            System.out.println(jsonReservation);

        } catch (Exception e) {
            System.err.println("Erreur côté client RMI : " + e.getMessage());
            e.printStackTrace();
        }
    }
}