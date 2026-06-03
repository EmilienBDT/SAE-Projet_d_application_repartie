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

import java.io.IOException;

// \http-proxy> mvn clean compile exec:java

public class DataFetcher {
    
    // à changer pour plus tard
    private static final String API_URL = "https://carto.g-ny.eu/data/cifs/cifs_waze_v2.json"; 

    public static void fetchDonnees() {
        

        

        // Configuration du proxy
        ProxySelector proxyIut = ProxySelector.of(new InetSocketAddress("www-cache", 3128));
        
        
        // Création du client HTTP avec le proxy intégré
        HttpClient client ;

        // on applique le proxy si il est damndé (machine IUT): mvn clean compile exec:java -Dexec.args="-DuseProxy=true"
        String useProxy = System.getProperty("useProxy");

        if ("true".equals(useProxy)) { 
            client = HttpClient.newBuilder().proxy(proxyIut)
                .build();
        } else {
            client = HttpClient.newBuilder()
                .build();
        }

        // Préparation de la requête GET
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .GET()
                .build();

        try {
            // Envoi de la requête synchrone 
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Test de la réception 
            if (response.statusCode() == 200) {
                System.out.println("Données téléchargées avec succès");
                
                String jsonBody = response.body();
                
                // Test du format des données en tentant de les parser 
                try {
                    JSONObject root = new JSONObject(jsonBody);
                    JSONArray incidents = root.getJSONArray("incidents");
                    // données exploitable
                    for (int i = 0; i < incidents.length(); i++) {
                        JSONObject item = incidents.getJSONObject(i);
                        System.out.println("Description : " + item.getString("short_description"));
                    }
                   
                    
                } catch (JSONException e) {
                    System.err.println("Erreur : Le contenu reçu n'est pas un JSON valide.");
                    e.printStackTrace();
                }

            } else {
                System.err.println("Erreur du serveur distant : " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            // Gestion des erreurs du réseau 
            System.err.println("Erreur réseau lors de la tentative de connexion :");
            e.printStackTrace();
        }
    }

    // Méthode main pour tester la classe
    public static void main(String[] args) {
        fetchDonnees();
    }
}
