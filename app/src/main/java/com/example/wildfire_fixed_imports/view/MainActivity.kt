package com.example.wildfire_fixed_imports.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.Layout
import android.view.Menu
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.view.bottom_sheet.BottomSheetLayout
import com.example.wildfire_fixed_imports.viewmodel.map_controllers.z_delete_discarded.initHeatmapColors
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private lateinit var mapViewModel: MapViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var layout: View
    private val TAG:String
        get() = "$javaClass $methodName"
    private lateinit var  arrow: ImageView
    private lateinit var legendText: TextView
    private lateinit var cloudImageView: ImageView
    private lateinit var fireImageView: ImageView
    private lateinit var  switchAqiCloudBSIcon: SwitchCompat
    private lateinit var  switchFireBSIcon: SwitchCompat
    private lateinit var bottomSheet: BottomSheetLayout
    private lateinit var aqiGaugeExpanded: ViewGroup
    private lateinit var aqiGaugeMinimized: ImageView


    private lateinit var btmsheetToggleIndex: SwitchCompat
    private lateinit var btmSheetToggleRadius: SwitchCompat

    private lateinit var btmSheetTvIndex: TextView
    private lateinit var btmSheetTvRadius: TextView
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var appBarLayout: AppBarLayout
    lateinit var layoutRegistrationFragment: Layout

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
        applicationLevelProvider.bottomSheet?.visibility = VISIBLE








        fusedLocationClient=LocationServices.getFusedLocationProviderClient(this)
        //set up toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

         drawerToggle = ActionBarDrawerToggle(this,
            drawer_layout,
            toolbar,
                 R.string.navigation_drawer_open,
                 R.string.navigation_drawer_close)



        // drawer toggle aka burger menu changes
        drawerToggle.isDrawerIndicatorEnabled = true
        applicationLevelProvider.drawerToggle = drawerToggle

        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        actionBar!!.setDisplayShowTitleEnabled(false)



        appBarLayout = findViewById(R.id.app_bar_layout)
        applicationLevelProvider.appBarLayout = appBarLayout




        //find by ids and APLs

        arrow=findViewById(R.id.bottomsheert_img_arrow)
        legendText=findViewById(R.id.bottomsheert_tv_status)
        fireImageView = findViewById(R.id.bottomsheert_img_fire)
        cloudImageView = findViewById(R.id.bottomsheert_img_cloud)
        switchAqiCloudBSIcon = findViewById(R.id.bottomsheet_switch_cloud)
        switchFireBSIcon=findViewById(R.id.bottomsheert_switch_fire)
        bottomSheet =findViewById(R.id.bottomSheetLayout)
        aqiGaugeExpanded = findViewById(R.id.aqi_bar_include)
        aqiGaugeMinimized = findViewById(R.id.img_appbar_aqi_gauge)

        //bottom sheet toggles and textviews
        btmsheetToggleIndex = findViewById(R.id.bottomsheet_switch_index)
        btmSheetToggleRadius = findViewById(R.id.bottomsheet_switch_radius)
        btmSheetTvIndex = findViewById(R.id.bottomsheet_tv_index)
        btmSheetTvRadius = findViewById(R.id.bottomsheet_tv_radius)
        applicationLevelProvider.btmsheetToggleIndex=btmsheetToggleIndex
        applicationLevelProvider.btmSheetToggleRadius=btmSheetToggleRadius
        applicationLevelProvider.btmSheetTvIndex=btmSheetTvIndex
        applicationLevelProvider.btmSheetTvRadius=btmSheetTvRadius


        /*  aqiGaugeExpanded.setAlpha(0.5f)*/
        aqiGaugeMinimized.setAlpha(0.5f)
        aqiGaugeExpanded.setAlpha(0.7f)


        applicationLevelProvider.arrow=arrow
        applicationLevelProvider.fireImageView=fireImageView
        applicationLevelProvider.cloudImageView=cloudImageView
        applicationLevelProvider.switchAqiCloudBSIcon=switchAqiCloudBSIcon
        applicationLevelProvider.switchFireBSIcon=switchFireBSIcon
        applicationLevelProvider.bottomSheet=bottomSheet
        applicationLevelProvider.aqiGaugeExpanded=aqiGaugeExpanded
        applicationLevelProvider.aqiGaugeMinimized=aqiGaugeMinimized
        applicationLevelProvider.legendText=legendText


        setUpOnClicks()
        setUpNav()

            Timber.e("web be user"+applicationLevelProvider.localUser?.mWebBEUser)

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
             //  switchFireBSIcon.visibility = View.INVISIBLE
             // switchAqiCloudBSIcon.visibility = View.INVISIBLE
             arrow.setImageResource(R.drawable.ic_arrow_drop_down)
             legendText.visibility = INVISIBLE



         }
         else {
             //switchFireBSIcon.visibility = View.VISIBLE
             // switchAqiCloudBSIcon.visibility =View.VISIBLE

             arrow.setImageResource(R.drawable.ic_arrow_drop_up)
             legendText.setText("FILTERS")
             legendText.visibility = VISIBLE
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
            } catch (ex: SecurityException) {
                Timber.i("Security Exception, no location available")
            }
        } else {
            if (checkSelfPermissionCompat(Manifest.permission.INTERNET) ==
                    PackageManager.PERMISSION_GRANTED) {
                try {
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                } catch (ex: SecurityException) {
                    Timber.i("Security Exception, no location available")
                }
            }
        }
    }





    private fun setUpNav() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                    R.id.nav_home, R.id.nav_login, R.id.nav_reg, R.id.nav_settings,
                    R.id.nav_debug//, R.id.nav_share, R.id.nav_send
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

                    initHeatmapColors()
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
