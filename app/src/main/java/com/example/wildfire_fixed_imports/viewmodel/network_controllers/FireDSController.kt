package com.example.wildfire_fixed_imports.viewmodel.network_controllers

import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.util.RetrofitErrorHandler
import com.example.wildfire_fixed_imports.util.methodName
import com.example.wildfire_fixed_imports.model.*
import timber.log.Timber


/*
*
* This class handles CRUD functionality for the fire objects used by the data science backend
*
*   this class also manages making repeated calls to the backend to remain updated on fires
* */


class FireDSController () {


    private val appLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private val retrofitDSService = appLevelProvider.retrofitDSService



    private val TAG = "FireDSController"



    suspend fun getDSFireLocations(): SuccessFailWrapper<List<DSFires>>
            =
            try {
            Timber.i("$TAG try $methodName triggered")
            val result =retrofitDSService.getDSFireLocations()

            Timber.i("$TAG success\n list of fires = \n$result ")
            SuccessFailWrapper.Success("Success", result)

        } catch (throwable: Throwable) {
            Timber.i("$TAG catch triggered in getDSFireLocations")
                RetrofitErrorHandler(
                    throwable
                )

        }


    suspend fun getDSRSSFireLocations(dsfire: DSRSSFireSubmit): SuccessFailWrapper<List<DSRRSSFireContainer>>
            =
            try {
            Timber.i("$TAG try getDSRSSFireLocations triggered")
            val result =retrofitDSService.getDSRSSFireLocations(dsfire)

            Timber.i("$TAG success\n list of RSS fires = \n$result ")
            SuccessFailWrapper.Success("Success", result)

        } catch (throwable: Throwable) {
            Timber.i("$TAG catch triggered in getDSRSSFireLocations")
                RetrofitErrorHandler(
                    throwable
                )

        }




    @Deprecated("please use new methods in same class")
    suspend fun getFireLocations() {

        //TODO handle errors

        val results = retrofitDSService.getDSFireLocations()
    /*    (appLevelProvider.masterCoordinator)?.handleFireData(results)*/

    }



}