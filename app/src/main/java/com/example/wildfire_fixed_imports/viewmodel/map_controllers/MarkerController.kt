package com.example.wildfire_fixed_imports.viewmodel.map_controllers

import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.fireIconTarget
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions


class MarkerController () {
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()


    private val addedMarkers = mutableListOf<Marker>()


    var symbolManager:SymbolManager = applicationLevelProvider.symbolManager


init {
    symbolManager.iconAllowOverlap = true
    symbolManager.textAllowOverlap = true



/*
    val urbanArea = FillLayer("urban-areas-fill", "urban-areas")

    urbanArea.setProperties(
            fillColor(Color.parseColor("#ff0088")),
            fillOpacity(0.4f)
    )
    applicationLevelProvider.mapboxStyle.addLayerBelow(urbanArea, "water")*/
}

var count =0

    fun addSymbol(targetLatLng: LatLng, title:String?, snippet:String?) :Int {

    //add the marker to map and set the newly created marker object to newMarkers


    //add newly added marker to list of markers in case of later need to remove or edit

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
            .withDraggable(false)


    val saucefam =symbolManager.create(sauce)
    //symbolManager.delete(saucefam)
/*        symbolManager.addClickListener( OnSymbolClickListener() {
            @Override
            public void onAnnotationClick(Symbol symbol) {
                Toast.makeText(SymbolListenerActivity.this,
                        getString(R.string.clicked_symbol_toast), Toast.LENGTH_SHORT).show();
                symbol.setIconImage(MAKI_ICON_CAFE);
                symbolManager.update(symbol);
            }
        })*/

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