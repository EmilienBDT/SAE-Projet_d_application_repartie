package fr.iutnc.proxy;

import java.net.URI;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public class IncidentHandler implements HttpHandler {

    // à changer pour plus tard
    private static final String API_URL = "https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json";

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // En-têtes
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        String responsePayload = "";
        int statusCode = 200;
        try {
            // Configuration du proxy et création du client HTTP

            String useProxy = System.getProperty("useProxy", "false");
            HttpClient client;

            // on applique le proxy si il est demandé (machine IUT)

            if ("true".equals(useProxy)) {
                ProxySelector proxyIut = ProxySelector.of(new InetSocketAddress("www-cache", 3128));
                client = HttpClient.newBuilder().proxy(proxyIut).build();
            } else {
                client = HttpClient.newBuilder().build();
            }

            // Préparation de la requête GET
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();
            // Envoi de la requête synchrone
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Test de la réception
            if (response.statusCode() == 200) {
                System.out.println("Données téléchargées avec succès");

                String jsonBody = response.body();

            } else {
                statusCode = response.statusCode();
                responsePayload = "{\"error\": \"Erreur du service Waze\"}";
            }
            

        } catch (IOException | InterruptedException e) {
            statusCode = 500;
            responsePayload = "{\"error\": \"Erreur réseau interne\"}";
        }

        byte[] bytes = responsePayload.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

}
    
