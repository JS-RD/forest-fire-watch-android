package com.example.wildfire_fixed_imports.viewmodel.view_controllers

import android.graphics.BitmapFactory
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.com.example.wildfire_fixed_imports.LatLng
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions


class SymbolController () {

    val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    val mapView = applicationLevelProvider.mapboxView
    val mapboxMap = applicationLevelProvider.mapboxMap
    val mapStyle = applicationLevelProvider.mapboxStyle

    private val FIRE_ICON = "FIRE-15"

    init {


        // Set up a SymbolManager instance
        val symbolManager =  SymbolManager(mapView, mapboxMap, mapStyle)

        symbolManager.iconAllowOverlap = true
        symbolManager.textAllowOverlap = true

// Add symbol at specified lat/lon
      //  mapStyle.addImage(FIRE_ICON, applicationLevelProvider.fireIconAlt)
        val symbol = symbolManager.create(SymbolOptions()
                .withLatLng(applicationLevelProvider.userLocation.LatLng())
                .withIconImage(FIRE_ICON)
                .withIconSize(2.0f)
                .withDraggable(true))

/*// Add click listener and change the symbol to a cafe icon on click
            symbolManager.addClickListener(new OnSymbolClickListener() {
                @Override
                public void onAnnotationClick(Symbol symbol) {
                    Toast.makeText(SymbolListenerActivity.this,
                            getString(R.string.clicked_symbol_toast), Toast.LENGTH_SHORT).show();
                    symbol.setIconImage(MAKI_ICON_CAFE);
                    symbolManager.update(symbol);
                }
            });*/

/*// Add long click listener and change the symbol to an airport icon on long click
            symbolManager.addLongClickListener((new OnSymbolLongClickListener() {
                @Override
                public void onAnnotationLongClick(Symbol symbol) {
                    Toast.makeText(SymbolListenerActivity.this,
                            getString(R.string.long_clicked_symbol_toast), Toast.LENGTH_SHORT).show();
                    symbol.setIconImage(MAKI_ICON_AIRPORT);
                    symbolManager.update(symbol);
                }
            }));*/
    }


}