package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.app.Activity
import android.graphics.Color
import android.view.View
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.MainActivity
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import timber.log.Timber


/*
*           Map Controller
*   Map controller is repsonsible for recieving instructions on what to draw to the map and then draing those instructions.  i.e the
*  view model will let map controll know there is fire at x,y and map controller will then determine how best to display that fire, then draw that fire to the map
*
*
* */
class MapController() {

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

    //create heatmapcontroller scoped to class
    private var heatMapController: HeatMapController


    fun traceResult() {

    }

    init {

       Timber.i("loggo init")


        //create heatmapcontroller and assign reg to applicationlevelprovider
        heatMapController= HeatMapController()
        applicationLevelProvider.heatMapController=heatMapController


        if (currentActivity is MainActivity) {
            currentActivity.setFabOnclick { heatMapController.initializeHeatMapExtended() }
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



    }


