package fr.iutnc.proxy;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class IncidentHandler implements HttpHandler {
    private static final String API_URL = ConfigLoader.get("api.url");

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // En-têtes CORS obligatoires
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

        String responsePayload = "";
        int statusCode = 200;

        try {
            String useProxy = System.getProperty("useProxy", "false");
            HttpClient client;

            if ("true".equals(useProxy)) {
                ProxySelector proxyIut = ProxySelector.of(new InetSocketAddress("www-cache", 3128));
                client = HttpClient.newBuilder().proxy(proxyIut).build();
            } else {
                client = HttpClient.newBuilder().build();
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                responsePayload = response.body();
            } else {
                statusCode = response.statusCode();
                responsePayload = "{\"error\": \"Erreur du service Waze\"}";
            }

        } catch (InterruptedException e) {
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