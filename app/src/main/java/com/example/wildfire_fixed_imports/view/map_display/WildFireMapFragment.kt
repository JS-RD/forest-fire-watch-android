package com.example.wildfire_fixed_imports.view.map_display

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.view.EntranceActivity
import com.example.wildfire_fixed_imports.view.MainActivity
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.MapViewModel
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.CarmenFeature
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.exceptions.MapboxConfigurationException
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment
import com.mapbox.mapboxsdk.plugins.places.autocomplete.ui.PlaceSelectionListener
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.layers.Property.NONE
import com.mapbox.mapboxsdk.style.layers.Property.VISIBLE
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class WildFireMapFragment : Fragment() {
    // get the correct instance of application level provider
    private val applicationLevelProvider: ApplicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"


    override fun onAttach(context: Context) {
        super.onAttach(context)
        applicationLevelProvider.bottomSheet?.visibility = View.VISIBLE
        applicationLevelProvider.aqiGaugeExpanded?.visibility = View.VISIBLE

        if (applicationLevelProvider.dataRepository.fireGeoJson.value.isNullOrEmpty()) {

            startActivity(Intent(this.context, EntranceActivity::class.java))
        }
    }

    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapboxMap: MapboxMap
    private lateinit var autocompleteFragment: PlaceAutocompleteFragment
    private lateinit var supportFragmentManager: FragmentManager
    private var mapView: MapView? = null

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        // Initialize Home View Model
        mapViewModel = ViewModelProviders.of(this, applicationLevelProvider.mapViewModelFactory).get(
                MapViewModel::class.java)
        applicationLevelProvider.appMapViewModel = mapViewModel

        mapView = v.findViewById(R.id.mapview_main)
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { myMapboxMap ->
            //set the applicationLevelProvider properties to reflect the loaded map
            Timber.i("map loaded async ${System.currentTimeMillis()}")
            applicationLevelProvider.mapboxMap = myMapboxMap
            mapboxMap = myMapboxMap
            applicationLevelProvider.mapboxView = mapView as MapView

            finishLoading()
            Timber.i("First finish loading")
            applicationLevelProvider.currentActivity.locationInit()

        }

        if (savedInstanceState == null) {
            val placeOptions = PlaceOptions.builder()
                    .toolbarColor(ContextCompat.getColor(applicationLevelProvider.applicationContext, R.color.colorPrimary))
                    .statusbarColor(Color.YELLOW)
                    .hint("Begin searching...")
                    .build()

            autocompleteFragment = PlaceAutocompleteFragment.newInstance(
                    Mapbox.getAccessToken() ?: throw MapboxConfigurationException(),
                    placeOptions
            )
            supportFragmentManager =this.activity?.supportFragmentManager ?: applicationLevelProvider.currentActivity.supportFragmentManager
            val container= view!!.findViewById<FrameLayout>(R.id.fragment_container)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, autocompleteFragment, PlaceAutocompleteFragment.TAG)
            transaction.commit()
        } else {
            autocompleteFragment = supportFragmentManager.findFragmentByTag(PlaceAutocompleteFragment.TAG) as PlaceAutocompleteFragment
        }

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: CarmenFeature) {
                Toast.makeText(applicationLevelProvider.applicationContext,
                        carmenFeature.text()



                        , Toast.LENGTH_LONG).show()
                val mapboxGeocoding = MapboxGeocoding.builder()
                        .accessToken(applicationLevelProvider.mapboxToken)
                        .query(carmenFeature.text() ?: "uhnolsax battmon")
                        .build()

                mapboxGeocoding.enqueueCall(object : Callback<GeocodingResponse> {
                    override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {

                        val results = response.body()!!.features()
                        Timber.i( "onResponse: " + results.toString())
                        if (results.size > 0) {

                            // Log the first results Point.
                            val firstResultPoint = results[0].center()
                            Timber.i( "onResponse: " + firstResultPoint!!.toString())

                        } else {

                            // No result for your request were found.
                            Timber.i( "onResponse: No result found")

                        }
                    }

                    override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {
                        throwable.printStackTrace()
                    }
                })


                Timber.i("$\n\n{carmenFeature.text()}  carmen feature.txt \n\n"
                + "${mapboxGeocoding} mapboxgeocoding result\n"
                )

                // finish()
            }

            override fun onCancel() {
                //finish()
            }
        })

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    fun finishLoading() {
        val style = Style.SATELLITE
        mapboxMap.setStyle(style) { it ->

            it.transition = TransitionOptions(0, 0, false)

            it.resetIconsForNewStyle()

            applicationLevelProvider.currentActivity.enableLocationComponent(it)
            applicationLevelProvider.mapboxStyle = it


            mapViewModel.onMapLoaded()
            Timber.w("$TAG config")


            applicationLevelProvider.dataRepository.aqiGeoJson.observe(this, Observer { string ->
                if (string.isNotBlank()) {
                    mapViewModel.triggerMapRedraw()
                }
            })
            applicationLevelProvider.dataRepository.aqiGeoJson.observe(this, Observer { string ->
                if (string.isNotBlank()) {
                    mapViewModel.triggerMapRedraw()
                }
            })
            applicationLevelProvider.dataRepository.aqiNearestNeighborGeoJson.observe(this, Observer { string ->
                if (string.isNotBlank()) {
                    mapViewModel.doExperimental()
                }
            }
            )
            // start the fire service/immediately to start retrieving fires


        }


        applicationLevelProvider.switchFireBSIcon.setOnClickListener {
            fireToggleButtonOnClick()
        }
        applicationLevelProvider.switchAqiCloudBSIcon.setOnClickListener {
            aqiToggleButtonOnClick()
        }
        applicationLevelProvider.fireImageView.setOnClickListener {
            fireToggleButtonOnClick()
        }
        applicationLevelProvider.cloudImageView.setOnClickListener {
            aqiToggleButtonOnClick()
        }

        applicationLevelProvider.btmsheetToggleIndex?.setOnClickListener {
            aqiToggleBaseText()
            aqiToggleCompositeText()
        }

        applicationLevelProvider.btmSheetToggleRadius?.setOnClickListener {
            aqiToggleBaseHML()
            aqiToggleCompositeHML()
        }

        applicationLevelProvider.btmSheetTvIndex?.text = "Air Quality index"
        applicationLevelProvider.btmSheetTvRadius?.text = "Air Quality Radius"

    }

    fun aqiToggleBaseText() {
        mapboxMap.getStyle { style ->


            val layer2: Layer? = style.getLayer(AQI_BASE_TEXT_LAYER)
            if (layer2 != null) {
                if (VISIBLE == layer2.visibility.getValue()) {
                    layer2.setProperties(visibility(NONE))
                    applicationLevelProvider.aqiBaseTextLayerVisibility = NONE
                } else {
                    layer2.setProperties(visibility(VISIBLE))
                    applicationLevelProvider.aqiBaseTextLayerVisibility = VISIBLE
                }

            }

        }
    }

    fun aqiToggleCompositeText() {

        mapboxMap.getStyle { style ->

            val layer: Layer? = style.getLayer(AQI_CLUSTERED_COUNT_LAYER)
            if (layer != null) {
                if (VISIBLE == layer.visibility.getValue()) {
                    layer.setProperties(visibility(NONE))
                    applicationLevelProvider.aqiClusterTextLayerVisibility = NONE

                } else {
                    layer.setProperties(visibility(VISIBLE))
                    applicationLevelProvider.aqiClusterTextLayerVisibility = VISIBLE

                }
            }
        }
    }

    fun aqiToggleBaseHML() {
        mapboxMap.getStyle { style ->

            val layer1: Layer? = style.getLayer(AQI_HEATLITE_BASE_LAYER)
            if (layer1 != null) {
                if (VISIBLE == layer1.visibility.getValue()) {
                    layer1.setProperties(visibility(NONE))
                    applicationLevelProvider.aqiBaseHMLLayerVisibility = NONE
                } else {
                    layer1.setProperties(visibility(VISIBLE))
                    applicationLevelProvider.aqiBaseHMLLayerVisibility = VISIBLE
                }
            }
        }
    }

    fun aqiToggleCompositeHML() {

        mapboxMap.getStyle { style ->
            for (i in 0..2) {
                val layer: Layer? = style.getLayer("cluster-hml-$i")
                if (layer != null) {
                    if (VISIBLE == layer.visibility.getValue()) {
                        layer.setProperties(visibility(NONE))
                        applicationLevelProvider.aqiClusterHMLLayerVisibility = NONE
                    } else {
                        layer.setProperties(visibility(VISIBLE))
                        applicationLevelProvider.aqiClusterHMLLayerVisibility = VISIBLE
                    }
                }
            }
        }
    }


    fun fireToggleButtonOnClick() {
        opacitySwitch(applicationLevelProvider.fireImageView)
        mapboxMap.getStyle { style ->
            val layer: Layer? = style.getLayer(FIRE_SYMBOL_LAYER)
            if (layer != null) {
                if (VISIBLE == layer.visibility.getValue()) {
                    layer.setProperties(visibility(NONE))
                    applicationLevelProvider.fireLayerVisibility = NONE
                    applicationLevelProvider.switchFireBSIcon.setChecked(false)
                } else {
                    layer.setProperties(visibility(VISIBLE))
                    applicationLevelProvider.fireLayerVisibility = VISIBLE
                    applicationLevelProvider.switchFireBSIcon.setChecked(true)
                }
            }
        }
        mapViewModel.toggleFireRetrieval()
        Timber.i("$TAG toggle fire")


    }

    private fun toggleAQIDetailSwitchs(switchOn: Boolean) {
        if (switchOn) {

            applicationLevelProvider.btmSheetToggleRadius?.setChecked(true)
            applicationLevelProvider.btmsheetToggleIndex?.setChecked(true)
        } else {

            applicationLevelProvider.btmSheetToggleRadius?.setChecked(false)
            applicationLevelProvider.btmsheetToggleIndex?.setChecked(false)
        }
    }

    fun aqiToggleButtonOnClick() {
        opacitySwitch(applicationLevelProvider.cloudImageView)
        mapViewModel.toggleAQIRetrieval()
        mapboxMap.getStyle { style ->


            val layer2: Layer? = style.getLayer(AQI_BASE_TEXT_LAYER)
            if (layer2 != null) {
                if (VISIBLE == layer2.visibility.getValue()) {
                    layer2.setProperties(visibility(NONE))
                    applicationLevelProvider.aqiBaseTextLayerVisibility = NONE
                    applicationLevelProvider.switchAqiCloudBSIcon.setChecked(false)
                    toggleAQIDetailSwitchs(false)
                } else {
                    layer2.setProperties(visibility(VISIBLE))
                    applicationLevelProvider.aqiBaseTextLayerVisibility = VISIBLE
                    applicationLevelProvider.switchAqiCloudBSIcon.setChecked(true)
                    toggleAQIDetailSwitchs(true)
                }
            }


            val layer: Layer? = style.getLayer(AQI_CLUSTERED_COUNT_LAYER)
            if (layer != null) {
                if (VISIBLE == layer.visibility.getValue()) {
                    layer.setProperties(visibility(NONE))
                    applicationLevelProvider.aqiLayerVisibility = NONE
                    applicationLevelProvider.aqiClusterTextLayerVisibility = NONE

                } else {
                    layer.setProperties(visibility(VISIBLE))
                    applicationLevelProvider.aqiLayerVisibility = VISIBLE
                    applicationLevelProvider.aqiClusterTextLayerVisibility = VISIBLE

                }
            }

            val layer1: Layer? = style.getLayer(AQI_HEATLITE_BASE_LAYER)
            if (layer1 != null) {
                if (VISIBLE == layer1.visibility.getValue()) {
                    layer1.setProperties(visibility(NONE))
                    applicationLevelProvider.aqiBaseHMLLayerVisibility = NONE
                } else {
                    layer1.setProperties(visibility(VISIBLE))
                    applicationLevelProvider.aqiBaseHMLLayerVisibility = VISIBLE
                }
            }
            for (i in 0..2) {
                val layer: Layer? = style.getLayer("cluster-hml-$i")
                if (layer != null) {
                    if (VISIBLE == layer.visibility.getValue()) {
                        layer.setProperties(visibility(NONE))
                        applicationLevelProvider.aqiClusterHMLLayerVisibility = NONE
                    } else {
                        layer.setProperties(visibility(VISIBLE))
                        applicationLevelProvider.aqiClusterHMLLayerVisibility = VISIBLE
                    }
                }
            }
        }
        Timber.i("$TAG toggle aqi")
    }


    fun opacitySwitch(view: ImageView) {
        if (view.alpha == 1F) {
            view.alpha = 0.5F
        } else {
            view.alpha = 1F
        }
    }


    //mapbox boilerplate for surviving config changes
    override fun onStart(): Unit {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()

        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onDetach() {
        super.onDetach()
    }


}