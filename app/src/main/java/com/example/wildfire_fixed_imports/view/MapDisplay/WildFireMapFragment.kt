package com.example.wildfire_fixed_imports.view.MapDisplay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.MainActivity
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.com.example.wildfire_fixed_imports.LatLng
import com.example.wildfire_fixed_imports.com.example.wildfire_fixed_imports.getBitmapFromVectorDrawable
import com.example.wildfire_fixed_imports.fireIconTarget
import com.example.wildfire_fixed_imports.viewmodel.MasterController
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.MapViewModel
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private lateinit var masterController: MasterController


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
             val style = Style.TRAFFIC_DAY
            /* private static final String ICON_ID = "ICON_ID";
             private static final String LAYER_ID = "LAYER_ID";
            val style =Style.Builder().fromUri()
                    .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                    .withProperties(PropertyFactory.iconImage(ICON_ID),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true),
                            iconOffset(new Float[] {0f, -9f}))*/
              myMapboxMap.setStyle(style) {

                  val id = R.drawable.ic_fireicon
                  applicationLevelProvider.fireIconAlt = getBitmapFromVectorDrawable(applicationLevelProvider.applicationContext,id)
                  it.addImage(fireIconTarget,
                          applicationLevelProvider.fireIconAlt
                  )

                  it.transition = TransitionOptions(0, 0, false)

                  mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(
                          12.099, -79.045), 3.0))

                 (applicationLevelProvider.currentActivity as MainActivity).enableLocationComponent(it)
                  applicationLevelProvider.mapboxStyle=it

                  masterController= MasterController()
                  applicationLevelProvider.masterController=masterController


                  mapViewModel.setMyMasterController(masterController)
                  CoroutineScope(Dispatchers.Main).launch {
                      val locale = (applicationLevelProvider.currentActivity as MainActivity).getLatestLocation()
                      mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                              locale!!.LatLng(), 6.0), 12000);
                  }
             }










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