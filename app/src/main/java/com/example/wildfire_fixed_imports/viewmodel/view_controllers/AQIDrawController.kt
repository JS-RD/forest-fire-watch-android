package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.app.Activity
import android.view.View
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.sendSelect

import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style

import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


/*
*
*  AQIDrawController is responsible for drawing
*
* */
class AQIDrawController() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val targetMap: MapboxMap by lazy {
        applicationLevelProvider.mapboxMap
    }
    private val mapboxView: View by lazy {
        applicationLevelProvider.mapboxView
    }
    //additional dependency injection
    private val currentActivity: Activity = applicationLevelProvider.currentActivity


    //heat map functions experimental:


    private val HEATMAP_SOURCE_ID = "HEATMAP_SOURCE_ID"
    private val HEATMAP_LAYER_ID = "HEATMAP_LAYER_ID"
    lateinit var listOfHeatmapColors: Array<Expression>
    lateinit var listOfHeatmapRadiusStops: Array<Expression>
    lateinit var listOfHeatmapIntensityStops: Array<Float>
    private var index = 0
    private var lastSource: GeoJsonSource = GeoJsonSource("sauce")
    private var heatmapHasBeennitialized = false
    //style of underlying map, Style.DARK, Style.LIGHT, or Style.SATELLITE are suggested values.
    private val HEATMAP_STYLE = Style.DARK

    fun rec(){


    }
}