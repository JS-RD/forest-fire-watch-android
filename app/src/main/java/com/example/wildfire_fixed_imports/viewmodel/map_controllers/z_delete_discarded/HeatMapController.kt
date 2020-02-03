package com.example.wildfire_fixed_imports.viewmodel.map_controllers.z_delete_discarded

import android.app.Activity
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException


/*

currently deprecated, unused in rest of project



*  in effort to decouple as much code as possible, a helper class specifically for heatmaps is proposed in addition to mapcontroller
*  as it allows us to simplify the map controller class and isolate heatmap related issues.
*
* */

@Deprecated("currently unused in rest of project, remains for future adaptation")
class HeatMapController () {
    //set correct mapbox map and the view containing the mapbox map via dependency injection
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val targetMap: MapboxMap by lazy {
        applicationLevelProvider.mapboxMap
    }
    private val mapboxView: View by lazy {
        applicationLevelProvider.mapboxView
    }
    //additional dependency injection
    private val currentActivity : Activity = applicationLevelProvider.currentActivity

    //EARTH QUAKE DATA FROM SDK EXAMPLE
    private val HEATMAP_SOURCE_URL =
        "https://www.mapbox.com/mapbox-gl-js/assets/earthquakes.geojson"


    //heat map functions experimental:


    private val HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID"
    private val HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID"
    lateinit var listOfHeatmapColors: Array<Expression>
    lateinit var listOfHeatmapRadiusStops: Array<Expression>
    lateinit var listOfHeatmapIntensityStops: Array<Float>
    private var index = 0
    private var lastSource: GeoJsonSource = GeoJsonSource("sauce")
    private var heatmapHasBeennitialized = false
    //style of underlying map, Style.DARK, Style.LIGHT, or Style.SATELLITE are suggested values.
    private val HEATMAP_STYLE = Style.DARK


 /*   fun initializeHeatMapExtended() {

        targetMap.setStyle(HEATMAP_STYLE) { style ->
            addHeatMapSource(style)
            *//*      addHeatmapLayer_fixed(style)
                      addCircleLayer(style)*//*
            if (!heatmapHasBeennitialized) {
                initHeatmapColors()
                initHeatmapRadiusStops()
                initHeatmapIntensityStops()

                //this may need to be moved out of initialize block
                addHeatmapLayer(style)

                if (currentActivity is MainActivity) {
                    currentActivity.setFabOnclick {
                        Toast.makeText(currentActivity.applicationContext, "initialize fab successful", Toast.LENGTH_SHORT ).show()
                        index++
                        if (index == listOfHeatmapColors.size - 1) {
                            index = 0
                        }
                        val heatmapLayer = style . getLayer (HEATMAP_LAYER_ID)
                        if (heatmapLayer != null) {
                            heatmapLayer.setProperties(
                                    PropertyFactory.heatmapColor(listOfHeatmapColors[index]),
                                    PropertyFactory.heatmapRadius(listOfHeatmapRadiusStops[index]),
                                    PropertyFactory.heatmapIntensity(listOfHeatmapIntensityStops[index])
                            );
                        }
                    }
                } else {
                    Toast.makeText(currentActivity.applicationContext, "didn't work yo", Toast.LENGTH_LONG ).show()
                }



                heatmapHasBeennitialized=true
            }
        }
        }
*/



    fun addHeatMapSource(@NonNull loadedMapStyle: Style) {
        val source = GeoJsonSource(
            HEATMAP_SOURCE_ID,
            //URI("asset://la_heatmap_styling_points.geojson")
            URI(HEATMAP_SOURCE_URL)
        )

        //if the new source is not the same as the old source, dump remove the old soure, ad the new source,
        // and set last source to our new value
        if (source != lastSource) {
            try {
                if (heatmapHasBeennitialized){
                    loadedMapStyle.removeSource(lastSource)
                }
                loadedMapStyle.addSource(
                    source
                )
                lastSource=source
            } catch (exception: URISyntaxException) {
                Timber.d(exception)
            }
        }
        else {
            //do nothing
            Toast.makeText(currentActivity.baseContext, "No change to source, not updated", Toast.LENGTH_SHORT).show()
        }
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
            )
        )
    }

    private fun initHeatmapIntensityStops() {
        listOfHeatmapIntensityStops = arrayOf(
            // 0
            0.6f,
// 1
            0.3f,
// 2
            1f,
// 3
            1f,
// 4
            1f,
// 5
            1f,
// 6
            1.5f,
// 7
            0.8f,
// 8
            0.25f,
// 9
            0.8f,
// 10
            0.25f,
// 11
            0.5f)
    }



    fun addHeatmapLayer(loadedMapStyle: Style) {
        // Create the heatmap layer
        val layer: HeatmapLayer = HeatmapLayer(HEATMAP_LAYER_ID, HEATMAP_SOURCE_ID);

        // Heatmap layer disappears at whatever zoom level is set as the maximum
        layer.setMaxZoom(18f)

        layer.setProperties(
            // Color ramp for heatmap.  Domain is 0 (low) to 1 (high).
            // Begin color ramp at 0-stop with a 0-transparency color to create a blur-like effect.
            PropertyFactory.heatmapColor(listOfHeatmapColors[index]),

            // Increase the heatmap color weight weight by zoom level
            // heatmap-intensity is a multiplier on top of heatmap-weight
            PropertyFactory.heatmapIntensity(listOfHeatmapIntensityStops[index]),

// Adjust the heatmap radius by zoom level
            PropertyFactory.heatmapRadius(
                listOfHeatmapRadiusStops[index]
            ),

            PropertyFactory.heatmapOpacity(1f)
        )
// Add the heatmap layer to the map and above the "water-label" layer
        loadedMapStyle.addLayerAbove(layer, "waterway-label");
    }



    //heat map alt show
    // everything that follows is a similar model, less abstracted and not used in the current implementation, it remains until we can finalize our needs.

    private val EARTHQUAKE_SOURCE_ID = "earthquakes"
    private val HEATMAP_LAYER_ID_alt = "earthquakes-heat"
    private val HEATMAP_LAYER_SOURCE = "earthquakes"
    private val CIRCLE_LAYER_ID = "earthquakes-circle"





    /*
    *
    *
*
    * */

    fun attemptHeatmapInitialAlt() {
        targetMap.setStyle(HEATMAP_STYLE, object : Style.OnStyleLoaded {
            override fun onStyleLoaded(style: Style) {
                addEarthquakeSource(style)
                addHeatmapLayer_fixed(style)
                addCircleLayer(style)
            }
        });
    }

    private fun addEarthquakeSource(@NonNull loadedMapStyle: Style) {
        try {
            loadedMapStyle.addSource(
                GeoJsonSource(
                    EARTHQUAKE_SOURCE_ID,
                    URI(HEATMAP_SOURCE_URL)
                )
            )
        } catch (uriSyntaxException: URISyntaxException) {
            Timber.e(uriSyntaxException, "That's not an url... ")
        }
    }

    private fun addHeatmapLayer_fixed(loadedMapStyle: Style) {
        val layer = HeatmapLayer(HEATMAP_LAYER_ID, EARTHQUAKE_SOURCE_ID)
        layer.maxZoom = 9f
        layer.setSourceLayer(HEATMAP_LAYER_SOURCE)
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
        loadedMapStyle.addLayerBelow(circleLayer, HEATMAP_LAYER_ID_alt)
    }




}