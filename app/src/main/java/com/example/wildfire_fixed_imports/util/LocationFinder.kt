package com.example.wildfire_fixed_imports.util

import android.content.Context
import android.location.Location
import android.location.LocationManager

class LocationFinder(context: Context) {
    var lm = context
            .getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    var network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    var net_loc: Location? = null


    var gps_loc: Location? = null
    var finalLoc: Location? = null

    fun check(): Location? {//smaller the number more accurate result will
        try {
            if (gps_enabled) {
                gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        } catch (e: SecurityException) {

        }
        try {
            if (network_enabled) {
                net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        } catch (e: SecurityException) {

        }
        if (gps_loc != null && net_loc != null) {
            finalLoc = if (gps_loc!!.accuracy > net_loc!!.accuracy) net_loc else gps_loc
            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)
        } else {
            if (gps_loc != null) {
                finalLoc = gps_loc
            } else if (net_loc != null) {
                finalLoc = net_loc
            }
        }

        return finalLoc
    }
}
