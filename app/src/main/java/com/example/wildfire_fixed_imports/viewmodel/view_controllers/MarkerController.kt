package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.hardware.camera2.params.InputConfiguration
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.style.layers.Property

class MarkerController () {
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()


    private val mapboxView: View by lazy {
        applicationLevelProvider.mapboxView
    }

    private val addedMarkers = mutableListOf<Marker>()
    // Create an Icon object for the marker to use
    val fireIcon = applicationLevelProvider.fireIcon


// Add the marker to the map
    fun addMarker(targetLatLng: LatLng,title:String?,snippet:String?) :Int {

        //add the marker to map and set the newly created marker object to newMarkers

        val newMarker: Marker = applicationLevelProvider.mapboxMap.addMarker(MarkerOptions()
            .position(targetLatLng)
            .title (title)
            .snippet(snippet)
            .icon(fireIcon)


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

        applicationLevelProvider.mapboxMap.removeMarker(markerToRemove)

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