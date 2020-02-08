package com.example.wildfire_fixed_imports.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.model.LoadingDefinition
import com.example.wildfire_fixed_imports.util.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_entrance.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule


/*
*
* Entrace activity exists as a splash screen that allows us to decouple our initialization code, specifically:
* preference and persistence loading
*  permission checks
*  and begin initial retrieval, processing and caching of remote data (i.e. fire/AQI/feature/location data)_
*
* Activity should remain as minimal as possible, existing solely to prompt the user for permission handling, alert the user
* if they've been logged out unexpectedly, and do similar house keeping and initialization checks
*
* */
class EntranceActivity : AppCompatActivity() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"

    lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)


        motion_layout_entrance.transitionToEnd()
        val intent = Intent(this, MainActivity::class.java)
        textView=findViewById(R.id.entry_tv)
        Timer().schedule(2000){
        initPermissions()
        }
        textView.setOnClickListener {
            redirect()
        }
    }

    fun initPermissions() {
        CoroutineScope(Dispatchers.Main).async {
            withContext(Dispatchers.Default) {  checkInternetPermission() }
            withContext(Dispatchers.Default) { checkFineLocationPermission() }
            withContext(Dispatchers.Default) { checkCoarseLocationPermission()}
            Timber.i("init - initpermissions")
        }

    }


    // redirects to app upon completion
    fun redirect() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("anal sax", "Batman")
        }
        startActivity(intent)
    }

    fun checkInternetPermission() {
        Timber.i("init - check internet")
        // Check if the INTERNET permission has been granted
        if (ContextCompat.checkSelfPermission(applicationLevelProvider.applicationContext, Manifest.permission.INTERNET) ==
            PackageManager.PERMISSION_GRANTED) {
            Timber.i("init - internet already available")
            // Permission is already available, set boolean in ApplicationLevelProvider
            applicationLevelProvider.internetPermission = true
            //pop snackbar to notify of permissions
            textView.showSnackbar("Internet permission: ${applicationLevelProvider.internetPermission} \n " +
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
            textView.showSnackbar(
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
        if (ContextCompat.checkSelfPermission(applicationLevelProvider.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            Timber.i("init -  fine location already granted")
            // Permission is already available, set boolean in ApplicationLevelProvider
            applicationLevelProvider.fineLocationPermission = true
            //pop snackbar to notify of permissions
            textView.showSnackbar("Internet permission: ${applicationLevelProvider.internetPermission} \n " +
                    "Fine Location permission: ${applicationLevelProvider.fineLocationPermission}", Snackbar.LENGTH_SHORT)
            redirect()
        } else {
            // Permission is missing and must be requested.
            requestFineLocationPermission()
        }
    }

    fun checkCoarseLocationPermission() {
        Timber.i("init - check coarse location")
        // Check if the Camera permission has been granted
        if (ContextCompat.checkSelfPermission(applicationLevelProvider.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            Timber.i("init -  coarse location already granted")
            // Permission is already available, set boolean in ApplicationLevelProvider
            applicationLevelProvider.coarseLocationPermission = true
            //pop snackbar to notify of permissions
            textView.showSnackbar("coarse permission: ${applicationLevelProvider.coarseLocationPermission} \n " +
                    "Fine Location permission: ${applicationLevelProvider.coarseLocationPermission}", Snackbar.LENGTH_SHORT)
            redirect()
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
            textView.showSnackbar(
                "GPS location data is needed to provide accurate local results",
                Snackbar.LENGTH_INDEFINITE, "OK"
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_COARSE_LOCATION
                )
            }

        } else {
            textView.showSnackbar("cOARSE Location not available", Snackbar.LENGTH_SHORT)

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
            textView.showSnackbar(
                "GPS location data is needed to provide accurate local results",
                Snackbar.LENGTH_INDEFINITE, "OK"
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
            }

        } else {
            textView.showSnackbar("Fine Location not available", Snackbar.LENGTH_SHORT)

            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_FINE_LOCATION
            )
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Timber.i("on request == before while loop permission: ${permissions.toString()} requestcode: $requestCode grantresults: ${grantResults.toString()} ")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                Timber.i("on request == after while loop fine location")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    applicationLevelProvider.fineLocationPermission = true
                    textView.showSnackbar("Fine Location granted successfully", Snackbar.LENGTH_SHORT)
                    redirect()

                } else {
                    applicationLevelProvider.fineLocationPermission = false
                    textView.showSnackbar("Fine Location not granted", Snackbar.LENGTH_SHORT)
                }
                return
            }
            MY_PERMISSIONS_REQUEST_INTERNET -> {
                Timber.i("on request == after while loop internet")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    applicationLevelProvider.internetPermission = true
                    textView.showSnackbar("Internet granted successfully", Snackbar.LENGTH_SHORT)

                } else {
                    applicationLevelProvider.internetPermission = false
                    textView.showSnackbar("Internet not granted", Snackbar.LENGTH_SHORT)
                    //
                    TODO("CAUSE APPLICATION TO EXIT HERE")
                }
                return
            }

            MY_PERMISSIONS_COARSE_LOCATION -> {
                Timber.i("on request == after while loop internet")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    applicationLevelProvider.coarseLocationPermission = true
                    textView.showSnackbar("Internet granted successfully", Snackbar.LENGTH_SHORT)

                } else {
                    applicationLevelProvider.coarseLocationPermission = false
                    textView.showSnackbar("Internet not granted", Snackbar.LENGTH_SHORT)
                    //
                    // TODO("CAUSE APPLICATION TO EXIT HERE")
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                Timber.i("on request == after while loop else")
                // Ignore all other requests.
            }
        }
        Timber.i("$TAG \n END OF ENTRANCE ACTIVITY, PERMISSIONS EXHAUSTED \n redirecting to mainActivity ")



    }

}
