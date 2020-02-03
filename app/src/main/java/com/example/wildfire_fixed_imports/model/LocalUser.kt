package com.example.wildfire_fixed_imports.model

import android.content.Context
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.local_store.SharedPreferencesHelper
import com.example.wildfire_fixed_imports.util.Coroutines
import com.example.wildfire_fixed_imports.util.StackTraceInfo
import com.example.wildfire_fixed_imports.util.className
import com.example.wildfire_fixed_imports.util.fileName
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber

/*
*  this class acts as a repository for user related objects
*
* */

class LocalUser(
        var mWebBEUser: WebBEUser? = null,
        var mLocations: MutableList<WebBELocation>  = mutableListOf(),
        var theme: String? = null,
        var mFirebaseUser:FirebaseUser? = null,
        var mAqiStations: MutableList<AQIStations> = mutableListOf(),
        var mFireLocations: MutableList<DSFires> = mutableListOf(),
        var mLayerVisibility: MutableMap<String,Boolean> = mutableMapOf()


)


{
    private val applicationLevelProvider: ApplicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"
    private val firebaseAuth = applicationLevelProvider.firebaseAuth

    init {
        Timber.i ("$TAG \n LocalUser init")
        Coroutines.io {
            if (firebaseAuth.currentUser?.uid != null) {
                applicationLevelProvider.userWebBEController.signin()
                Timber.i ("$TAG \n web user = \n ${applicationLevelProvider.webUser} ")
            }
        }
    }
    companion object {
        private var mInstance: LocalUser? = null
        fun getInstance(context: Context): LocalUser {
            if (mInstance == null) mInstance = LocalUser()
            return mInstance as LocalUser
        }
    }
}