
package com.example.wildfire_fixed_imports.viewmodel.view_controllers


import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.fireIconTarget
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException


/**
 * Use GeoJSON and circle layers to visualize point data as circle clusters.
 */
class CircleLayerClusteringActivity : AppCompatActivity() {
    private var mapboxMap: MapboxMap? = null
    private var applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private  var mapView = applicationLevelProvider.mapboxView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, "Access token")
        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapview_main)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(OnMapReadyCallback { map ->
            mapboxMap = map
            map.setStyle(Style.LIGHT) { style ->
                // Disable any type of fading transition when icons collide on the map. This enhances the visual
                // look of the data clustering together and breaking apart.
                style.transition = TransitionOptions(0, 0, false)
                mapboxMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(
                        12.099, -79.045), 3.0))
                addClusteredGeoJsonSource(style)

                Toast.makeText(this@CircleLayerClusteringActivity, "zoom map instructions",
                        Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addClusteredGeoJsonSource(loadedMapStyle: Style) { // Add a new source from the GeoJSON data and set the 'cluster' option to true.
        try {
            loadedMapStyle.addSource( // Point to GeoJSON data. This example visualizes all M1.0+ earthquakes from
// 12/22/15 to 1/21/16 as logged by USGS' Earthquake hazards program.
                    GeoJsonSource("earthquakes",
                            URI("https://www.mapbox.com/mapbox-gl-js/assets/earthquakes.geojson"),
                            GeoJsonOptions()
                                    .withCluster(true)
                                    .withClusterMaxZoom(14)
                                    .withClusterRadius(50)
                    )
            )
        } catch (uriSyntaxException: URISyntaxException) {
            Timber.e("Check the URL %s", uriSyntaxException.message)
        }
        //Creating a marker layer for single data points
        val unclustered = SymbolLayer("unclustered-points", "earthquakes")
        unclustered.setProperties(
                PropertyFactory.iconImage("cross-icon-id"),
                PropertyFactory.iconSize(
                        Expression.division(
                                Expression.get("mag"), Expression.literal(4.0f)
                        )
                ),
                PropertyFactory.iconColor(
                        Expression.interpolate(Expression.exponential(1), Expression.get("mag"),
                                Expression.stop(2.0, Expression.rgb(0, 255, 0)),
                                Expression.stop(4.5, Expression.rgb(0, 0, 255)),
                                Expression.stop(7.0, Expression.rgb(255, 0, 0))
                        )
                )
        )
        unclustered.setFilter(Expression.has("mag"))
        loadedMapStyle.addLayer(unclustered)
        // Use the earthquakes GeoJSON source to create three layers: One layer for each cluster category.
// Each point range gets a different fill color.
        val layers = arrayOf(intArrayOf(150, ContextCompat.getColor(this, R.color.lb_control_button_color)), intArrayOf(20, ContextCompat.getColor(this, R.color.design_default_color_primary)), intArrayOf(0, ContextCompat.getColor(this, R.color.mapbox_blue)))
        for (i in layers.indices) { //Add clusters' circles
            val circles = CircleLayer("cluster-$i", "earthquakes")
            circles.setProperties(
                    PropertyFactory.circleColor(layers[i][1]),
                    PropertyFactory.circleRadius(18f)
            )
            val pointCount = Expression.toNumber(Expression.get("point_count"))
            // Add a filter to the cluster layer that hides the circles based on "point_count"
            circles.setFilter(
                    if (i == 0) Expression.all(Expression.has("point_count"),
                            Expression.gte(pointCount, Expression.literal(layers[i][0]))
                    ) else Expression.all(Expression.has("point_count"),
                            Expression.gte(pointCount, Expression.literal(layers[i][0])),
                            Expression.lt(pointCount, Expression.literal(layers[i - 1][0]))
                    )
            )
            loadedMapStyle.addLayer(circles)
        }
        //Add the count labels
        val count = SymbolLayer("count", "earthquakes")
        count.setProperties(
                PropertyFactory.textField(Expression.toString(Expression.get("point_count"))),
                PropertyFactory.textSize(12f),
                PropertyFactory.textColor(Color.WHITE),
                PropertyFactory.textIgnorePlacement(true),
                PropertyFactory.textAllowOverlap(true)
        )
        loadedMapStyle.addLayer(count)
    }

    public override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    public override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        mapView!!.onPause()
    }

    public override fun onStop() {
        super.onStop()
        mapView!!.onStop()
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