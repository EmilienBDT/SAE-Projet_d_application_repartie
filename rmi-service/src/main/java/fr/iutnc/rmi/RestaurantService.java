package fr.iutnc.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

//Service distant permettant la gestion et la consultation des restaurants.
public interface RestaurantService extends Remote {
    
    /**
     * Récupère la liste de tous les restaurants avec leurs coordonnées géographiques.
     * @return Une chaîne de caractères contenant un tableau JSON d'objets restaurant.
     * @throws RemoteException En cas d'erreur de communication réseau ou de base de données.
     */
    String getRestaurants() throws RemoteException;

    /**
     * Enregistre une nouvelle réservation pour un restaurant spécifique.
     * @param restaurantId L'identifiant unique du restaurant.
     * @param nom Le nom de famille de la personne réservant la table.
     * @param prenom Le prénom de la personne.
     * @param nbConvives Le nombre total de personnes attendues.
     * @param telephone Le numéro de contact.
     * @return Une chaîne de caractères contenant un objet JSON avec le statut (success/error) et un message.
     * @throws RemoteException En cas d'erreur réseau ou si l'identifiant du restaurant n'existe pas.
     */
    String reserverTable(int restaurantId, String nom, String prenom, int nbConvives, String telephone) throws RemoteException;
}