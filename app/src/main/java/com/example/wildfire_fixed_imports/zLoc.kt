package com.example.wildfire_fixed_imports

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.example.wildfire_fixed_imports.util.MY_PERMISSIONS_COARSE_LOCATION
import com.example.wildfire_fixed_imports.util.MY_PERMISSIONS_REQUEST_FINE_LOCATION
import com.example.wildfire_fixed_imports.util.MY_PERMISSIONS_REQUEST_INTERNET
import com.example.wildfire_fixed_imports.util.showSnackbar
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

open class zLoc() : Fragment() {


    fun initPermissions() {

        checkFineLocationPermission()
        checkInternetPermission()
        checkCoarseLocationPermission()
        Timber.i("init - initpermissions")

    }

    val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    fun checkInternetPermission() {
        Timber.i("init - check internet")
        // Check if the INTERNET permission has been granted
        if (checkSelfPermission(applicationLevelProvider.applicationContext, Manifest.permission.INTERNET) ==
                PackageManager.PERMISSION_GRANTED) {
            Timber.i("init - internet already available")
            // Permission is already available, set boolean in ApplicationLevelProvider
            applicationLevelProvider.internetPermission = true
            //pop snackbar to notify of permissions
            applicationLevelProvider.showSnackbar("Internet permission: ${applicationLevelProvider.internetPermission} \n " +
                    "Fine Location permission: ${applicationLevelProvider.fineLocationPermission}", Snackbar.LENGTH_SHORT)


        } else {
            // Permission is missing and must be requested.
            requestInternetPermission()
        }
    }

    fun requestInternetPermission() {
        Timber.i("init - request internet")
        // Permission has not been granted and must be requested.
        if (shouldShowRequestPermissionRationale(Manifest.permission.INTERNET)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            applicationLevelProvider.showSnackbar(
                    "INTERNET acess is required for this app to function at all.",
                    Snackbar.LENGTH_INDEFINITE, "OK"
            ) {
                requestPermissions(
                        arrayOf(Manifest.permission.INTERNET),
                        MY_PERMISSIONS_REQUEST_INTERNET
                )
            }

        } else {
            applicationLevelProvider.showSnackbar("INTERNET not available", Snackbar.LENGTH_SHORT)

            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissions(arrayOf(Manifest.permission.INTERNET),
                    MY_PERMISSIONS_REQUEST_INTERNET
            )
        }
    }

    fun checkFineLocationPermission() {
        Timber.i("init - check fine location")
        // Check if the Camera permission has been granted
        if (checkSelfPermission(applicationLevelProvider.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Timber.i("init -  fine location already granted")
            // Permission is already available, set boolean in ApplicationLevelProvider
            applicationLevelProvider.fineLocationPermission = true
            //pop snackbar to notify of permissions
            applicationLevelProvider.showSnackbar("Internet permission: ${applicationLevelProvider.internetPermission} \n " +
                    "Fine Location permission: ${applicationLevelProvider.fineLocationPermission}", Snackbar.LENGTH_SHORT)

        } else {
            // Permission is missing and must be requested.
            requestFineLocationPermission()
        }
    }

    fun checkCoarseLocationPermission() {
        Timber.i("init - check coarse location")
        // Check if the Camera permission has been granted
        if (checkSelfPermission(applicationLevelProvider.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Timber.i("init -  coarse location already granted")
            // Permission is already available, set boolean in ApplicationLevelProvider
            applicationLevelProvider.coarseLocationPermission = true
            //pop snackbar to notify of permissions
            applicationLevelProvider.showSnackbar("coarse permission: ${applicationLevelProvider.coarseLocationPermission} \n " +
                    "Fine Location permission: ${applicationLevelProvider.coarseLocationPermission}", Snackbar.LENGTH_SHORT)

        } else {
            // Permission is missing and must be requested.
            requestCoarseLocationPermission()
        }
    }

    fun requestCoarseLocationPermission() {
        Timber.i("init - request coarse location")
        // Permission has not been granted and must be requested.
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            applicationLevelProvider.showSnackbar(
                    "GPS location data is needed to provide accurate local results",
                    Snackbar.LENGTH_INDEFINITE, "OK"
            ) {
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        MY_PERMISSIONS_COARSE_LOCATION
                )
            }

        } else {
            applicationLevelProvider.showSnackbar("cOARSE Location not available", Snackbar.LENGTH_SHORT)

            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_COARSE_LOCATION
            )
        }
    }

    fun requestFineLocationPermission() {
        Timber.i("init - request fine location")
        // Permission has not been granted and must be requested.
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            applicationLevelProvider.showSnackbar(
                    "GPS location data is needed to provide accurate local results",
                    Snackbar.LENGTH_INDEFINITE, "OK"
            ) {
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
            }

        } else {
            applicationLevelProvider.showSnackbar("Fine Location not available", Snackbar.LENGTH_SHORT)

            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }
}