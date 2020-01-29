package com.example.wildfire_fixed_imports

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.view.bottom_sheet.BottomSheetLayout
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var fab: FloatingActionButton
    private lateinit var layout: View
    private val TAG:String
        get() = "$javaClass $methodName"
    private lateinit var  arrow: ImageView
    private lateinit var  aqiCloudBSIcon: SwitchCompat
    private lateinit var  fireBSIcon: SwitchCompat
    private lateinit var bottomSheet: BottomSheetLayout
    private lateinit var aqiGaugeExpanded: ViewGroup
    private lateinit var aqiGaugeMinimized: ImageView
    private lateinit var togle:SwitchCompat



    private var locationManager: LocationManager? = null
    private lateinit var fusedLocationClient:FusedLocationProviderClient
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applicationLevelProvider.currentActivity = this
        mapViewModel =
                ViewModelProviders.of(this, applicationLevelProvider.mapViewModelFactory).get(
                        MapViewModel::class.java
                )

        applicationLevelProvider.appMapViewModel = mapViewModel




        applicationLevelProvider.nav_view = findViewById(R.id.nav_view)


        fusedLocationClient=LocationServices.getFusedLocationProviderClient(this)
        //set up toolbar

        //find by ids and APLs
        arrow=findViewById(R.id.imageViewArrow)
        aqiCloudBSIcon = findViewById(R.id.switchImageViewCloud)
        fireBSIcon=findViewById(R.id.switchImageViewFire)
        bottomSheet =findViewById(R.id.bottomSheetLayout)
        aqiGaugeExpanded = findViewById(R.id.aqi_bar_include)
        aqiGaugeMinimized = findViewById(R.id.img_appbar_aqi_gauge)

        aqiGaugeMinimized.setAlpha(0.5f)
        applicationLevelProvider.arrow=arrow
        applicationLevelProvider.aqiCloudBSIcon=aqiCloudBSIcon
        applicationLevelProvider.fireBSIcon=fireBSIcon
        applicationLevelProvider.bottomSheet=bottomSheet
        applicationLevelProvider.aqiGaugeExpanded=aqiGaugeExpanded
        applicationLevelProvider.aqiGaugeMinimized=aqiGaugeMinimized



setUpOnClicks()
        setUpNav()




        //check permissions


    }

 fun setUpOnClicks() {
     aqiGaugeExpanded.setOnClickListener {
         aqiGaugeExpanded.visibility = INVISIBLE
         aqiGaugeMinimized.visibility = VISIBLE
        // aqiGaugeMinimized.setAlpha(0.3f)
     }
     aqiGaugeMinimized.setOnClickListener {
         aqiGaugeExpanded.visibility = VISIBLE
         aqiGaugeMinimized.visibility = INVISIBLE
         //aqiGaugeExpanded.setAlpha(0.3f)
     }

     val bottomSheetObserver = Observer<Float> {
         if (it ==1f){
             //  fireBSIcon.visibility = View.INVISIBLE
             // aqiCloudBSIcon.visibility = View.INVISIBLE
             arrow.setImageResource(R.drawable.ic_arrow_drop_down)

         }
         else {
             //fireBSIcon.visibility = View.VISIBLE
             // aqiCloudBSIcon.visibility =View.VISIBLE

             arrow.setImageResource(R.drawable.ic_arrow_drop_up)
         }

     }
     bottomSheet.progress.observe(this, bottomSheetObserver)



     arrow.setOnClickListener{ bottomSheet.toggle()
         Timber.i("arrow click")}


 }

    //navigation and interface methods

    fun locationInit() {
        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            try {
                // Request location updates

                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
                Timber.i(" $TAG requesting location")
                val sauce = locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
                CoroutineScope(Dispatchers.IO).launch {
                    getLatestLocation()

                }
            } catch (ex: SecurityException) {
                Timber.i("Security Exception, no location available")
            }
        } else {
            if (checkSelfPermissionCompat(Manifest.permission.INTERNET) ==
                    PackageManager.PERMISSION_GRANTED) {
                try {
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                    CoroutineScope(Dispatchers.IO).launch {
                        getLatestLocation()

                    }
                } catch (ex: SecurityException) {
                    Timber.i("Security Exception, no location available")
                }
            }
        }
    }




    //navigation and interface methods

   /* fun setFabOnclick(lambda: () -> Unit) {
        fab.setOnClickListener { lambda.invoke() }
    }*/

    private fun setUpNav() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_login_register, R.id.nav_settings,
                R.id.nav_debug, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )

        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    //for grabing location
    fun getLatestLocation(): Location? {

        val localationFinder = LocationFinder(this)
        val result = localationFinder.check()
        if (result != null) {
            return result
        }
        /* else {
             if (applicationLevelProvider.fineLocationPermission) {
                 val locale = fusedLocationClient.lastLocation
                 return locale
             }
         }*/
        return null
    }




    //for location component
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Timber.i("location log" + location.longitude + ":" + location.latitude)

        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }


    fun enableLocationComponent(loadedMapStyle: Style) {
// Check if permissions are enabled and if not let user known
        if (applicationLevelProvider.fineLocationPermission) {

// Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .build()

            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(this, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .build()

// Get an instance of the LocationComponent and then adjust its settings
            applicationLevelProvider.mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

// Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

// Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

// Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            Toast.makeText(this, "Fine Location not enabled", Toast.LENGTH_SHORT).show()
        }
    }



    private fun rotateArrow(progress: Float) {
        arrow.rotation = 180 * progress
        bottomSheet.toggle()
        Timber.i("arrow click")
    }


/*
    fun tempFrag() {
        var id =findViewById<FrameLayout>(R.id.fragment_container)
        if (id != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            */
/*     if (savedInstanceState != null) {
                     return;
                 }
     *//*
            // Create a new Fragment to be placed in the activity layout
            val firstFragment = GetInfoFragment()
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            // firstFragment.arguments = intent.extras
            // Add the fragment to the 'fragment_container' FrameLayout
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit()
        }
    }
*/



    //permissions methods


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        Timber.i("on request == before while loop permission: ${permissions.toString()} requestcode: $requestCode grantresults: ${grantResults.toString()} ")
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                Timber.i("on request == after while loop fine location")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    applicationLevelProvider.fineLocationPermission = true
                    applicationLevelProvider.showSnackbar("Fine Location granted successfully", Snackbar.LENGTH_SHORT)
                } else {
                    applicationLevelProvider.fineLocationPermission = false
                    applicationLevelProvider.showSnackbar("Fine Location not granted", Snackbar.LENGTH_SHORT)
                }
                return
            }
            MY_PERMISSIONS_REQUEST_INTERNET -> {
                Timber.i("on request == after while loop internet")
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    applicationLevelProvider.internetPermission = true
                    applicationLevelProvider.showSnackbar("Internet granted successfully", Snackbar.LENGTH_SHORT)
                } else {
                    applicationLevelProvider.internetPermission = false
                    applicationLevelProvider.showSnackbar("Internet not granted", Snackbar.LENGTH_SHORT)
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
                    applicationLevelProvider.showSnackbar("Internet granted successfully", Snackbar.LENGTH_SHORT)
                } else {
                    applicationLevelProvider.coarseLocationPermission = false
                    applicationLevelProvider.showSnackbar("Internet not granted", Snackbar.LENGTH_SHORT)
                    //
                    TODO("CAUSE APPLICATION TO EXIT HERE")
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
    }


}
/*
*
*
*
*
    var permissionsListener: PermissionsListener = object : PermissionsListener {
        override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        }
        override fun onPermissionResult(granted: Boolean) {
            if (granted) {
                // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
            } else {
                // User denied the permission
            }
        }
    }
*
*
*   fun checkPermissions() {
        // Here, this is the current activity
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION
                )
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }
    }
*
*     var permissionsManager = PermissionsManager()
    if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
    } else {
        permissionsManager = PermissionsManager(this)
        permissionsManager.requestLocationPermissions(this)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
* */


/*
*
*         /* val fm = supportFragmentManager
             val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
             val fragment2= navHostFragment!!.childFragmentManager.fragments[0]
              println(fragment2.toString())
                      (fragment2 as HomeFragment).mapIT()*/
           // val homeid = findNavController(R.id.nav_host_fragment).currentDestination!!.id
         /*   will give you your current Fragment's id where
*/
          //  private fun navController() = Navigation.findNavController(this, R.id.navHostFragment)
/*            this id is the id which you have given in your Navigation Graph XML file under fragment tag.
            You could also compare currentDestination.label if you wan*/

       /*   *//*  val fragment: HomeFragment =
                fm.findFragmentById(R.id.home) as HomeFragment*//*
         //   NewsItemFragment tag = ( NewsItemFragment)getFragmentManager().findFragmentByTag(MainActivity.NEWSITEM_FRAGMENT)
            val fragment2 = fm.findFragmentById(R.id.nav_host_fragment)
                val fraglist  = fm.fragments
            print(fraglist.toString())
            println("huh")
            println(fragment2)
       *//*  //   println(fragment2!!.tag)
            println(fragment2.id)
            *//*            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/