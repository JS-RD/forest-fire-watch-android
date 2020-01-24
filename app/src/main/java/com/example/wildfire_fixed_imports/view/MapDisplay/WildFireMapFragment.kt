package com.example.wildfire_fixed_imports.view.MapDisplay

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.viewmodel.MasterCoordinator
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.MapViewModel
import com.example.wildfire_fixed_imports.zLoc
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property.NONE
import com.mapbox.mapboxsdk.style.layers.Property.VISIBLE
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class WildFireMapFragment : zLoc() {
    // get the correct instance of application level provider

    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"


    init {
        //set this fragment as the map fragment in ApplicationLevelProvider
        applicationLevelProvider.mapFragment = this
    }

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapboxMap:MapboxMap
    private lateinit var mapView: MapView
    private lateinit var masterCoordinator: MasterCoordinator


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


     // Initialize Home View Model
        mapViewModel = ViewModelProviders.of(this, applicationLevelProvider.mapViewModelFactory).get(
                MapViewModel::class.java)


      Mapbox.getInstance(this.context!!,  getString(R.string.mapbox_access_token))


        val root = inflater.inflate(R.layout.fragment_home, container, false)



        mapView = root.findViewById(R.id.mapview_main)
        mapView.onCreate(savedInstanceState)


        if (applicationLevelProvider.internetPermission &&
                (applicationLevelProvider.coarseLocationPermission ||
                        applicationLevelProvider.fineLocationPermission)) {
            finishLoading()
            applicationLevelProvider.currentActivity.locationInit()
        } else {
            initPermissions()
        }



        return root
    }

    fun finishLoading() {
        mapView.getMapAsync { myMapboxMap ->
            //set the applicationLevelProvider properties to reflect the loaded map
            Timber.i("map loaded async ${System.currentTimeMillis()}")
            applicationLevelProvider.mapboxMap = myMapboxMap
            mapboxMap = myMapboxMap
            applicationLevelProvider.mapboxView = mapView
            val style = Style.SATELLITE

            myMapboxMap.setStyle(style) {


                it.transition = TransitionOptions(0, 0, false)

                it.resetIconsForNewStyle()

                applicationLevelProvider.currentActivity.enableLocationComponent(it)
                applicationLevelProvider.mapboxStyle = it


                val symbolManager = SymbolManager(applicationLevelProvider.mapboxView, applicationLevelProvider.mapboxMap, applicationLevelProvider.mapboxStyle)
                applicationLevelProvider.symbolManager = symbolManager
                mapViewModel.onMapLoaded()
                Timber.w("$TAG config")

                // start the fire service/immediately to start retrieving fires
                CoroutineScope(Dispatchers.IO).launch {
                    mapViewModel.startFireRetrieval()
                    mapViewModel.startAQIRetrieval()
                }

            }

            applicationLevelProvider.fireBSIcon.setOnClickListener {
                mapboxMap.getStyle { style ->
                    val layer: Layer? = style.getLayer(FIRE_SYMBOL_LAYER)
                    if (layer != null) {
                        if (VISIBLE == layer.visibility.getValue()) {
                            layer.setProperties(visibility(NONE))
                        } else {
                            layer.setProperties(visibility(VISIBLE))
                        }
                    }
                }
                mapViewModel.toggleFireRetrieval()
                Timber.i("$TAG toggle fire")
            }
            applicationLevelProvider.aqiCloudBSIcon.setOnClickListener {
                mapViewModel.toggleAQIRetrieval()
                mapboxMap.getStyle { style ->
                    val layer: Layer? = style.getLayer("count")
                    if (layer != null) {
                        if (VISIBLE == layer.visibility.getValue()) {
                            layer.setProperties(visibility(NONE))
                        } else {
                            layer.setProperties(visibility(VISIBLE))
                        }
                    }
                    val layer1: Layer? = style.getLayer("unclustered-aqi-points")
                    if (layer1 != null) {
                        if (VISIBLE == layer1.visibility.getValue()) {
                            layer1.setProperties(visibility(NONE))
                        } else {
                            layer1.setProperties(visibility(VISIBLE))
                        }
                    }

                    for (i in 0..2) {
                        val layer: Layer? = style.getLayer("cluster-$i")
                        if (layer != null) {
                            if (VISIBLE == layer.visibility.getValue()) {
                                layer.setProperties(visibility(NONE))
                            } else {
                                layer.setProperties(visibility(VISIBLE))
                            }
                        }
                    }
                }
                Timber.i("$TAG toggle aqi")
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.i("on request == before while loop permission: ${permissions.toString()} requestcode: $requestCode grantresults: ${grantResults.toString()} ")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                Timber.i("on request == after while loop fine location")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    applicationLevelProvider.fineLocationPermission = true
                    applicationLevelProvider.showSnackbar("Fine Location granted successfully", Snackbar.LENGTH_SHORT)
                    applicationLevelProvider.currentActivity.locationInit()
                    finishLoading()

                } else {
                    applicationLevelProvider.fineLocationPermission = false
                    applicationLevelProvider.showSnackbar("Fine Location not granted", Snackbar.LENGTH_SHORT)
                }
                return
            }
            MY_PERMISSIONS_REQUEST_INTERNET -> {
                Timber.i("on request == after while loop internet")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    applicationLevelProvider.internetPermission = true
                    applicationLevelProvider.showSnackbar("Internet granted successfully", Snackbar.LENGTH_SHORT)
                    finishLoading()
                } else {
                    applicationLevelProvider.internetPermission = false
                    applicationLevelProvider.showSnackbar("Internet not granted", Snackbar.LENGTH_SHORT)
                    //
                    TODO("CAUSE APPLICATION TO EXIT HERE")
                }
                return
            }

            MY_PERMISSIONS_COARSE_LOCATION -> {
                Timber.i("on request == after while loop internet")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    applicationLevelProvider.coarseLocationPermission = true
                    applicationLevelProvider.showSnackbar("Internet granted successfully", Snackbar.LENGTH_SHORT)
                    applicationLevelProvider.currentActivity.locationInit()
                    finishLoading()
                } else {
                    applicationLevelProvider.coarseLocationPermission = false
                    applicationLevelProvider.showSnackbar("Internet not granted", Snackbar.LENGTH_SHORT)
                    //
                    TODO("CAUSE APPLICATION TO EXIT HERE")
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                Timber.i("on request == after while loop else")
                // Ignore all other requests.
            }
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)



    }


/*    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFabHomePress) {
            listener = context
        } else {
            throw ClassCastException(
                context.toString() + " must implement OnFabHomePress.")
        }
    }



    interface OnFabHomePress {
        fun onFabPRess()
    }*/



    //mapbox boilerplate for surviving config changes
    override fun onStart(): Unit {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}