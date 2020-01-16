package com.example.wildfire_fixed_imports

import android.app.Activity
import android.app.Application
import android.location.Location
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.core.graphics.drawable.toBitmap
import com.crashlytics.android.Crashlytics
import com.example.wildfire_fixed_imports.model.WebBEUser
import com.example.wildfire_fixed_imports.networking.FirebaseAuthImpl
import com.example.wildfire_fixed_imports.networking.RetroImplForDataScienceBackEnd
import com.example.wildfire_fixed_imports.networking.RetrofitImplementationForWebBackend
import com.example.wildfire_fixed_imports.view.MapDisplay.WildFireMapFragment
import com.example.wildfire_fixed_imports.view.tools.DebugFragment
import com.example.wildfire_fixed_imports.viewmodel.network_controllers.UserLocationWebBEController
import com.example.wildfire_fixed_imports.viewmodel.network_controllers.UserWebBEController
import com.example.wildfire_fixed_imports.viewmodel.view_controllers.HeatMapController
import com.example.wildfire_fixed_imports.viewmodel.view_controllers.MapController
import com.example.wildfire_fixed_imports.viewmodel.view_controllers.MarkerController
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.MapViewModel
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.MapViewModelFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.maps.MapboxMap
import timber.log.Timber
import timber.log.Timber.DebugTree


class ApplicationLevelProvider : Application() {

    /*
    *
    *  ApplicationLevelProvider will allow us to access singleton services and shared data between otherwise disjunct classes,
    *
    *
    *  This is a service locator more than dependency injection, I think, to be honest the distinction is somewhat confusing to me but the
    * bottom line is: using ApplicationLevelProvider, we can minimize objects being needlessly replicated and can further allow classes to painlessly
    * find and communicate with each other.
    *
    * Instuctions for usee:
    * anywhere you can get a hold of the application class, i.e. within acitivties, fragments or anywhere else within the application,
    * you simply need to:
    *
    * #1: get a hold of the proper instance of ApplicationLevelProvider via something like:
    * val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    *
    * #2: Define a property, service or other object you would like to share within the properties of ApplicationLevelProvider.
    * e.g. since we need to create a MapViewModelFactory() that can be used by both the MapFragment and the MainAcitivty,
    * we define mapViewModelFactory as MapViewmodelFactory() as a public property of ApplicationLEvelProvider
    *
    * #3: then all we need to do is call the the instance we've found of ApplicationLevelProvider and retrieve the property we need,
    * e.g. to get the view model factory we would do something like:
    * mapViewModel = ViewModelProviders.of(this, applicationLevelProvider.mapViewModelFactory).get(
    *        MapViewModel::class.java)
    *
    * This can be done to share retrofit instances, shared prefs, classes to house strings or other primitive data, etc etc.
    * As of now I'm not seeing why we need koin or dagger with a system like this in place and I don't see where we lose any functionality or
    * speed -- lets see if we can avoid the additional dependency but if issues come up, we'll switch back to another dependency provider library.
    *
    *
    * */


    // Initialize Firebase analytics, Auth

    val mFirebaseAnalytics by lazy {
        FirebaseAnalytics.getInstance(this)
    }
    val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    var firebaseUser: FirebaseUser? = null

    var webUser:WebBEUser? = null


/*    val authenticationState by lazy {
        AuthenticationState()
    }*/





    val retrofitWebService by lazy {
        RetrofitImplementationForWebBackend.createWEB()
    }
    val retrofitDSService by lazy {
        RetroImplForDataScienceBackEnd.createDS()
    }
    val firebaseAuthImpl by lazy {
        FirebaseAuthImpl()
    }

    val userWebBEController by lazy{
        UserWebBEController()
    }

    val userLocationWebBEController by lazy {
        UserLocationWebBEController()
    }

// ...


val mapViewModelFactory by lazy {
    MapViewModelFactory()
}
val markerController by lazy {
    MarkerController()
}
val heatMapController by lazy {
    HeatMapController()
}

    lateinit var currentActivity: Activity
    lateinit var mapFragment: WildFireMapFragment
    lateinit var debugFragment: DebugFragment
    lateinit var mapController: MapController

    lateinit var mapboxMap: MapboxMap
    lateinit var mapboxView: View

    var fineLocationPermission: Boolean = false
    var internetPermission: Boolean = false

    lateinit var appMapViewModel: MapViewModel

    lateinit var fireIcon: Icon

    lateinit var userLocation: Location


    companion object {
        private lateinit var instance: ApplicationLevelProvider
        fun getApplicaationLevelProviderInstance(): ApplicationLevelProvider {
            return instance
        }

    }


    override fun onCreate() {
        super.onCreate()

        instance = this
        //viewModelFactory = HomeViewModelFactory()

        //hash tag team smoke trees
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }


        val iconFactory by lazy { IconFactory.getInstance(this) }
        val fireBitmap = getDrawable(R.drawable.ic_fireicon)!!.toBitmap(50, 50)
        fireIcon =
                iconFactory.fromBitmap(fireBitmap)


    }

    /** A tree which logs important information for crash reporting.
     * as per king Jake W's timber example app.
     * addtional testing of this needed, but should be functional... probably*/
    private class CrashReportingTree : Timber.Tree() {
        override fun log(
                priority: Int,
                tag: String?, @NonNull message: String,
                t: Throwable?
        ) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
            Crashlytics.setUserIdentifier(getApplicaationLevelProviderInstance().firebaseUser.toString())
            Crashlytics.log(priority,tag,message)
            if (t != null) {
                if (priority == Log.ERROR) {
                    Crashlytics.logException(t)
                } else if (priority == Log.WARN) {
                    Crashlytics.log("warning: ${t.toString()}")
                }

            }

        }
    }
}

