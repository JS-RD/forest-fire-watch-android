package com.example.wildfire_fixed_imports.util

import android.content.Context
import android.location.Location
import android.location.LocationManager

class LocationFinder(context: Context) {
    private var lm = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    private var networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    private var netLoc: Location? = null


    private  var gpsLoc: Location? = null
    private  var finalLoc: Location? = null

    fun check(): Location? {//smaller the number more accurate result will
        try {
            if (gpsEnabled) {
                gpsLoc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        } catch (e: SecurityException) {

        }
        try {
            if (networkEnabled) {
                netLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        } catch (e: SecurityException) {

        }
        if (gpsLoc != null && netLoc != null) {
            finalLoc = if (gpsLoc!!.accuracy > netLoc!!.accuracy) netLoc else gpsLoc
            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)
        } else {
            if (gpsLoc != null) {
                finalLoc = gpsLoc
            } else if (netLoc != null) {
                finalLoc = netLoc
            }
        }

        return finalLoc
    }
}
