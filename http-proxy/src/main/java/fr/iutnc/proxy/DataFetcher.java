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

/*
 * Classe utilitaire permettant de récupérer et de tester le traitement
 * des données depuis une API distante en gérant la configuration d'un proxy.
 */
public class DataFetcher {

    private static final String API_URL = ConfigLoader.get("api.url");

    /*
     * Exécute une requête GET vers l'API configurée pour récupérer les données.
     * Si la propriété système {@code useProxy} est définie à "true", un proxy
     * spécifique à l'IUT est appliqué à la connexion.
     * Parse ensuite le résultat JSON pour afficher la description des incidents.
     */
    public static void fetchDonnees() {
        // Configuration du proxy
        ProxySelector proxyIut = ProxySelector.of(new InetSocketAddress("www-cache", 3128));

        // Création du client HTTP avec le proxy intégré
        HttpClient client;

        // on applique le proxy s'il est demandé (machine IUT): mvn clean compile
        // exec:java -Dexec.args="-DuseProxy=true"
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

    /*
     * Méthode main pour tester la classe
     */
    public static void main(String[] args) {
        fetchDonnees();
    }
}
