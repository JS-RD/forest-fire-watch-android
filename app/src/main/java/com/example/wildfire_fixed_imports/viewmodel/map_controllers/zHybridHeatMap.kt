import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException

private fun addClusteredGeoJsonSource(loadedMapStyle: Style) { // Add a new source from our GeoJSON data and set the 'cluster' option to true.
    try {
        loadedMapStyle.addSource( // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
// 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                GeoJsonSource("earthquakes",
                        URI("https://www.mapbox.com/mapbox-gl-js/assets/earthquakes.geojson"),
                        GeoJsonOptions()
                                .withCluster(true)
                                .withClusterMaxZoom(15) // Max zoom to cluster points on
                                .withClusterRadius(20) // Use small cluster radius for the hotspots look
                )
        )
    } catch (uriSyntaxException: URISyntaxException) {
        Timber.e("Check the URL %s", uriSyntaxException.message)
    }


}