// Initialisation de la carte [Latitude, Longitude]
const map = L.map('map').setView([48.6921, 6.1844], 14);

// Ajout du fond de carte OpenStreetMap
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

// URLs des API de Nancy 
// Note pour la suite : Plus tard, remplacerer ces URLs par l'adresse 
// du proxy Java (ex: 'http://localhost:8080/api/velib')
const URL_INFO = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_information.json";
const URL_STATUS = "https://api.cyclocity.fr/contracts/nancy/gbfs/v2/station_status.json";

// Récupération et fusion des données
async function chargerStations() {
    try {
        // téléchargement des fichiers
        const [reponseInfo, reponseStatus] = await Promise.all([
            fetch(URL_INFO),
            fetch(URL_STATUS)
        ]);

        const dataInfo = await reponseInfo.json();
        const dataStatus = await reponseStatus.json();

        const stationsInfos = dataInfo.data.stations;
        const stationsStatus = dataStatus.data.stations;

        // On transforme le tableau en Map
        const statusMap = new Map(stationsStatus.map(s => [s.station_id, s]));

        // Boucle sur chaque station pour créer les marqueurs
        stationsInfos.forEach(info => {
            const status = statusMap.get(info.station_id);

            if (status) {
                const adresse = info.name; 
                const velosDispo = status.num_bikes_available;
                const placesLibres = status.num_docks_available;

                // Création du marqueur sur la carte
                const marker = L.marker([info.lat, info.lon]).addTo(map);

                // Contenu de la bulle d'info
                const popupContent = `
                    <div class="popup-station">
                        <h3>${adresse}</h3>
                        <p>🚲 <b>Vélos disponibles :</b> ${velosDispo}</p>
                        <p>🅿️ <b>Places libres :</b> ${placesLibres}</p>
                    </div>
                `;
                marker.bindPopup(popupContent);
            }
        });

    } catch (erreur) {
        console.error("Erreur lors de la récupération des données GBFS :", erreur);
    }
}

// Lancement du chargement des données au démarrage
chargerStations();