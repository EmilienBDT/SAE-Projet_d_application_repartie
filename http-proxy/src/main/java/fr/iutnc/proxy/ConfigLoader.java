package fr.iutnc.proxy;

import java.io.InputStream;
import java.util.Properties;

/*
 * Classe chargée de lire et de fournir les paramètres de configuration
 * définis dans le fichier {@code config.properties}.
 */
public class ConfigLoader {
    private static Properties properties = new Properties();

    /*
     * Tente de lire le fichier config.properties.
     */
    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null)
                throw new RuntimeException("Fichier config.properties introuvable");
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Récupère la valeur d'une propriété de configuration à partir de sa clé
     *
     * @param key La clé de la propriété recherchée 
     * @return La valeur associée à la clé
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }
}