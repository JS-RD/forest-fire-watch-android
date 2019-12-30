package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.graphics.Color
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer
import com.mapbox.mapboxsdk.style.layers.HillshadeLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.hillshadeHighlightColor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.hillshadeShadowColor
import com.mapbox.mapboxsdk.style.sources.RasterDemSource


/*
*           Map Controller
*   Map controller is repsonsible for recieving instructions on what to draw to the map and then draing those instructions.  i.e the
*  view model will let map controll know there is fire at x,y and map controller will then determine how best to display that fire, then draw that fire to the map
*
*
* */
class MapController (val targetMap:MapboxMap){

fun addbackgrondtomap(){
    targetMap.getStyle {
        val backgroundLayer = BackgroundLayer("background-layer")
        backgroundLayer.setProperties(PropertyFactory.backgroundColor(Color.BLUE))

        // Add background layer to map
        it.addLayer(backgroundLayer)
}
}
}