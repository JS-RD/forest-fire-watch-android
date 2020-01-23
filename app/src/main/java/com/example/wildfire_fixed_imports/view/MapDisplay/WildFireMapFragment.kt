package com.example.wildfire_fixed_imports.view.MapDisplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.*
import com.example.wildfire_fixed_imports.util.StackTraceInfo
import com.example.wildfire_fixed_imports.util.className
import com.example.wildfire_fixed_imports.util.fileName
import com.example.wildfire_fixed_imports.util.resetIconsForNewStyle
import com.example.wildfire_fixed_imports.viewmodel.MasterCoordinator
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.MapViewModel
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import kotlinx.android.synthetic.main.app_bar_main.*
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class WildFireMapFragment : Fragment() {
    // get the correct instance of application level provider
    val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

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
                applicationLevelProvider.mapboxStyle=it


                val  symbolManager = SymbolManager(applicationLevelProvider.mapboxView, applicationLevelProvider.mapboxMap, applicationLevelProvider.mapboxStyle)
                applicationLevelProvider.symbolManager=symbolManager
                mapViewModel.onMapLoaded()
                Timber.w("$TAG config")

                // start the fire service/immediately to start retrieving fires
                CoroutineScope(Dispatchers.IO).launch {
                    mapViewModel.startFireRetrieval()
                    mapViewModel.startAQIRetrieval()
                }

            }

            applicationLevelProvider.fireBSIcon.setOnClickListener {
                mapViewModel.toggleFireRetrieval()
                Timber.i("$TAG toggle fire")
            }
            applicationLevelProvider.aqiCloudBSIcon.setOnClickListener {
                mapViewModel.toggleAQIRetrieval()
                Timber.i("$TAG toggle aqi")
            }






            /*imageViewArrow.setOnClickListener { _ -> bottomSheetLayout.toggle() }
            bottomSheetLayout.setOnProgressListener { progress -> rotateArrow(progress)}*/
        }



        return root
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
}