package com.example.wildfire_fixed_imports.view.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.viewmodel.view_controllers.MapController
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.HomeViewModel
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

class HomeFragment : Fragment() {
    private lateinit var listener: OnFabHomePress
    private lateinit var homeViewModel: HomeViewModel
    private var mapView: MapView? = null
    var mapboxMap:MapboxMap? = null
    lateinit var mapController:MapController


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)

        //val textView: TextView = root.findViewById(R.id.text_home)
         Mapbox.getInstance(this.context!!,  getString(R.string.mapbox_access_token))


        val root = inflater.inflate(R.layout.fragment_home, container, false)



        mapView = root.findViewById(R.id.mapview_main)
        mapView?.onCreate(savedInstanceState)
         mapView?.getMapAsync { myMapboxMap ->


             mapboxMap = myMapboxMap

             mapController= MapController(myMapboxMap)


            myMapboxMap.setStyle(Style.MAPBOX_STREETS) {

                // Map is set up and the style has loaded. Now you can add data or make other map adjustments


            }

        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
 //       (activity as MainActivity).methodName()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFabHomePress) {
            listener = context
        } else {
            throw ClassCastException(
                context.toString() + " must implement OnDogSelected.")
        }
    }

    public fun mapIT(){
    print("oit got to fragment")
       mapController.addbackgrondtomap()
    }

    interface OnFabHomePress {
        fun onFabPRess()
    }
}