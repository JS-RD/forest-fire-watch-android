package com.example.wildfire_fixed_imports.model.local_store

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.*
import com.example.wildfire_fixed_imports.util.*
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.properties.Delegates

/*
*  this class acts as a repository for user related objects
*
* */

class LocalUser(
        var mWebBEUser: WebBEUser? = null,
        var mLocations: MutableList<WebBELocation?> = mutableListOf(DEFAULT_WEBBELOCATION),
        var mDefaultRadius: Double = 5.0,
        var mTheme: Int? = THEME_UNDEFINED,

        var mFireLocations: MutableList<DSFires> = mutableListOf(),
        var mLayerVisibility: MutableMap<String, Boolean> = mutableMapOf(
                AQI_BASE_TEXT_LAYER to true,
                AQI_HEATLITE_BASE_LAYER to true,
                AQI_HEATLITE_CLUSTER_LAYER to true,
                AQI_CLUSTERED_COUNT_LAYER to true,
                AQI_NEAREST_NEIGHBOR_LAYER_ID to true
        )


) {
    var observableData: String by Delegates.observable("Initial value") {
        property, oldValue, newValue ->
        println("${property.name}: $oldValue -> $newValue")
    }
    var mAqiStations: MutableList<AQIStations> by Delegates.observable(mutableListOf()){ property, oldValue, newValue ->
       oldValue.addAll(newValue)
       oldValue.toSet()

    }

    var AqiGeoJson:String = ""

    val mFirebaseUser: FirebaseUser? get() =  firebaseAuth.currentUser

    private val applicationLevelProvider: ApplicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val sharedPreferencesHelper: SharedPreferencesHelper = applicationLevelProvider.sharedPreferencesHelper
    private val userLocationWebBEController = applicationLevelProvider.userLocationWebBEController
    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"
    private val firebaseAuth = applicationLevelProvider.firebaseAuth

    init {
        Timber.i("$TAG \n LocalUser init")

        mTheme = getSavedTheme()
        Timber.i("$TAG theme = $mTheme")
        setTheme(mTheme as Int)

        mDefaultRadius = getDefaultRadius()

        CoroutineScope(Dispatchers.IO).launch {

            if (firebaseAuth.currentUser?.uid != null) {
                val result = CoroutineScope(Dispatchers.IO).async {applicationLevelProvider.userWebBEController.signin()}.await()
                when (result) {
                    is SuccessFailWrapper.Success -> {

                    Timber.i("$TAG Login successful for ${applicationLevelProvider.webUser?.email}\n token = ${applicationLevelProvider.webUser?.token}")
                }
                    /*
                    is SuccessFailWrapper.Exception -> Timber.i("$TAG login fail ${result.e}"
                    is SuccessFailWrapper.Fail -> _"$TAG login fail ${result.mes}"
                    is SuccessFailWrapper.Throwable ->
                    */
                    else -> "$TAG login fail $result"
                }
                getLayerVisibilityFromPrefs()
                getUserLocationsInit()
                Timber.i("test $mLocations")
                applicationLevelProvider.dataRepository.initData()
            }


        }


    }

    private suspend fun getUserLocationsInit() {
        mLocations= mutableListOf()
        mLocations.add(applicationLevelProvider.latestLocation?.toWebBELocation()
                ?: DEFAULT_WEBBELOCATION)
        val result = userLocationWebBEController.getWebBELocations()
        when (result) {
            is SuccessFailWrapper.Success -> {
                Timber.i("$TAG get user Locations Successful \n message = ${result.message}")
                result.value?.forEach {
                    mLocations.add(it)
                }

            }
            else -> Timber.e("$TAG \n ERROR IN GET USER LOCATIONS INIT \n $result")
        }
        Timber.e("$TAG Final locations for user")
        var count =0
        mLocations.forEach {
            Timber.e("\n${count++} $it")
        }
    }



    private fun getLayerVisibilityFromPrefs() {
        mLayerVisibility.forEach{
            mLayerVisibility[it.key] =sharedPreferencesHelper.getBoolean(it.key,true)
            //Timber.d("\n ${it.key} = key \n ${it.value} = value ")
        }
    }
    private fun saveLayerVisibility() {
        mLayerVisibility.forEach{

            sharedPreferencesHelper.saveBoolean(it.key,it.value)
        }
    }
    private fun getDefaultRadius():Double =  sharedPreferencesHelper.getDouble(KEY_DEFAULT_RADIUS, 5.0)

    fun saveDefaultRadius(defaultRadius: Double) = sharedPreferencesHelper.saveDouble(KEY_DEFAULT_RADIUS, defaultRadius).also { mDefaultRadius=defaultRadius }

    private fun getSavedTheme():Int = sharedPreferencesHelper.getInteger(KEY_THEME, THEME_UNDEFINED)

    private fun setTheme(prefsMode: Int) {
        Timber.i("theme prefsmode= $prefsMode")
        CoroutineScope(Dispatchers.Main).launch {
            when (prefsMode) {
                THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM).also { Timber.i("theme follow sys") }
                THEME_BATTERY -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY).also { Timber.i("theme auth batt ") }
                THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES).also { Timber.i("theme night yes ") }
                THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO).also { Timber.i("theme night no ") }
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM).also { Timber.i("theme else af  ") }
            }
        }

    }

    fun saveTheme(theme: Int) = sharedPreferencesHelper.saveInteger(KEY_THEME, theme)

    fun saveUser() {


    }


    companion object {
        private var mInstance: LocalUser? = null
        fun getInstance(): LocalUser {
            if (mInstance == null) mInstance = LocalUser()
            return mInstance as LocalUser
        }
    }
}