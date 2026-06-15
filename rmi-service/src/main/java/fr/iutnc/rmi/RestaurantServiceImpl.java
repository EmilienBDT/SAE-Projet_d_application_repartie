package fr.iutnc.rmi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Implémentation du service distant {@link RestaurantService}.
 * Cette classe exécute les requêtes SQL vers la base de données 
 * et formate les résultats en JSON avant de les renvoyer au client RMI.
 */
public class RestaurantServiceImpl extends UnicastRemoteObject implements RestaurantService {

    /**
     * Constructeur par défaut.
     * Exporte l'objet pour le rendre disponible aux appels RMI.
     *
     * @throws RemoteException Si l'exportation de l'objet distant échoue
     */
    public RestaurantServiceImpl() throws RemoteException {
        super();
    }

    /**
     * {@inheritDoc}
     * Interroge la table {@code restaurants} de la base de données
     */
    @Override
    public String getRestaurants() throws RemoteException {
        JSONArray jsonArray = new JSONArray();
        String query = "SELECT id, nom, adresse, latitude, longitude FROM restaurants";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("id"));
                obj.put("nom", rs.getString("nom"));
                obj.put("adresse", rs.getString("adresse"));

                JSONObject coord = new JSONObject();
                coord.put("lat", rs.getDouble("latitude"));
                coord.put("lng", rs.getDouble("longitude"));
                obj.put("coordonnees", coord);

                jsonArray.put(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Erreur BDD lors de la récupération des restaurants", e);
        }
        return jsonArray.toString();
    }

    /**
     * {@inheritDoc}
     * Insère une nouvelle entrée dans la table {@code reservations}
     */
    @Override
    public String reserverTable(int restaurantId, String nom, String prenom, int nbConvives, String telephone)
            throws RemoteException {
        String query = "INSERT INTO reservations (id, restaurant_id, nom, prenom, nb_convives, telephone) VALUES (seq_reservations.NEXTVAL, ?, ?, ?, ?, ?)";
        JSONObject response = new JSONObject();

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, restaurantId);
            stmt.setString(2, nom);
            stmt.setString(3, prenom);
            stmt.setInt(4, nbConvives);
            stmt.setString(5, telephone);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                response.put("status", "success");
                response.put("message", "Réservation confirmée pour " + prenom + " " + nom);
            } else {
                response.put("status", "error");
                response.put("message", "Échec de la réservation.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response.toString();
    }
}