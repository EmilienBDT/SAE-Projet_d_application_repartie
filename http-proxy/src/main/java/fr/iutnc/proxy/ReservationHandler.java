package fr.iutnc.proxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import fr.iutnc.rmi.RestaurantService;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


/**
 * Handler chargé de traiter les requêtes de réservation.
 * Il réceptionne les données JSON envoyées en POST par le client, les parse,
 * et transmet la demande au service distant via RMI.
 */

public class ReservationHandler implements HttpHandler {
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
        // Configuration CORS pour autoriser les requêtes POST depuis webetu
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // Réponse immédiate pour la requête de pré-vérification (Preflight) CORS
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String responsePayload = "";
        int statusCode = 200;

        try {
            // Lecture du flux JSON envoyé par le client JS
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject jsonReq = new JSONObject(body);

            int restaurantId = jsonReq.getInt("restaurantId");
            String nom = jsonReq.getString("nom");
            String prenom = jsonReq.getString("prenom");
            int nbConvives = jsonReq.getInt("nbConvives");
            String telephone = jsonReq.getString("telephone");

            Registry registry = LocateRegistry.getRegistry(RMI_HOST, RMI_PORT);
            RestaurantService service = (RestaurantService) registry.lookup("RestaurantService");

            responsePayload = service.reserverTable(restaurantId, nom, prenom, nbConvives, telephone);

        } catch (Exception e) {
            statusCode = 500;
            responsePayload = "{\"status\": \"error\", \"message\": \"Erreur lors de la réservation.\"}";
            e.printStackTrace();
        }

        byte[] bytes = responsePayload.getBytes("UTF-8");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}