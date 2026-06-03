package fr.iutnc.proxy;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class ProxyServer {
    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(ConfigLoader.get("proxy.port"));
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            
            // Définition des routes de l'API
            server.createContext("/api/incidents", new IncidentHandler());
            server.createContext("/api/restaurants", new RestaurantHandler());
            server.createContext("/api/reserver", new ReservationHandler());
            
            server.setExecutor(null);
            server.start();
            System.out.println("Proxy HTTP démarré et en écoute sur le port " + port);
        } catch (IOException e) {
            System.err.println("Erreur lors du démarrage du Proxy : " + e.getMessage());
            e.printStackTrace();
        }
    }
}