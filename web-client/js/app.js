// Initialisation de la carte [Latitude, Longitude]
const map = L.map('map', {zoomControl: false}).setView([48.6921, 6.1844], 14);
L.control.zoom({ position: 'bottomright' }).addTo(map);

// Ajout du fond de carte OpenStreetMap
L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
}).addTo(map);

// État global de l'application
const state = {
    filter: null,
    search: '',
    data: {
        restaurants: [],
        velos: [],
        incidents: []
    }
};

// Couches Leaflet par catégorie
const layers = {
    restaurants: L.layerGroup().addTo(map),
    velos: L.layerGroup().addTo(map),
    incidents: L.layerGroup().addTo(map)
};

// Définition des icônes
const icons = {
    incident: L.icon({ iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-orange.png', iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34] }),
    restaurant: L.icon({ iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png', iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34] }),
    velo: L.icon({ iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png', iconSize: [25, 41], iconAnchor: [12, 41], popupAnchor: [1, -34] })
};

// Fonction principale de rendu
function render() {
    const resultsBox = document.getElementById('results-box');
    resultsBox.innerHTML = '';

    layers.restaurants.clearLayers();
    layers.velos.clearLayers();
    layers.incidents.clearLayers();

    const searchLower = state.search.toLowerCase();
    
    const showRestaurants = (state.filter === null || state.filter === 'restaurants');
    const showVelos = (state.filter === null || state.filter === 'velos');
    const showIncidents = (state.filter === null || state.filter === 'incidents');

    let resultatsTrouves = 0;

    // Rendu Restaurants
    if (showRestaurants) {
        state.data.restaurants.forEach(resto => {
            if (resto.nom.toLowerCase().includes(searchLower) || resto.adresse.toLowerCase().includes(searchLower)) {
                resultatsTrouves++;
                
                const marker = L.marker([resto.coordonnees.lat, resto.coordonnees.lng], { icon: icons.restaurant });
                const formHTML = `
                    <div id="view_${resto.id}" class="popup-station">
                        <h3>${resto.nom}</h3>
                        <p>${resto.adresse}</p>
                        <button class="btn-reserver" style="margin-top: 10px;" onclick="document.getElementById('view_${resto.id}').style.display='none'; document.getElementById('form_${resto.id}').style.display='block';">Réserver une table</button>
                    </div>
                    <div id="form_${resto.id}" class="popup-station" style="display:none;">
                        <h3>Réserver une table :</h3>
                        <input type="text" id="nom_${resto.id}" placeholder="Nom"><br>
                        <input type="text" id="prenom_${resto.id}" placeholder="Prénom"><br>
                        <input type="number" id="convives_${resto.id}" placeholder="Convives" min="1"><br>
                        <input type="text" id="tel_${resto.id}" placeholder="Téléphone"><br>
                        <button class="btn-reserver" onclick="reserver(${resto.id})">Confirmer</button>
                        <p id="msg_${resto.id}" style="color: green; font-weight: bold; font-size:12px; margin-top:5px;"></p>
                    </div>
                `;
                marker.bindPopup(formHTML);
                layers.restaurants.addLayer(marker);

                resultsBox.innerHTML += `
                    <div class="result-card resto-card">
                        <div class="clickable-card" onclick="focusOnMap(${resto.coordonnees.lat}, ${resto.coordonnees.lng}, 'restaurants')">
                            <div class="result-title">${resto.nom}</div>
                            <div class="result-address">${resto.adresse}</div>
                        </div>
                        <button class="btn-reserver" onclick="focusOnMap(${resto.coordonnees.lat}, ${resto.coordonnees.lng}, 'restaurants')">Réserver</button>
                    </div>
                `;
            }
        });
    }

    // Rendu Vélos
    if (showVelos) {
        state.data.velos.forEach(velo => {
            if (velo.name.toLowerCase().includes(searchLower) || velo.address.toLowerCase().includes(searchLower)) {
                resultatsTrouves++;

                const marker = L.marker([velo.lat, velo.lon], { icon: icons.velo });
                const popupContent = `
                    <div class="popup-station">
                        <h3>${velo.name}</h3>
                        <p>${velo.address}</p>
                        <hr>
                        <p><b>Vélos disponibles :</b> ${velo.velosDispo}</p>
                        <p><b>Places libres :</b> ${velo.placesLibres}</p>
                    </div>
                `;
                marker.bindPopup(popupContent);
                layers.velos.addLayer(marker);

                resultsBox.innerHTML += `
                    <div class="result-card velo-card clickable-card" onclick="focusOnMap(${velo.lat}, ${velo.lon}, 'velos')">
                        <div class="result-title">${velo.name}</div>
                        <div class="result-address">${velo.address}</div>
                        <div class="result-desc">
                            <b>Vélos disponibles : </b>${velo.velosDispo}<br>
                            <b>Places libres : </b>${velo.placesLibres}
                        </div>
                    </div>
                `;
            }
        });
    }

    // Rendu Incidents
    if (showIncidents) {
        state.data.incidents.forEach(incident => {
            if (incident.short_description.toLowerCase().includes(searchLower) || incident.street.toLowerCase().includes(searchLower)) {
                resultatsTrouves++;

                const marker = L.marker([incident.lat, incident.lon], { icon: icons.incident });
                marker.bindPopup(`
                    <div class="popup-station">
                        <h3>${incident.short_description}</h3>
                        <p>${incident.street}</p>
                        <hr>
                        <p><b>Début :</b> ${incident.starttime}</p>
                    </div>
                `);
                layers.incidents.addLayer(marker);

                resultsBox.innerHTML += `
                    <div class="result-card incident-card clickable-card" onclick="focusOnMap(${incident.lat}, ${incident.lon}, 'incidents')">
                        <div class="result-title">${incident.short_description}</div>
                        <div class="result-address">${incident.street}</div>
                        <div class="result-desc" style="font-size: 11px;"><b>Début : </b>${incident.starttime}</div>
                    </div>
                `;
            }
        });
    }

    if (resultatsTrouves === 0) {
        resultsBox.innerHTML = '<p style="color: #999; font-size: 13px; text-align: center; padding: 20px 0;">Aucun résultat trouvé.</p>';
    }
}

// Actions UI
window.toggleFilter = function(category) {
    if (state.filter === category) {
        state.filter = null;
    } else {
        state.filter = category;
    }
    
    document.querySelectorAll('.filter-toggle').forEach(el => el.classList.remove('active'));
    if (state.filter) {
        document.getElementById('toggle-' + category).classList.add('active');
    }
    
    render();
};

window.handleSearch = function(event) {
    state.search = event.target.value;
    render();
};

window.focusOnMap = function(lat, lng, layerName) {
    map.setView([lat, lng], 16);
    layers[layerName].eachLayer(function (marker) {
        if (marker.getLatLng().lat === lat && marker.getLatLng().lng === lng) {
            marker.openPopup();
            
            // Si c'est un restaurant, s'assurer que la vue par défaut est affichée à l'ouverture
            if(layerName === 'restaurants') {
                const id = marker._popup._content.match(/view_(\d+)/);
                if(id && id[1]) {
                    setTimeout(() => {
                        const viewEl = document.getElementById(`view_${id[1]}`);
                        const formEl = document.getElementById(`form_${id[1]}`);
                        if(viewEl && formEl) {
                            viewEl.style.display = 'block';
                            formEl.style.display = 'none';
                        }
                    }, 50);
                }
            }
        }
    });
};

// Actions API
async function chargerStations() {
    try {
        const [reponseInfo, reponseStatus] = await Promise.all([
            fetch(CONFIG.URL_INFO), fetch(CONFIG.URL_STATUS)
        ]);
        const dataInfo = await reponseInfo.json();
        const dataStatus = await reponseStatus.json();
        const statusMap = new Map(dataStatus.data.stations.map(s => [s.station_id, s]));

        state.data.velos = dataInfo.data.stations.map(info => {
            const status = statusMap.get(info.station_id);
            return {
                name: info.name,
                lat: info.lat,
                lon: info.lon,
                address: info.address,
                velosDispo: status ? status.num_bikes_available : 0,
                placesLibres: status ? status.num_docks_available : 0
            };
        });
        render();
    } catch (erreur) { console.error("Erreur Vélibs :", erreur); }
}

async function chargerIncidents() {
    try {
        const reponse = await fetch(`${CONFIG.PROXY_URL}/incidents`);
        const data = await reponse.json();
        state.data.incidents = data.incidents.map(incident => { 
            const coords = incident.location.polyline.split(" ");
            return {
                short_description: incident.short_description,
                starttime: incident.starttime,
                street: incident.location.street,
                lat: parseFloat(coords[0]),
                lon: parseFloat(coords[1])
            };
        });
        render();
    } catch (erreur) { console.error("Erreur Waze :", erreur); }
}

async function chargerRestaurants() {
    try {
        const reponse = await fetch(`${CONFIG.PROXY_URL}/restaurants`);
        state.data.restaurants = await reponse.json();
        render();
    } catch (erreur) { console.error("Erreur Restaurants :", erreur); }
}

async function reserver(id) {
    const nbConvives = parseInt(document.getElementById(`convives_${id}`).value);
    
    if (isNaN(nbConvives) || nbConvives < 1) {
        document.getElementById(`msg_${id}`).innerText = "Nombre de convives invalide.";
        document.getElementById(`msg_${id}`).style.color = "red";
        return;
    }

    const data = {
        restaurantId: id,
        nom: document.getElementById(`nom_${id}`).value,
        prenom: document.getElementById(`prenom_${id}`).value,
        nbConvives: nbConvives,
        telephone: document.getElementById(`tel_${id}`).value
    };

    try {
        const reponse = await fetch(`${CONFIG.PROXY_URL}/reserver`, {
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
        document.getElementById(`msg_${id}`).style.color = "red";
    }
}

// Démarrage
chargerStations();
chargerIncidents();
chargerRestaurants();