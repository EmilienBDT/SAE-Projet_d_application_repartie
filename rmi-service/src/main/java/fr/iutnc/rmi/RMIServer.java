package fr.iutnc.rmi.server;

import fr.iutnc.rmi.core.RestaurantService;
import fr.iutnc.rmi.impl.RestaurantServiceImpl;
import fr.iutnc.rmi.utils.ConfigLoader;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class RMIServer {
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(ConfigLoader.get("rmi.registry.port"));
            String serviceName = ConfigLoader.get("rmi.service.name");

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