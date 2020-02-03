package com.example.wildfire_fixed_imports.model

import android.content.Context
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.local_store.SharedPreferencesHelper
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.view.login_registration.LoginResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber

/*
*  this class acts as a repository for user related objects
*
* */

class LocalUser(
        var mWebBEUser: WebBEUser? = null,
        var mLocations: MutableList<WebBELocation?> = mutableListOf(),
        var mTheme: Int? = THEME_UNDEFINED,
        var mFirebaseUser: FirebaseUser? = null,
        var mAqiStations: MutableList<AQIStations> = mutableListOf(),
        var mFireLocations: MutableList<DSFires> = mutableListOf(),
        var mLayerVisibility: MutableMap<String, Boolean> = mutableMapOf(
                AQI_BASE_TEXT_LAYER to true,
                AQI_HEATLITE_BASE_LAYER to true,
                AQI_HEATLITE_CLUSTER_LAYER to true,
                AQI_CLUSTERED_COUNT_LAYER to true,
                AQI_NEAREST_NEIGHBOR_LAYER_ID to true
        )


) {
    private val applicationLevelProvider: ApplicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val sharedPreferencesHelper: SharedPreferencesHelper = applicationLevelProvider.sharedPreferencesHelper
    private val userLocationWebBEController = applicationLevelProvider.userLocationWebBEController
    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"
    private val firebaseAuth = applicationLevelProvider.firebaseAuth

    init {
        Timber.i("$TAG \n LocalUser init")
        CoroutineScope(Dispatchers.IO).async {

            if (firebaseAuth.currentUser?.uid != null) {
                val result = CoroutineScope(Dispatchers.IO).async {applicationLevelProvider.userWebBEController.signin()}.await()
                when (result) {
                    is SuccessFailWrapper.Success -> Timber.i("$TAG Login successful for ${applicationLevelProvider.webUser?.email}\n token = ${applicationLevelProvider.webUser?.token}")
                    /*
                    is SuccessFailWrapper.Exception -> Timber.i("$TAG login fail ${result.e}"
                    is SuccessFailWrapper.Fail -> _"$TAG login fail ${result.mes}"
                    is SuccessFailWrapper.Throwable ->
                    */
                    else -> "$TAG login fail $result"
                }
                getLayerVisibilityFromPrefs()
                getUserLocationsInit()
            }


        }
    }

    private suspend fun getUserLocationsInit() {
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
            Timber.d("\n ${it.key} = key \n ${it.value} = value ")
        }
    }
    private fun saveLayerVisibility() {
        mLayerVisibility.forEach{

            sharedPreferencesHelper.saveBoolean(it.key,it.value)
        }
    }

    private fun getSavedTheme() = sharedPreferencesHelper.getInteger(KEY_THEME, THEME_UNDEFINED)

    private fun saveTheme(theme: Int) = sharedPreferencesHelper.saveInteger(KEY_THEME, theme)

    fun saveUser() {


    }


    companion object {
        private var mInstance: LocalUser? = null
        fun getInstance(context: Context): LocalUser {
            if (mInstance == null) mInstance = LocalUser()
            return mInstance as LocalUser
        }
    }
}