package com.example.wildfire_fixed_imports.viewmodel.map_controllers.z_delete_discarded

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException

class zHeatMapSpots {
    /**
     * Use Mapbox GL clustering to visualize point data as hotspots.
     */
    inner class CreateHotspotsActivity : AppCompatActivity() {
        private var mapView: MapView? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            // Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.

            mapView?.getMapAsync(OnMapReadyCallback { mapboxMap -> mapboxMap.setStyle(Style.DARK) { style -> addClusteredGeoJsonSource(style) } })
        }

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
            // Use the earthquakes source to create four layers:
// three for each cluster category, and one for unclustered points
// Each point range gets a different fill color.

            val layers = arrayOf(intArrayOf(150, Color.parseColor("#E55E5E")),
                    intArrayOf(20, Color.parseColor("#F9886C")),
                    intArrayOf(0, Color.parseColor("#FBB03B")))
            val unclustered = CircleLayer("unclustered-points", "earthquakes")
            unclustered.setProperties(
                    PropertyFactory.circleColor(Color.parseColor("#FBB03B")),
                    PropertyFactory.circleRadius(20f),
                    PropertyFactory.circleBlur(1f))
            unclustered.setFilter(Expression.neq(Expression.get("cluster"), Expression.literal(true)))
            loadedMapStyle.addLayerBelow(unclustered, "building")
            for (i in layers.indices) {
                val circles = CircleLayer("cluster-$i", "earthquakes")
                circles.setProperties(
                        PropertyFactory.circleColor(layers[i][1]),
                        PropertyFactory.circleRadius(70f),
                        PropertyFactory.circleBlur(1f)
                )
                val pointCount = Expression.toNumber(Expression.get("point_count"))
                circles.setFilter(
                        if (i == 0) Expression.gte(pointCount, Expression.literal(layers[i][0])) else Expression.all(
                                Expression.gte(pointCount, Expression.literal(layers[i][0])),
                                Expression.lt(pointCount, Expression.literal(layers[i - 1][0]))
                        )
                )
                loadedMapStyle.addLayerBelow(circles, "building")
            }
        }

        public override fun onResume() {
            super.onResume()
            mapView!!.onResume()
        }

        override fun onStart() {
            super.onStart()
            mapView!!.onStart()
        }

        override fun onStop() {
            super.onStop()
            mapView!!.onStop()
        }

        public override fun onPause() {
            super.onPause()
            mapView!!.onPause()
        }

        override fun onLowMemory() {
            super.onLowMemory()
            mapView!!.onLowMemory()
        }

        override fun onDestroy() {
            super.onDestroy()
            mapView!!.onDestroy()
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            mapView!!.onSaveInstanceState(outState)
        }
    }
}