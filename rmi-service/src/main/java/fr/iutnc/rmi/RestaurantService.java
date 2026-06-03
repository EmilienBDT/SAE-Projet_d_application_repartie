package fr.iutnc.rmi.core;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RestaurantService extends Remote {
    String getRestaurants() throws RemoteException;
    String reserverTable(int restaurantId, String nom, String prenom, int nbConvives, String telephone) throws RemoteException;
}