package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.MainActivity
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.*
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import timber.log.Timber
import java.net.URI
import java.net.URISyntaxException


/*
*           Map Controller
*   Map controller is repsonsible for recieving instructions on what to draw to the map and then draing those instructions.  i.e the
*  view model will let map controll know there is fire at x,y and map controller will then determine how best to display that fire, then draw that fire to the map
*
*
* */
class MapController() {

    //set correct mapbox map and the view containing the mapbox map via dependency injection

    val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    val targetMap: MapboxMap by lazy {
        applicationLevelProvider.mapboxMap
    }
    val mapboxView: View by lazy {
        applicationLevelProvider.mapboxView
    }

    //additional dependency injection
    val currentActivity : Activity = applicationLevelProvider.currentActivity

    fun traceResult() {

    }

    init {



        //this is here stirctly while testing heatmap methods
        val app = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
        val currentAct = app.currentActivity
        Toast.makeText(currentAct.applicationContext, "init", Toast.LENGTH_SHORT ).show()
        if (currentAct is MainActivity) {
            currentAct.setFabOnclick { attemptHeatmapInitialAlt() }
        }
    }


    fun addbackgroundtomap() {
        targetMap.getStyle {
            val backgroundLayer = BackgroundLayer("background-layer")
            backgroundLayer.setProperties(PropertyFactory.backgroundColor(Color.BLUE))

            // Add background layer to map
            it.addLayer(backgroundLayer)
        }
    }

    //heat map alt show
    //EARTH QUAKE DATA FROM SDK EXAMPLE
    private val HEATMAP_SOURCE_URL =
        "https://www.mapbox.com/mapbox-gl-js/assets/earthquakes.geojson"
    private val EARTHQUAKE_SOURCE_ID = "earthquakes"
    private val HEATMAP_LAYER_ID_alt = "earthquakes-heat"
    private val HEATMAP_LAYER_SOURCE = "earthquakes"
    private val CIRCLE_LAYER_ID = "earthquakes-circle"

    //style of underlying map, Style.DARK, Style.LIGHT, or Style.SATELLITE are suggested values.
    private val HEADMAP_STYLE = Style.SATELLITE



    /*
    *
    *
*
    * */

    fun attemptHeatmapInitialAlt() {

                targetMap.setStyle(HEADMAP_STYLE, object : Style.OnStyleLoaded {
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
            heatmapColor(
                interpolate(
                    linear(), heatmapDensity(),
                    literal(0), rgba(33, 102, 172, 0),
                    literal(0.2), rgb(103, 169, 207),
                    literal(0.4), rgb(209, 229, 240),
                    literal(0.6), rgb(253, 219, 199),
                    literal(0.8), rgb(239, 138, 98),
                    literal(1), rgb(178, 24, 43)
                )
            ),  // Increase the heatmap weight based on frequency and property magnitude
            heatmapWeight(
                interpolate(
                    linear(), get("mag"),
                    stop(0, 0),
                    stop(6, 1)
                )
            ),  // Increase the heatmap color weight weight by zoom level
// heatmap-intensity is a multiplier on top of heatmap-weight
            heatmapIntensity(
                interpolate(
                    linear(), zoom(),
                    stop(0, 1),
                    stop(9, 3)
                )
            ),  // Adjust the heatmap radius by zoom level
            heatmapRadius(
                interpolate(
                    linear(), zoom(),
                    stop(0, 2),
                    stop(9, 20)
                )
            ),  // Transition from heatmap to circle layer by zoom level
            heatmapOpacity(
                interpolate(
                    linear(), zoom(),
                    stop(7, 1),
                    stop(9, 0)
                )
            )
        )
        loadedMapStyle.addLayerAbove(layer, "waterway-label")
    }

    private fun addCircleLayer(loadedMapStyle: Style) {
        val circleLayer = CircleLayer(CIRCLE_LAYER_ID, EARTHQUAKE_SOURCE_ID)
        circleLayer.setProperties( // Size circle radius by earthquake magnitude and zoom level
            circleRadius(
                interpolate(
                    linear(), zoom(),
                    literal(7), interpolate(
                        linear(), get("mag"),
                        stop(1, 1),
                        stop(6, 4)
                    ),
                    literal(16), interpolate(
                        linear(), get("mag"),
                        stop(1, 5),
                        stop(6, 50)
                    )
                )
            ),  // Color circle by earthquake magnitude
            circleColor(
                interpolate(
                    linear(), get("mag"),
                    literal(1), rgba(33, 102, 172, 0),
                    literal(2), rgb(103, 169, 207),
                    literal(3), rgb(209, 229, 240),
                    literal(4), rgb(253, 219, 199),
                    literal(5), rgb(239, 138, 98),
                    literal(6), rgb(178, 24, 43)
                )
            ),  // Transition from heatmap to circle layer by zoom level
            circleOpacity(
                interpolate(
                    linear(), zoom(),
                    stop(7, 0),
                    stop(8, 1)
                )
            ),
            circleStrokeColor("white"),
            circleStrokeWidth(1.0f)
        )
        loadedMapStyle.addLayerBelow(circleLayer, HEATMAP_LAYER_ID)
    }


    //heat map functions experimental:


    private val HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID"
    private val HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID"
    private var mapView: MapView? = null
    lateinit var listOfHeatmapColors: Array<Expression>
    lateinit var listOfHeatmapRadiusStops: Array<Expression>
    lateinit var listOfHeatmapIntensityStops: Array<Float>
    private var index = 0



    fun initializeHeatMapExtended() {
        targetMap.setStyle(Style.LIGHT) { style ->
            val cameraPositionForFragmentMap = CameraPosition.Builder()
                .target(LatLng(34.056684, -118.254002))
                .zoom(11.047)
                .build()
            targetMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(cameraPositionForFragmentMap), 2600
            )
            try {
                style.addSource(
                    GeoJsonSource(
                        HEATMAP_SOURCE_ID,
                        URI("asset://la_heatmap_styling_points.geojson")
                    )
                )
            } catch (exception: URISyntaxException) {
                Timber.d(exception)
            }
            initHeatmapColors()
            initHeatmapRadiusStops()
            initHeatmapIntensityStops()
            addHeatmapLayer(style)
            if (currentActivity is MainActivity) {
                currentActivity.setFabOnclick {
                    index++
                    if (index == listOfHeatmapColors.size - 1) {
                        index = 0
                    }
                    style.getLayer(HEATMAP_LAYER_ID)?.setProperties(
                        heatmapColor(listOfHeatmapColors[index]),
                        heatmapRadius(listOfHeatmapRadiusStops[index]),
                        heatmapIntensity(listOfHeatmapIntensityStops[index])
                    )
                }
            }
        }
    }

    fun initializeHeatMap() {
        val currentAct = ApplicationLevelProvider.getApplicaationLevelProviderInstance().currentActivity
        if (currentAct is MainActivity) {
            (currentAct as MainActivity).setFabOnclick {
                val app = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
                Toast.makeText(currentAct.applicationContext, app.someString, Toast.LENGTH_LONG ).show()
                index++
                /*  if (index == listOfHeatmapColors.size - 1) {
                      index = 0
                  }
                  val heatmapLayer = style . getLayer (HEATMAP_LAYER_ID)
                  if (heatmapLayer != null) {
                      heatmapLayer.setProperties(
                          heatmapColor(listOfHeatmapColors[index]),
                          heatmapRadius(listOfHeatmapRadiusStops[index]),
                          heatmapIntensity(listOfHeatmapIntensityStops[index])
                      );
                  }*/
            }
        }
        else {
            Toast.makeText(currentAct.applicationContext, "didn't work yo", Toast.LENGTH_LONG ).show()
        }
        targetMap.setStyle(Style.LIGHT, object : Style.OnStyleLoaded {

            override fun onStyleLoaded(style: Style) {


                val cameraPositionForFragmentMap = CameraPosition.Builder()
                    .target(LatLng(34.056684, -118.254002))
                    .zoom(11.047)
                    .build();


                targetMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPositionForFragmentMap), 2600
                );
                try {
                    style.addSource(
                        GeoJsonSource(
                            HEATMAP_SOURCE_ID,
                            URI("asset://la_heatmap_styling_points.geojson")
                        )
                    )
                } catch (exception: URISyntaxException) {
                    Timber.d(exception);
                }
                initHeatmapColors();
                initHeatmapRadiusStops();
                initHeatmapIntensityStops();
                addHeatmapLayer(style);

                //lol some goofy nonsense for funz, please to be remove or replace with less janky
                val app = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
                val currentAct = app.currentActivity

                if (currentAct is MainActivity) {
                    (currentAct as MainActivity).setFabOnclick {
                        Toast.makeText(currentAct.applicationContext, app.someString, Toast.LENGTH_LONG ).show()
                        index++
                        if (index == listOfHeatmapColors.size - 1) {
                            index = 0
                        }
                        val heatmapLayer = style . getLayer (HEATMAP_LAYER_ID)
                        if (heatmapLayer != null) {
                            heatmapLayer.setProperties(
                                heatmapColor(listOfHeatmapColors[index]),
                                heatmapRadius(listOfHeatmapRadiusStops[index]),
                                heatmapIntensity(listOfHeatmapIntensityStops[index])
                            );
                        }
                    }
                }
                else {
                    Toast.makeText(currentAct.applicationContext, "didn't work yo", Toast.LENGTH_LONG ).show()
                }

                /*findViewById(R.id.switch_heatmap_style_fab).setOnClickListener(new View . OnClickListener () {
                    @Override
                    public void onClick(View view) {

                    }
                });*/
            }
        })
    }


    private fun initHeatmapColors() {
        listOfHeatmapColors = arrayOf( // 0
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(0, 0, 0, 0.01),
                literal(0.25), rgba(224, 176, 63, 0.5),
                literal(0.5), rgb(247, 252, 84),
                literal(0.75), rgb(186, 59, 30),
                literal(0.9), rgb(255, 0, 0)
            ),  // 1
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(255, 255, 255, 0.4),
                literal(0.25), rgba(4, 179, 183, 1.0),
                literal(0.5), rgba(204, 211, 61, 1.0),
                literal(0.75), rgba(252, 167, 55, 1.0),
                literal(1), rgba(255, 78, 70, 1.0)
            ),  // 2
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(12, 182, 253, 0.0),
                literal(0.25), rgba(87, 17, 229, 0.5),
                literal(0.5), rgba(255, 0, 0, 1.0),
                literal(0.75), rgba(229, 134, 15, 0.5),
                literal(1), rgba(230, 255, 55, 0.6)
            ),  // 3
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(135, 255, 135, 0.2),
                literal(0.5), rgba(255, 99, 0, 0.5),
                literal(1), rgba(47, 21, 197, 0.2)
            ),  // 4
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(4, 0, 0, 0.2),
                literal(0.25), rgba(229, 12, 1, 1.0),
                literal(0.30), rgba(244, 114, 1, 1.0),
                literal(0.40), rgba(255, 205, 12, 1.0),
                literal(0.50), rgba(255, 229, 121, 1.0),
                literal(1), rgba(255, 253, 244, 1.0)
            ),  // 5
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(0, 0, 0, 0.01),
                literal(0.05), rgba(0, 0, 0, 0.05),
                literal(0.4), rgba(254, 142, 2, 0.7),
                literal(0.5), rgba(255, 165, 5, 0.8),
                literal(0.8), rgba(255, 187, 4, 0.9),
                literal(0.95), rgba(255, 228, 173, 0.8),
                literal(1), rgba(255, 253, 244, .8)
            ),  //6
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(0, 0, 0, 0.01),
                literal(0.3), rgba(82, 72, 151, 0.4),
                literal(0.4), rgba(138, 202, 160, 1.0),
                literal(0.5), rgba(246, 139, 76, 0.9),
                literal(0.9), rgba(252, 246, 182, 0.8),
                literal(1), rgba(255, 255, 255, 0.8)
            ),  //7
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(0, 0, 0, 0.01),
                literal(0.1), rgba(0, 2, 114, .1),
                literal(0.2), rgba(0, 6, 219, .15),
                literal(0.3), rgba(0, 74, 255, .2),
                literal(0.4), rgba(0, 202, 255, .25),
                literal(0.5), rgba(73, 255, 154, .3),
                literal(0.6), rgba(171, 255, 59, .35),
                literal(0.7), rgba(255, 197, 3, .4),
                literal(0.8), rgba(255, 82, 1, 0.7),
                literal(0.9), rgba(196, 0, 1, 0.8),
                literal(0.95), rgba(121, 0, 0, 0.8)
            ),  // 8
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(0, 0, 0, 0.01),
                literal(0.1), rgba(0, 2, 114, .1),
                literal(0.2), rgba(0, 6, 219, .15),
                literal(0.3), rgba(0, 74, 255, .2),
                literal(0.4), rgba(0, 202, 255, .25),
                literal(0.5), rgba(73, 255, 154, .3),
                literal(0.6), rgba(171, 255, 59, .35),
                literal(0.7), rgba(255, 197, 3, .4),
                literal(0.8), rgba(255, 82, 1, 0.7),
                literal(0.9), rgba(196, 0, 1, 0.8),
                literal(0.95), rgba(121, 0, 0, 0.8)
            ),  // 9
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(0, 0, 0, 0.01),
                literal(0.1), rgba(0, 2, 114, .1),
                literal(0.2), rgba(0, 6, 219, .15),
                literal(0.3), rgba(0, 74, 255, .2),
                literal(0.4), rgba(0, 202, 255, .25),
                literal(0.5), rgba(73, 255, 154, .3),
                literal(0.6), rgba(171, 255, 59, .35),
                literal(0.7), rgba(255, 197, 3, .4),
                literal(0.8), rgba(255, 82, 1, 0.7),
                literal(0.9), rgba(196, 0, 1, 0.8),
                literal(0.95), rgba(121, 0, 0, 0.8)
            ),  // 10
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(0, 0, 0, 0.01),
                literal(0.1), rgba(0, 2, 114, .1),
                literal(0.2), rgba(0, 6, 219, .15),
                literal(0.3), rgba(0, 74, 255, .2),
                literal(0.4), rgba(0, 202, 255, .25),
                literal(0.5), rgba(73, 255, 154, .3),
                literal(0.6), rgba(171, 255, 59, .35),
                literal(0.7), rgba(255, 197, 3, .4),
                literal(0.8), rgba(255, 82, 1, 0.7),
                literal(0.9), rgba(196, 0, 1, 0.8),
                literal(0.95), rgba(121, 0, 0, 0.8)
            ),  // 11
            interpolate(
                linear(), heatmapDensity(),
                literal(0.01), rgba(0, 0, 0, 0.25),
                literal(0.25), rgba(229, 12, 1, .7),
                literal(0.30), rgba(244, 114, 1, .7),
                literal(0.40), rgba(255, 205, 12, .7),
                literal(0.50), rgba(255, 229, 121, .8),
                literal(1), rgba(255, 253, 244, .8)
            )
        )
    }

    private fun initHeatmapRadiusStops() {
        listOfHeatmapRadiusStops = arrayOf( // 0
            interpolate(
                linear(), zoom(),
                literal(6), literal(50),
                literal(20), literal(100)
            ),  // 1
            interpolate(
                linear(), zoom(),
                literal(12), literal(70),
                literal(20), literal(100)
            ),  // 2
            interpolate(
                linear(), zoom(),
                literal(1), literal(7),
                literal(5), literal(50)
            ),  // 3
            interpolate(
                linear(), zoom(),
                literal(1), literal(7),
                literal(5), literal(50)
            ),  // 4
            interpolate(
                linear(), zoom(),
                literal(1), literal(7),
                literal(5), literal(50)
            ),  // 5
            interpolate(
                linear(), zoom(),
                literal(1), literal(7),
                literal(15), literal(200)
            ),  // 6
            interpolate(
                linear(), zoom(),
                literal(1), literal(10),
                literal(8), literal(70)
            ),  // 7
            interpolate(
                linear(), zoom(),
                literal(1), literal(10),
                literal(8), literal(200)
            ),  // 8
            interpolate(
                linear(), zoom(),
                literal(1), literal(10),
                literal(8), literal(200)
            ),  // 9
            interpolate(
                linear(), zoom(),
                literal(1), literal(10),
                literal(8), literal(200)
            ),  // 10
            interpolate(
                linear(), zoom(),
                literal(1), literal(10),
                literal(8), literal(200)
            ),  // 11
            interpolate(
                linear(), zoom(),
                literal(1), literal(10),
                literal(8), literal(200)
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
                    heatmapColor(listOfHeatmapColors[index]),

                    // Increase the heatmap color weight weight by zoom level
                    // heatmap-intensity is a multiplier on top of heatmap-weight
                    heatmapIntensity(listOfHeatmapIntensityStops[index]),

// Adjust the heatmap radius by zoom level
                    heatmapRadius(
                        listOfHeatmapRadiusStops[index]
                    ),

                    heatmapOpacity(1f)
                )
// Add the heatmap layer to the map and above the "water-label" layer
                loadedMapStyle.addLayerAbove(layer, "waterway-label");
            }



    }


