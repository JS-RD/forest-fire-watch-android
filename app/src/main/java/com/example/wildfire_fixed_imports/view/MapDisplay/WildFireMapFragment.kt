package com.example.wildfire_fixed_imports.view.MapDisplay

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.MainActivity
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.viewmodel.view_controllers.MapController
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.MapViewModel
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.app_bar_main.*
import timber.log.Timber


class WildFireMapFragment : Fragment() {
    // get the correct instance of application level provider
    val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    init {
        //set this fragment as the map fragment in ApplicationLevelProvider
        applicationLevelProvider.mapFragment = this
    }



    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapboxMap:MapboxMap
    private lateinit var mapView: MapView
    private lateinit var mapController:MapController


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


             mapController= MapController()
             applicationLevelProvider.mapController=mapController

            myMapboxMap.setStyle(Style.MAPBOX_STREETS) {
                (applicationLevelProvider.currentActivity as MainActivity).enableLocationComponent(it)
            }

             mapViewModel.setMyTargetMap(mapController)


             /*imageViewArrow.setOnClickListener { _ -> bottomSheetLayout.toggle() }
             bottomSheetLayout.setOnProgressListener { progress -> rotateArrow(progress)}*/
        }



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)



    }

    private fun rotateArrow(progress: Float) {
        imageViewArrow.rotation = -180 * progress
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