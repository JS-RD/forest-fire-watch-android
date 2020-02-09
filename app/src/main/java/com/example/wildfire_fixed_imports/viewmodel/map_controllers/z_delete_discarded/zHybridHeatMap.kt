package com.example.wildfire_fixed_imports.viewmodel.map_controllers.z_delete_discarded

import android.graphics.Color
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException

class zHybridHeatMap() {

}

private var mapView: MapView? = null
private var mapboxMap: MapboxMap? = null
private lateinit var listOfHeatmapColors: Array<Expression>
private lateinit var listOfHeatmapRadiusStops: Array<Expression>
private lateinit var listOfHeatmapIntensityStops: Array<Float>
private var index = 0

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

private fun addUnclusteredGeoJsonSource(loadedMapStyle: Style) { // Add a new source from our GeoJSON data and set the 'cluster' option to true.


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


fun createLayers(loadedMapStyle: Style) {

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

fun initHeatmapColors() {
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

