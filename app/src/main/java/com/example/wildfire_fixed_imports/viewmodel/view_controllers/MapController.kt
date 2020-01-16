package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.MainActivity
import com.example.wildfire_fixed_imports.model.DSFires
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.function.ToDoubleBiFunction


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

    private val TAG = "MapController"

    var initialized=false

    fun traceResult() {

    }

    init {

       Timber.i("$TAG init")
        // Create the observer which updates the UI.
        val fireObserver = Observer<List<DSFires>> { list ->
            // Update the UI, in this case, a TextView.
            Timber.i("$TAG init create fire observer")
            if (!initialized) {
                Timber.i("$TAG new list reached observer ${list.toString()}")
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



        var iterator = 0
        val arrayOfStyles:ArrayList<String> = arrayListOf(
                Style.DARK,
                Style.MAPBOX_STREETS,
                Style.OUTDOORS,
                Style.LIGHT,
                Style.SATELLITE,
                Style.SATELLITE_STREETS,
                Style.TRAFFIC_DAY,
                Style.TRAFFIC_NIGHT)


        if (currentActivity is MainActivity) {
            currentActivity.setFabOnclick {
                Timber.i("$TAG iterator = ${iterator} \n size = ${arrayOfStyles.size}")
                    if(iterator>=arrayOfStyles.size-1) {
                        Timber.i("$TAG iterator>=arrayOfStyles.siz")
                        iterator=0
                    }
                else {
                        iterator++
                        Timber.i("$TAG iterator++ = $iterator")
                    }
                Timber.i("$TAG setting map style to ${arrayOfStyles[iterator]}")
                targetMap.setStyle(arrayOfStyles[iterator])
                   // heatMapController.initializeHeatMapExtended()
                }
                }


        }


    fun addAllFires(DSFires:List<DSFires>) {
        for (i in DSFires.indices) {
            val current = DSFires[i]
            Timber.i("$i and ${current.toString()}")

            CoroutineScope(Dispatchers.Main).launch {

                markerController.addMarker(current.latlng(), current.name, current.type)

            }

        }

    }

/*
    suspend fun popit(){

        TODO("this is an async example that requires you to later .await() ")
        val result = CoroutineScope(Dispatchers.IO).async {
            applicationLevelProvider.retrofitDSService.getDSFireLocations()

        }


            Timber.i(System.currentTimeMillis().toString())


        TODO("this is how you would get out of a coroutine scope and back to a regular nonthread pattern ")
        CoroutineScope(Dispatchers.IO).launch {
            Timber.i(System.currentTimeMillis().toString())
            Toast.makeText(applicationLevelProvider.applicationContext, result.await().toString(), Toast.LENGTH_SHORT).show()
            delay(2000)
       popit2()
        }
    }
    fun popit2() {
        Toast.makeText(applicationLevelProvider.applicationContext, "this was not suspended", Toast.LENGTH_SHORT).show()

    }*/

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


