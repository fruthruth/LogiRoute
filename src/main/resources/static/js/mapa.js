document.addEventListener('DOMContentLoaded', function () {
    var mapEl = document.getElementById('map');
    var indicator = document.getElementById('refreshIndicator');

    // Default: Lima, Peru
    var defaultLat = -12.0464;
    var defaultLng = -77.0428;

    var map = L.map('map').setView([defaultLat, defaultLng], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
        maxZoom: 18
    }).addTo(map);

    // Markers layer group
    var markersLayer = L.layerGroup().addTo(map);

    // Create custom icon based on estado
    function createMarkerIcon(estado) {
        var color;
        switch (estado) {
            case 'DISPONIBLE':
                color = '#22c55e';
                break;
            case 'EN_RUTA':
                color = '#f59e0b';
                break;
            default:
                color = '#94a3b8';
        }
        return L.divIcon({
            className: 'custom-marker',
            html: '<div style="' +
                'width: 20px; height: 20px; ' +
                'background: ' + color + '; ' +
                'border: 3px solid white; ' +
                'border-radius: 50%; ' +
                'box-shadow: 0 2px 6px rgba(0,0,0,0.3);' +
                '"></div>',
            iconSize: [20, 20],
            iconAnchor: [10, 10],
            popupAnchor: [0, -12]
        });
    }

    // Populate markers from DOM data
    function loadMarkers() {
        markersLayer.clearLayers();
        var items = document.querySelectorAll('.mapa-repartidor-item');

        items.forEach(function (item) {
            var lat = parseFloat(item.getAttribute('data-lat'));
            var lng = parseFloat(item.getAttribute('data-lng'));
            var nombre = item.getAttribute('data-nombre');
            var licencia = item.getAttribute('data-licencia');
            var estado = item.getAttribute('data-estado');

            if (!isNaN(lat) && !isNaN(lng)) {
                var marker = L.marker([lat, lng], { icon: createMarkerIcon(estado) });
                marker.bindPopup(
                    '<div class="popup-nombre">' + nombre + '</div>' +
                    '<div class="popup-licencia">' + licencia + '</div>' +
                    '<div class="popup-estado ' + estado + '">' + estado + '</div>',
                    { className: 'custom-popup' }
                );
                markersLayer.addLayer(marker);
            }
        });

        // Fit map to markers if any exist
        if (markersLayer.getLayers().length > 0) {
            var group = L.featureGroup(markersLayer.getLayers());
            map.fitBounds(group.getBounds().pad(0.1));
        }
    }

    loadMarkers();

    // Auto-refresh every 10 seconds via fetch to get fresh data
    var refreshInterval = 10000;

    function refreshData() {
        indicator.classList.add('active');
        indicator.textContent = 'Actualizando...';

        fetch('/api/repartidores')
            .then(function (response) { return response.json(); })
            .then(function (data) {
                markersLayer.clearLayers();

                data.forEach(function (r) {
                    if (r.latitude && r.longitude) {
                        var lat = parseFloat(r.latitude);
                        var lng = parseFloat(r.longitude);

                        if (!isNaN(lat) && !isNaN(lng)) {
                            var marker = L.marker([lat, lng], { icon: createMarkerIcon(r.estado) });
                            marker.bindPopup(
                                '<div class="popup-nombre">' + r.nombre + '</div>' +
                                '<div class="popup-licencia">' + r.licencia + '</div>' +
                                '<div class="popup-estado ' + r.estado + '">' + r.estado + '</div>',
                                { className: 'custom-popup' }
                            );
                            markersLayer.addLayer(marker);
                        }
                    }
                });

                if (markersLayer.getLayers().length > 0) {
                    var group = L.featureGroup(markersLayer.getLayers());
                    map.fitBounds(group.getBounds().pad(0.1));
                }

                indicator.classList.remove('active');
                indicator.textContent = 'Última actualización: ' + new Date().toLocaleTimeString();
            })
            .catch(function () {
                indicator.classList.remove('active');
                indicator.textContent = 'Error al actualizar';
            });
    }

    setInterval(refreshData, refreshInterval);

    // Click on repartidor list item to zoom to marker
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
});
