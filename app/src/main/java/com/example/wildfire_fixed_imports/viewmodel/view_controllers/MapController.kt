package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.MainActivity
import com.example.wildfire_fixed_imports.model.DSFires
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val heatMapController = applicationLevelProvider.heatMapController

    //grab the viewmodel
    private val mapViewModel = applicationLevelProvider.appMapViewModel

    //markercontroller ref
    private val markerController = applicationLevelProvider.markerController


    var initialized=false

    fun traceResult() {

    }

    init {

       Timber.i("loggo init")
        // Create the observer which updates the UI.
        val fireObserver = Observer<List<DSFires>> { list ->
            // Update the UI, in this case, a TextView.
            Timber.i("loggo init in observer")
            if (!initialized) {
                Timber.i("new list reached observer ${list.toString()}")
                removeAllFires()
                addAllFires(list)
            }
            else {

                addAllFires(list)
            }

        }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mapViewModel.fireData.observe(currentActivity as LifecycleOwner, fireObserver)

        // start the fire service immediately to start retrieving fires

        CoroutineScope(Dispatchers.IO).launch {
            mapViewModel.startFireRetrieval()
        }






      //  val retrofitDSService =applicationLevelProvider.retrofitDSService
        if (currentActivity is MainActivity) {
            currentActivity.setFabOnclick {



                /*CoroutineScope(Dispatchers.IO).launch {
                    val result =retrofitDSService.getDSFireLocations()
                    Timber.i(result.toString())
                    for (i in result.indices) {
                        Timber.i(result[i].name)
                        Timber.i(result[i].location[0].toString())
                        Timber.i(result[i].location[1].toString())
                        Timber.i(result[i].latlng().toString())
                        Timber.i(result[i].type)
                    }
                }*/
                    heatMapController.initializeHeatMapExtended()
                }
                }


        }

    fun addAllFires(DSFires:List<DSFires>) {
        for (i in DSFires.indices) {
            val current = DSFires[i]
            Timber.i("$i and ${current.toString()}")
            markerController.addMarker(current.latlng(),current.name,current.type)
        }

    }

    fun removeAllFires() {
        targetMap.markers.removeAll(targetMap.markers)
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


