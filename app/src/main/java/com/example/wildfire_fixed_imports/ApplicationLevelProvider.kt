package com.example.wildfire_fixed_imports

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.MapViewModelFactory


class ApplicationLevelProvider : Application() {

    /*
    *
    *  ApplicationLevelProvider will allow us to access singleton services and shared data between otherwise disjunct classes,
    *
    * anywhere you can get a hold of the application class, i.e. within acitivties, fragments or anywhere you have context,
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
    val mapViewModelFactory = MapViewModelFactory()
    lateinit var currentActivity: Activity
    lateinit var mapFragment: Fragment



    companion object {
        private lateinit var instance: ApplicationLevelProvider
        /*        lateinit var viewModelFactory:HomeViewModelFactory
                    private set*/
        fun getApplicaationLevelProviderInstance(): ApplicationLevelProvider {
            return instance
        }

    }




    override fun onCreate() {
        super.onCreate()
        instance = this
        //viewModelFactory = HomeViewModelFactory()
    }



}

