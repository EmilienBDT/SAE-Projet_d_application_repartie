// Initialisation de la carte [Latitude, Longitude]
const map = L.map('map').setView([48.6921, 6.1844], 14);

// Ajout du fond de carte OpenStreetMap
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

// URLs des API de Nancy 
// Note pour la suite : Plus tard, remplacerer ces URLs par l'adresse 
const PROXY_URL = "http://localhost:8080/api";
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
        console.error("Erreur Vélibs :", erreur);
    }
}

// Chargement des Incidents Waze via Proxy
async function chargerIncidents() {
    try {

        // à modifier
        const reponse = await fetch(`${PROXY_URL}/incidents`);
        const data = await reponse.json();

        data.incidents.forEach(incident => {
            // Waze fournit souvent les coordonnées sous forme de ligne 
            const coords = incident.location.polyline.split(" ");
            const lon = parseFloat(coords[0]);
            const lat = parseFloat(coords[1]);

            // Icône orange pour différencier les travaux
            const incidentIcon = L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-orange.png',
                iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34]
            });

            const marker = L.marker([lat, lon], { icon: incidentIcon }).addTo(map);
            marker.bindPopup(`<b>🚧 Incident</b><br>${incident.short_description}<br>Début: ${incident.starttime}`);
        });
    } catch (erreur) { console.error("Erreur Waze :", erreur); }
}


// Chargement des Restaurants via Proxy

async function chargerRestaurants() {
    try {
        const reponse = await fetch(`${PROXY_URL}/restaurants`);
        const restaurants = await reponse.json();

        restaurants.forEach(resto => {
            const restoIcon = L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
                iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34]
            });

            const marker = L.marker([resto.coordonnees.lat, resto.coordonnees.lng], { icon: restoIcon }).addTo(map);

            const formHTML = `
                <div class="popup-station">
                    <h3>🍽️ ${resto.nom}</h3>
                    <p>${resto.adresse}</p>
                    <hr>
                    <b>Réserver une table :</b><br>
                    <input type="text" id="nom_${resto.id}" placeholder="Nom" size="10"><br>
                    <input type="text" id="prenom_${resto.id}" placeholder="Prénom" size="10"><br>
                    <input type="number" id="convives_${resto.id}" placeholder="Convives" style="width: 50px;"><br>
                    <input type="text" id="tel_${resto.id}" placeholder="Téléphone" size="10"><br>
                    <button onclick="reserver(${resto.id})" style="margin-top: 5px; width: 100%;">Confirmer</button>
                    <p id="msg_${resto.id}" style="color: green; font-weight: bold;"></p>
                </div>
            `;
            marker.bindPopup(formHTML);
        });
    } catch (erreur) { console.error("Erreur Restaurants :", erreur); }
}

// Action de Réservation (POST vers le Proxy)
async function reserver(id) {
    const data = {
        restaurantId: id,
        nom: document.getElementById(`nom_${id}`).value,
        prenom: document.getElementById(`prenom_${id}`).value,
        nbConvives: parseInt(document.getElementById(`convives_${id}`).value),
        telephone: document.getElementById(`tel_${id}`).value
    };

    try {
        const reponse = await fetch(`${PROXY_URL}/reserver`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        const resultat = await reponse.json();
        const msgElement = document.getElementById(`msg_${id}`);
        msgElement.innerText = resultat.message;
        msgElement.style.color = resultat.status === "success" ? "green" : "red";
    } catch (erreur) {
        document.getElementById(`msg_${id}`).innerText = "Erreur de connexion";
    }
}


// Lancement du chargement des données au démarrage

chargerStations();
chargerIncidents();
chargerRestaurants();