package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.Toast
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.MainActivity
import com.example.wildfire_fixed_imports.R
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer
import com.mapbox.mapboxsdk.style.layers.HeatmapLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
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
class MapController(val targetMap: MapboxMap, val mapboxView: View) {

    init {
        //this is here stirctly while testing heatmap methods
        initializeHeatMap()
    }


    fun addbackgroundtomap() {
        targetMap.getStyle {
            val backgroundLayer = BackgroundLayer("background-layer")
            backgroundLayer.setProperties(PropertyFactory.backgroundColor(Color.BLUE))

            // Add background layer to map
            it.addLayer(backgroundLayer)
        }
    }


    //heat map functions experimental:


    private val HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID"
    private val HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID"
    private val mapView: MapView? = null
    lateinit var listOfHeatmapColors: Array<Expression>
    lateinit var listOfHeatmapRadiusStops: Array<Expression>
    lateinit var listOfHeatmapIntensityStops: Array<Float>
    private var index = 0


    fun initializeHeatMap() {
        val currentAct = ApplicationLevelProvider.getApplicaationLevelProviderInstance().currentActivity
        if (currentAct is MainActivity) {
            (currentAct as MainActivity).setFabOnclick {
                Toast.makeText(currentAct.applicationContext, "the Jank succeeded!", Toast.LENGTH_LONG ).show()
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
                val currentAct = ApplicationLevelProvider.getApplicaationLevelProviderInstance().currentActivity

                if (currentAct is MainActivity) {
                    (currentAct as MainActivity).setFabOnclick {
                        Toast.makeText(currentAct.applicationContext, "the Jank succeeded!", Toast.LENGTH_LONG ).show()
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




            fun initHeatmapColors() {
                TODO()
            }

            fun initHeatmapRadiusStops() {
                TODO()
            }

            fun initHeatmapIntensityStops() {
                TODO()
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


