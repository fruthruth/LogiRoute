document.addEventListener('DOMContentLoaded', function () {
    var mapEl = document.getElementById('map');
    var indicator = document.getElementById('refreshIndicator');

    // Default: Arequipa, Peru
    var defaultLat = -16.4090474;
    var defaultLng = -71.5374516;

    var map = L.map('map').setView([defaultLat, defaultLng], 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        maxZoom: 18
    }).addTo(map);

    // Layer groups
    var pedidosLayer = L.layerGroup().addTo(map);
    var ubicacionLayer = L.layerGroup().addTo(map);

    // My location marker
    var myLocationMarker = null;

    function createPedidoIcon(estado) {
        var color;
        switch (estado) {
            case 'ASIGNADO': color = '#f59e0b'; break;
            case 'EN_RECOJO': color = '#8b5cf6'; break;
            case 'EN_TRANSITO': color = '#06b6d4'; break;
            default: color = '#94a3b8';
        }
        return L.divIcon({
            className: 'custom-marker',
            html: '<div style="width:22px;height:22px;background:' + color + ';border:3px solid white;border-radius:50%;box-shadow:0 2px 6px rgba(0,0,0,0.3);"></div>',
            iconSize: [22, 22],
            iconAnchor: [11, 11],
            popupAnchor: [0, -14]
        });
    }

    function createMyLocationIcon() {
        return L.divIcon({
            className: 'custom-marker',
            html: '<div style="width:16px;height:16px;background:#7c3aed;border:3px solid white;border-radius:50%;box-shadow:0 0 12px rgba(124,58,237,0.6);"></div>',
            iconSize: [16, 16],
            iconAnchor: [8, 8],
            popupAnchor: [0, -10]
        });
    }

    function loadPedidoMarkers() {
        pedidosLayer.clearLayers();
        var items = document.querySelectorAll('.mapa-repartidor-item');
        items.forEach(function (item) {
            var lat = parseFloat(item.getAttribute('data-lat'));
            var lng = parseFloat(item.getAttribute('data-lng'));
            var codigo = item.getAttribute('data-codigo');
            var destino = item.getAttribute('data-destino');
            var estado = item.getAttribute('data-estado');
            var origen = item.getAttribute('data-origen');

            if (!isNaN(lat) && !isNaN(lng)) {
                var marker = L.marker([lat, lng], { icon: createPedidoIcon(estado) });
                marker.bindPopup(
                    '<div class="popup-nombre">' + codigo + '</div>' +
                    '<div class="popup-direccion"><b>Origen:</b> ' + origen + '</div>' +
                    '<div class="popup-direccion"><b>Destino:</b> ' + destino + '</div>' +
                    '<div class="popup-estado ' + estado + '">' + estado.replace('_', ' ') + '</div>',
                    { className: 'custom-popup' }
                );
                pedidosLayer.addLayer(marker);
            }
        });
        fitMapToLayers();
    }

    function fitMapToLayers() {
        var allMarkers = [];
        if (pedidosLayer.getLayers().length > 0) {
            allMarkers = allMarkers.concat(pedidosLayer.getLayers());
        }
        if (ubicacionLayer.getLayers().length > 0) {
            allMarkers = allMarkers.concat(ubicacionLayer.getLayers());
        }
        if (allMarkers.length > 0) {
            var group = L.featureGroup(allMarkers);
            map.fitBounds(group.getBounds().pad(0.15));
        }
    }

    window.shareLocation = function () {
        var btn = document.getElementById('ubicacionBtn');
        btn.disabled = true;
        btn.textContent = 'Obteniendo ubicacion...';

        if (!navigator.geolocation) {
            alert('Tu navegador no soporta geolocalizacion.');
            btn.disabled = false;
            btn.innerHTML = 'Mi ubicacion';
            return;
        }

        navigator.geolocation.getCurrentPosition(
            function (position) {
                var lat = position.coords.latitude;
                var lng = position.coords.longitude;

                if (myLocationMarker) {
                    ubicacionLayer.removeLayer(myLocationMarker);
                }

                myLocationMarker = L.marker([lat, lng], { icon: createMyLocationIcon() });
                myLocationMarker.bindPopup(
                    '<div class="popup-nombre">Mi ubicacion actual</div>' +
                    '<div class="popup-direccion">Lat: ' + lat.toFixed(6) + ', Lng: ' + lng.toFixed(6) + '</div>',
                    { className: 'custom-popup' }
                );
                ubicacionLayer.addLayer(myLocationMarker);

                // Update server via fetch POST
                var formData = new URLSearchParams();
                formData.append('latitud', lat);
                formData.append('longitud', lng);

                fetch('/repartidor/ubicacion', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: formData.toString()
                }).catch(function () {});

                btn.disabled = false;
                btn.innerHTML = 'Mi ubicacion';
                fitMapToLayers();
            },
            function (error) {
                alert('No se pudo obtener tu ubicacion: ' + error.message);
                btn.disabled = false;
                btn.innerHTML = 'Mi ubicacion';
            },
            { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
        );
    };

    // Click on pedido list item to zoom to marker
    document.querySelectorAll('.mapa-repartidor-item').forEach(function (item) {
        item.style.cursor = 'pointer';
        item.addEventListener('click', function () {
            var lat = parseFloat(item.getAttribute('data-lat'));
            var lng = parseFloat(item.getAttribute('data-lng'));
            if (!isNaN(lat) && !isNaN(lng)) {
                map.setView([lat, lng], 16);
            }
        });
    });

    // Initialize
    loadPedidoMarkers();

    indicator.textContent = 'Listo';
});
