package fr.iutnc.proxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.iutnc.rmi.RestaurantService;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Handler chargé de traiter les requêtes demandant la liste des restaurants.
 * Il interroge le registre RMI pour récupérer ces informations depuis le service backend.
*/

public class RestaurantHandler implements HttpHandler {
    private static final String RMI_HOST = ConfigLoader.get("rmi.server.ip");
    private static final int RMI_PORT = Integer.parseInt(ConfigLoader.get("rmi.registry.port"));


    /**
     * Traite la requête HTTP entrante 
     * 
     * @param exchange L'objet encapsulant la requête HTTP reçue et la réponse à envoyer
     * @throws IOException Si une erreur d'entrée/sortie survient
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        String responsePayload = "";
        int statusCode = 200;

        try {
            Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            RestaurantService service = (RestaurantService) registry.lookup("RestaurantService");
            responsePayload = service.getRestaurants();
        } catch (Exception e) {
            statusCode = 500;
            responsePayload = "{\"error\": \"Erreur de communication RMI\"}";
            e.printStackTrace();
        }

        byte[] bytes = responsePayload.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}