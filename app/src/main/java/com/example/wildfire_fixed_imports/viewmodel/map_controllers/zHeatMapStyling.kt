package com.example.wildfire_fixed_imports.viewmodel.map_controllers

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.wildfire_fixed_imports.R
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException

class MultipleHeatmapStylingActivity : AppCompatActivity(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private lateinit var listOfHeatmapColors: Array<Expression>
    private lateinit var listOfHeatmapRadiusStops: Array<Expression>
    private lateinit var listOfHeatmapIntensityStops: Array<Float>
    private var index = 0
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        index = 0
        // Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.

        mapView?.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this@MultipleHeatmapStylingActivity.mapboxMap = mapboxMap
        mapboxMap.setStyle(Style.LIGHT) { style ->
            val cameraPositionForFragmentMap = CameraPosition.Builder()
                    .target(LatLng(34.056684, -118.254002))
                    .zoom(11.047)
                    .build()
            mapboxMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPositionForFragmentMap), 2600)
            try {
                style.addSource(GeoJsonSource(Companion.HEATMAP_SOURCE_ID, URI("asset://la_heatmap_styling_points.geojson")))
            } catch (exception: URISyntaxException) {
                Timber.d(exception)
            }
            initHeatmapColors()
            initHeatmapRadiusStops()
            initHeatmapIntensityStops()
            addHeatmapLayer(style)
            findViewById<View>(R.id.settings).setOnClickListener {
                index++
                if (index == listOfHeatmapColors.size - 1) {
                    index = 0
                }
                val heatmapLayer = style.getLayer(Companion.HEATMAP_LAYER_ID)
                heatmapLayer?.setProperties(
                        PropertyFactory.heatmapColor(listOfHeatmapColors[index]),
                        PropertyFactory.heatmapRadius(listOfHeatmapRadiusStops[index]),
                        PropertyFactory.heatmapIntensity(listOfHeatmapIntensityStops[index])
                )
            }
        }
    }

    private fun addHeatmapLayer(loadedMapStyle: Style) { // Create the heatmap layer
        val layer = HeatmapLayer(Companion.HEATMAP_LAYER_ID, Companion.HEATMAP_SOURCE_ID)
        // Heatmap layer disappears at whatever zoom level is set as the maximum
        layer.maxZoom = 18f
        layer.setProperties( // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
// Begin color ramp at 0-stop with a 0-transparency color to create a blur-like effect.
                PropertyFactory.heatmapColor(listOfHeatmapColors[index]),  // Increase the heatmap color weight weight by zoom level
// heatmap-intensity is a multiplier on top of heatmap-weight
                PropertyFactory.heatmapIntensity(listOfHeatmapIntensityStops[index]),  // Adjust the heatmap radius by zoom level
                PropertyFactory.heatmapRadius(listOfHeatmapRadiusStops[index]
                ),
                PropertyFactory.heatmapOpacity(1f)
        )
        // Add the heatmap layer to the map and above the "water-label" layer
        loadedMapStyle.addLayerAbove(layer, "waterway-label")
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

    private fun initHeatmapColors() {
        listOfHeatmapColors = arrayOf( // 0
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(0, 0, 0, 0.01),
                        Expression.literal(0.25), Expression.rgba(224, 176, 63, 0.5),
                        Expression.literal(0.5), Expression.rgb(247, 252, 84),
                        Expression.literal(0.75), Expression.rgb(186, 59, 30),
                        Expression.literal(0.9), Expression.rgb(255, 0, 0)
                ),  // 1
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(255, 255, 255, 0.4),
                        Expression.literal(0.25), Expression.rgba(4, 179, 183, 1.0),
                        Expression.literal(0.5), Expression.rgba(204, 211, 61, 1.0),
                        Expression.literal(0.75), Expression.rgba(252, 167, 55, 1.0),
                        Expression.literal(1), Expression.rgba(255, 78, 70, 1.0)
                ),  // 2
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(12, 182, 253, 0.0),
                        Expression.literal(0.25), Expression.rgba(87, 17, 229, 0.5),
                        Expression.literal(0.5), Expression.rgba(255, 0, 0, 1.0),
                        Expression.literal(0.75), Expression.rgba(229, 134, 15, 0.5),
                        Expression.literal(1), Expression.rgba(230, 255, 55, 0.6)
                ),  // 3
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(135, 255, 135, 0.2),
                        Expression.literal(0.5), Expression.rgba(255, 99, 0, 0.5),
                        Expression.literal(1), Expression.rgba(47, 21, 197, 0.2)
                ),  // 4
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(4, 0, 0, 0.2),
                        Expression.literal(0.25), Expression.rgba(229, 12, 1, 1.0),
                        Expression.literal(0.30), Expression.rgba(244, 114, 1, 1.0),
                        Expression.literal(0.40), Expression.rgba(255, 205, 12, 1.0),
                        Expression.literal(0.50), Expression.rgba(255, 229, 121, 1.0),
                        Expression.literal(1), Expression.rgba(255, 253, 244, 1.0)
                ),  // 5
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(0, 0, 0, 0.01),
                        Expression.literal(0.05), Expression.rgba(0, 0, 0, 0.05),
                        Expression.literal(0.4), Expression.rgba(254, 142, 2, 0.7),
                        Expression.literal(0.5), Expression.rgba(255, 165, 5, 0.8),
                        Expression.literal(0.8), Expression.rgba(255, 187, 4, 0.9),
                        Expression.literal(0.95), Expression.rgba(255, 228, 173, 0.8),
                        Expression.literal(1), Expression.rgba(255, 253, 244, .8)
                ),  //6
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(0, 0, 0, 0.01),
                        Expression.literal(0.3), Expression.rgba(82, 72, 151, 0.4),
                        Expression.literal(0.4), Expression.rgba(138, 202, 160, 1.0),
                        Expression.literal(0.5), Expression.rgba(246, 139, 76, 0.9),
                        Expression.literal(0.9), Expression.rgba(252, 246, 182, 0.8),
                        Expression.literal(1), Expression.rgba(255, 255, 255, 0.8)
                ),  //7
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(0, 0, 0, 0.01),
                        Expression.literal(0.1), Expression.rgba(0, 2, 114, .1),
                        Expression.literal(0.2), Expression.rgba(0, 6, 219, .15),
                        Expression.literal(0.3), Expression.rgba(0, 74, 255, .2),
                        Expression.literal(0.4), Expression.rgba(0, 202, 255, .25),
                        Expression.literal(0.5), Expression.rgba(73, 255, 154, .3),
                        Expression.literal(0.6), Expression.rgba(171, 255, 59, .35),
                        Expression.literal(0.7), Expression.rgba(255, 197, 3, .4),
                        Expression.literal(0.8), Expression.rgba(255, 82, 1, 0.7),
                        Expression.literal(0.9), Expression.rgba(196, 0, 1, 0.8),
                        Expression.literal(0.95), Expression.rgba(121, 0, 0, 0.8)
                ),  // 8
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(0, 0, 0, 0.01),
                        Expression.literal(0.1), Expression.rgba(0, 2, 114, .1),
                        Expression.literal(0.2), Expression.rgba(0, 6, 219, .15),
                        Expression.literal(0.3), Expression.rgba(0, 74, 255, .2),
                        Expression.literal(0.4), Expression.rgba(0, 202, 255, .25),
                        Expression.literal(0.5), Expression.rgba(73, 255, 154, .3),
                        Expression.literal(0.6), Expression.rgba(171, 255, 59, .35),
                        Expression.literal(0.7), Expression.rgba(255, 197, 3, .4),
                        Expression.literal(0.8), Expression.rgba(255, 82, 1, 0.7),
                        Expression.literal(0.9), Expression.rgba(196, 0, 1, 0.8),
                        Expression.literal(0.95), Expression.rgba(121, 0, 0, 0.8)
                ),  // 9
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(0, 0, 0, 0.01),
                        Expression.literal(0.1), Expression.rgba(0, 2, 114, .1),
                        Expression.literal(0.2), Expression.rgba(0, 6, 219, .15),
                        Expression.literal(0.3), Expression.rgba(0, 74, 255, .2),
                        Expression.literal(0.4), Expression.rgba(0, 202, 255, .25),
                        Expression.literal(0.5), Expression.rgba(73, 255, 154, .3),
                        Expression.literal(0.6), Expression.rgba(171, 255, 59, .35),
                        Expression.literal(0.7), Expression.rgba(255, 197, 3, .4),
                        Expression.literal(0.8), Expression.rgba(255, 82, 1, 0.7),
                        Expression.literal(0.9), Expression.rgba(196, 0, 1, 0.8),
                        Expression.literal(0.95), Expression.rgba(121, 0, 0, 0.8)
                ),  // 10
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(0, 0, 0, 0.01),
                        Expression.literal(0.1), Expression.rgba(0, 2, 114, .1),
                        Expression.literal(0.2), Expression.rgba(0, 6, 219, .15),
                        Expression.literal(0.3), Expression.rgba(0, 74, 255, .2),
                        Expression.literal(0.4), Expression.rgba(0, 202, 255, .25),
                        Expression.literal(0.5), Expression.rgba(73, 255, 154, .3),
                        Expression.literal(0.6), Expression.rgba(171, 255, 59, .35),
                        Expression.literal(0.7), Expression.rgba(255, 197, 3, .4),
                        Expression.literal(0.8), Expression.rgba(255, 82, 1, 0.7),
                        Expression.literal(0.9), Expression.rgba(196, 0, 1, 0.8),
                        Expression.literal(0.95), Expression.rgba(121, 0, 0, 0.8)
                ),  // 11
                Expression.interpolate(
                        Expression.linear(), Expression.heatmapDensity(),
                        Expression.literal(0.01), Expression.rgba(0, 0, 0, 0.25),
                        Expression.literal(0.25), Expression.rgba(229, 12, 1, .7),
                        Expression.literal(0.30), Expression.rgba(244, 114, 1, .7),
                        Expression.literal(0.40), Expression.rgba(255, 205, 12, .7),
                        Expression.literal(0.50), Expression.rgba(255, 229, 121, .8),
                        Expression.literal(1), Expression.rgba(255, 253, 244, .8)
                )
        )
    }

    private fun initHeatmapRadiusStops() {
        listOfHeatmapRadiusStops = arrayOf( // 0
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(6), Expression.literal(50),
                        Expression.literal(20), Expression.literal(100)
                ),  // 1
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(12), Expression.literal(70),
                        Expression.literal(20), Expression.literal(100)
                ),  // 2
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(7),
                        Expression.literal(5), Expression.literal(50)
                ),  // 3
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(7),
                        Expression.literal(5), Expression.literal(50)
                ),  // 4
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(7),
                        Expression.literal(5), Expression.literal(50)
                ),  // 5
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(7),
                        Expression.literal(15), Expression.literal(200)
                ),  // 6
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(10),
                        Expression.literal(8), Expression.literal(70)
                ),  // 7
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(10),
                        Expression.literal(8), Expression.literal(200)
                ),  // 8
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(10),
                        Expression.literal(8), Expression.literal(200)
                ),  // 9
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(10),
                        Expression.literal(8), Expression.literal(200)
                ),  // 10
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(10),
                        Expression.literal(8), Expression.literal(200)
                ),  // 11
                Expression.interpolate(
                        Expression.linear(), Expression.zoom(),
                        Expression.literal(1), Expression.literal(10),
                        Expression.literal(8), Expression.literal(200)
                ))
    }

    private fun initHeatmapIntensityStops() {
        listOfHeatmapIntensityStops = arrayOf( // 0
                0.6f,  // 1
                0.3f,  // 2
                1f,  // 3
                1f,  // 4
                1f,  // 5
                1f,  // 6
                1.5f,  // 7
                0.8f,  // 8
                0.25f,  // 9
                0.8f,  // 10
                0.25f,  // 11
                0.5f
        )
    }

    companion object {
        private const val HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID"
        private const val HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID"
    }
}
