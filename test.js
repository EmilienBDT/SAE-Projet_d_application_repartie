const map = L.map('map').setView([48.6921, 6.1844], 14);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
}).addTo(map);

// Récupération des conteneurs de la barre latérale
const velibListDiv = document.getElementById('velib-list');
const restaurantListDiv = document.getElementById('restaurant-list');

async function chargerStations() {
    try {
        // Utilisation de la configuration centralisée
        const [reponseInfo, reponseStatus] = await Promise.all([
            fetch(CONFIG.URL_INFO),
            fetch(CONFIG.URL_STATUS)
        ]);

        const dataInfo = await reponseInfo.json();
        const dataStatus = await reponseStatus.json();
        const statusMap = new Map(dataStatus.data.stations.map(s => [s.station_id, s]));

        dataInfo.data.stations.forEach(info => {
            const status = statusMap.get(info.station_id);
            if (status) {
                // Ajout sur la carte
                const marker = L.marker([info.lat, info.lon]).addTo(map);
                marker.bindPopup(`<b>${info.name}</b><br>Vélos: ${status.num_bikes_available}<br>Places: ${status.num_docks_available}`);
                
                // Ajout dans la liste verticale
                velibListDiv.innerHTML += `
                    <div class="list-item">
                        <h4>🚲 ${info.name}</h4>
                        <p>${status.num_bikes_available} vélos | ${status.num_docks_available} places</p>
                    </div>
                `;
            }
        });
    } catch (erreur) { console.error("Erreur Vélibs :", erreur); }
}

async function chargerIncidents() {
    try {
        const reponse = await fetch(`${CONFIG.PROXY_URL}/incidents`);
        const data = await reponse.json();

        data.incidents.forEach(incident => {
            const coords = incident.location.polyline.split(" ");
            const lon = parseFloat(coords[0]);
            const lat = parseFloat(coords[1]);

            const incidentIcon = L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-orange.png',
                iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34]
            });

            const marker = L.marker([lat, lon], { icon: incidentIcon }).addTo(map);
            marker.bindPopup(`<b>🚧 Incident</b><br>${incident.short_description}`);
        });
    } catch (erreur) { console.error("Erreur Waze :", erreur); }
}

async function chargerRestaurants() {
    try {
        const reponse = await fetch(`${CONFIG.PROXY_URL}/restaurants`);
        const restaurants = await reponse.json();

        restaurants.forEach(resto => {
            const restoIcon = L.icon({
                iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
                iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34]
            });

            const marker = L.marker([resto.coordonnees.lat, resto.coordonnees.lng], { icon: restoIcon }).addTo(map);
            marker.bindPopup(`
                <div class="popup-station">
                    <h3>${resto.nom}</h3>
                    <input type="text" id="nom_${resto.id}" placeholder="Nom">
                    <input type="text" id="prenom_${resto.id}" placeholder="Prénom">
                    <input type="number" id="convives_${resto.id}" placeholder="Convives">
                    <input type="text" id="tel_${resto.id}" placeholder="Téléphone">
                    <button onclick="reserver(${resto.id})">Réserver</button>
                    <p id="msg_${resto.id}"></p>
                </div>
            `);

            // Ajout dans la liste verticale
            restaurantListDiv.innerHTML += `
                <div class="list-item">
                    <h4>🍽️ ${resto.nom}</h4>
                    <p>${resto.adresse}</p>
                </div>
            `;
        });
    } catch (erreur) { console.error("Erreur Restaurants :", erreur); }
}

async function reserver(id) {
    // ... Logique de réservation inchangée, pensez juste à utiliser CONFIG.PROXY_URL dans le fetch
    const data = { /* ... */ };
    try {
        const reponse = await fetch(`${CONFIG.PROXY_URL}/reserver`, { /* ... */ });
        // ...
    } catch (erreur) { /* ... */ }
}

chargerStations();
chargerIncidents();
chargerRestaurants();