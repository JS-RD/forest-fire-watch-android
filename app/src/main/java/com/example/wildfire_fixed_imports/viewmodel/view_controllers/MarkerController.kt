package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.view.View
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap

class MarkerController () {
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val targetMap: MapboxMap by lazy {
        applicationLevelProvider.mapboxMap
    }
    private val mapboxView: View by lazy {
        applicationLevelProvider.mapboxView
    }

    private val addedMarkers = mutableListOf<Marker>()

    fun addMarker(targetLatLng: LatLng,title:String?) :Int {

        //add the marker to map and set the newly created marker object to newMarkers
        val newMarker: Marker = targetMap.addMarker(MarkerOptions()
            .position(targetLatLng)
            .title (title)
        )
        //add newly added marker to list of markers in case of later need to remove or edit
        addedMarkers.add(newMarker)

        //finally return a reference to index of the newly created marker so the calling method can retain a reference for themselves
        return addedMarkers.indexOf(newMarker)
    }

    fun removeMarker(indexLocation:Int) {
        //take in the index of a marker, find the marker in the list, use that object to remove the market from the map
        // and then remove that makrker from this list
        val markerToRemove = addedMarkers[indexLocation]

        targetMap.removeMarker(markerToRemove)

        addedMarkers.removeAt(indexLocation)
    }

    fun getMarkerAtIndex (indexLocation:Int) : Marker {
        return addedMarkers[indexLocation]
    }

    //test this method
 fun editMarker(indexLocation:Int,marker:Marker) {
     addedMarkers[indexLocation] =marker
     TODO("TEST THIS METHOD")

 }

}