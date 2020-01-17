package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.R.style
import android.graphics.Bitmap
import android.graphics.Color
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.com.example.wildfire_fixed_imports.getBitmap
import com.example.wildfire_fixed_imports.com.example.wildfire_fixed_imports.getBitmapFromVectorDrawable
import com.example.wildfire_fixed_imports.fireIconTarget
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


class MarkerController () {
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()


    private val addedMarkers = mutableListOf<Marker>()
    // Create an Icon object for the marker to use
    val fireIcon = applicationLevelProvider.fireIcon


    lateinit var fireBitmap: Bitmap
     var symbolManager:SymbolManager


init {

    symbolManager = SymbolManager(applicationLevelProvider.mapboxView, applicationLevelProvider.mapboxMap, applicationLevelProvider.mapboxStyle)
    symbolManager.iconAllowOverlap = true
    symbolManager.textAllowOverlap = true

    applicationLevelProvider.mapboxStyle


/*
    var bm = getBitmap(applicationLevelProvider.applicationContext, R.drawable.ic_fireicon_double)
    val bmp_Copy: Bitmap = bm.copy(Bitmap.Config.ARGB_8888, true)
*/


    val urbanArea = FillLayer("urban-areas-fill", "urban-areas")

    urbanArea.setProperties(
            fillColor(Color.parseColor("#ff0088")),
            fillOpacity(0.4f)
    )
    applicationLevelProvider.mapboxStyle.addLayerBelow(urbanArea, "water")


    val sauce = symbolManager.create( SymbolOptions()
            .withLatLng( LatLng(60.169091, 24.939876))
            .withIconImage(fireIconTarget)
            .withIconSize(0.1f)
            .withDraggable(true)
            )


    val sauce2 = symbolManager.create( SymbolOptions()
            .withLatLng( LatLng(20.169091, 24.939876))
            .withIconImage(fireIconTarget)
            .withIconSize(2.0f)
            .withDraggable(true)
    )

}

var count =0
// Add the marker to the map
    fun addMarker(targetLatLng: LatLng,title:String?,snippet:String?) :Int {

    //add the marker to map and set the newly created marker object to newMarkers

/*    val newMarker: Marker = applicationLevelProvider.mapboxMap.addMarker(MarkerOptions()
            .position(LatLng(50.0, 50.0))
            .title(title)
            .snippet(snippet)
            .icon(fireIcon)



    )

    //add newly added marker to list of markers in case of later need to remove or edit
    addedMarkers.add(newMarker)*/

    val sauce = SymbolOptions()
            .withLatLng(targetLatLng)
            .withIconImage(fireIconTarget) //set the below attributes according to your requirements
            .withIconSize(2.0f)
         //   .withIconOffset(arrayOf(0f, -1.5f))
            .withTextField(title)
            .withTextHaloColor("rgba(255, 255, 255, 100)")
            .withTextHaloWidth(5.0f)
            .withTextAnchor("top")
            .withTextOffset(arrayOf(0f, 1.5f))
            .withTextSize(12f)
            .withDraggable(true)


    val saucefam =symbolManager.create(sauce)
    //symbolManager.delete(saucefam)


        //finally return a reference to index of the newly created marker so the calling method can retain a reference for themselves
        return count++
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