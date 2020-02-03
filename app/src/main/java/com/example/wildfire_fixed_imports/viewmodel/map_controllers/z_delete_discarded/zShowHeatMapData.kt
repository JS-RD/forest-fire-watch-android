package com.example.wildfire_fixed_imports.viewmodel.map_controllers.z_delete_discarded

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException

class HeatmapActivity : AppCompatActivity() {
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.

        mapView?.getMapAsync(OnMapReadyCallback { mapboxMap ->
            this@HeatmapActivity.mapboxMap = mapboxMap
            mapboxMap.setStyle(Style.DARK) { style ->
                addEarthquakeSource(style)
                addHeatmapLayer(style)
                addCircleLayer(style)
            }
        })
    }

    private fun addEarthquakeSource(loadedMapStyle: Style) {
        try {
            loadedMapStyle.addSource(GeoJsonSource(EARTHQUAKE_SOURCE_ID, URI(EARTHQUAKE_SOURCE_URL)))
        } catch (uriSyntaxException: URISyntaxException) {
            Timber.e(uriSyntaxException, "That's not an url... ")
        }
    }

    private fun addHeatmapLayer(loadedMapStyle: Style) {
        val layer = HeatmapLayer(HEATMAP_LAYER_ID, EARTHQUAKE_SOURCE_ID)
        layer.maxZoom = 9f
        layer.sourceLayer = HEATMAP_LAYER_SOURCE
        layer.setProperties( // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
// Begin color ramp at 0-stop with a 0-transparency color
// to create a blur-like effect.
                PropertyFactory.heatmapColor(
                        Expression.interpolate(
                                Expression.linear(), Expression.heatmapDensity(),
                                Expression.literal(0), Expression.rgba(33, 102, 172, 0),
                                Expression.literal(0.2), Expression.rgb(103, 169, 207),
                                Expression.literal(0.4), Expression.rgb(209, 229, 240),
                                Expression.literal(0.6), Expression.rgb(253, 219, 199),
                                Expression.literal(0.8), Expression.rgb(239, 138, 98),
                                Expression.literal(1), Expression.rgb(178, 24, 43)
                        )
                ),  // Increase the heatmap weight based on frequency and property magnitude
                PropertyFactory.heatmapWeight(
                        Expression.interpolate(
                                Expression.linear(), Expression.get("mag"),
                                Expression.stop(0, 0),
                                Expression.stop(6, 1)
                        )
                ),  // Increase the heatmap color weight weight by zoom level
// heatmap-intensity is a multiplier on top of heatmap-weight
                PropertyFactory.heatmapIntensity(
                        Expression.interpolate(
                                Expression.linear(), Expression.zoom(),
                                Expression.stop(0, 1),
                                Expression.stop(9, 3)
                        )
                ),  // Adjust the heatmap radius by zoom level
                PropertyFactory.heatmapRadius(
                        Expression.interpolate(
                                Expression.linear(), Expression.zoom(),
                                Expression.stop(0, 2),
                                Expression.stop(9, 20)
                        )
                ),  // Transition from heatmap to circle layer by zoom level
                PropertyFactory.heatmapOpacity(
                        Expression.interpolate(
                                Expression.linear(), Expression.zoom(),
                                Expression.stop(7, 1),
                                Expression.stop(9, 0)
                        )
                )
        )
        loadedMapStyle.addLayerAbove(layer, "waterway-label")
    }

    private fun addCircleLayer(loadedMapStyle: Style) {
        val circleLayer = CircleLayer(CIRCLE_LAYER_ID, EARTHQUAKE_SOURCE_ID)
        circleLayer.setProperties( // Size circle radius by earthquake magnitude and zoom level
                PropertyFactory.circleRadius(
                        Expression.interpolate(
                                Expression.linear(), Expression.zoom(),
                                Expression.literal(7), Expression.interpolate(
                                Expression.linear(), Expression.get("mag"),
                                Expression.stop(1, 1),
                                Expression.stop(6, 4)
                        ),
                                Expression.literal(16), Expression.interpolate(
                                Expression.linear(), Expression.get("mag"),
                                Expression.stop(1, 5),
                                Expression.stop(6, 50)
                        )
                        )
                ),  // Color circle by earthquake magnitude
                PropertyFactory.circleColor(
                        Expression.interpolate(
                                Expression.linear(), Expression.get("mag"),
                                Expression.literal(1), Expression.rgba(33, 102, 172, 0),
                                Expression.literal(2), Expression.rgb(103, 169, 207),
                                Expression.literal(3), Expression.rgb(209, 229, 240),
                                Expression.literal(4), Expression.rgb(253, 219, 199),
                                Expression.literal(5), Expression.rgb(239, 138, 98),
                                Expression.literal(6), Expression.rgb(178, 24, 43)
                        )
                ),  // Transition from heatmap to circle layer by zoom level
                PropertyFactory.circleOpacity(
                        Expression.interpolate(
                                Expression.linear(), Expression.zoom(),
                                Expression.stop(7, 0),
                                Expression.stop(8, 1)
                        )
                ),
                PropertyFactory.circleStrokeColor("white"),
                PropertyFactory.circleStrokeWidth(1.0f)
        )
        loadedMapStyle.addLayerBelow(circleLayer, HEATMAP_LAYER_ID)
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView!!.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mapView!!.onDestroy()
    }

    companion object {
        private const val EARTHQUAKE_SOURCE_URL = "https://www.mapbox.com/mapbox-gl-js/assets/earthquakes.geojson"
        private const val EARTHQUAKE_SOURCE_ID = "earthquakes"
        private const val HEATMAP_LAYER_ID = "earthquakes-heat"
        private const val HEATMAP_LAYER_SOURCE = "earthquakes"
        private const val CIRCLE_LAYER_ID = "earthquakes-circle"
    }
}
