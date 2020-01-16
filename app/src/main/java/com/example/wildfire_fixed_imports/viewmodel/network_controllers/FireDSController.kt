package com.example.wildfire_fixed_imports.viewmodel.network_controllers

import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.com.example.wildfire_fixed_imports.RetrofitErrorHandler
import com.example.wildfire_fixed_imports.methodName
import com.example.wildfire_fixed_imports.model.*
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.MapViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean


/*
*
* This class handles CRUD functionality for the fire objects used by the data science backend
*
*   this class also manages making repeated calls to the backend to remain updated on fires
* */


class FireDSController (val mapViewModel: MapViewModel) {


    private val appLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private val retrofitDSService = appLevelProvider.retrofitDSService

    private  var isFiresServiceRunning = AtomicBoolean()

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
            RetrofitErrorHandler(throwable)

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
            RetrofitErrorHandler(throwable)

        }




    @Deprecated("please use new methods in same class")
    suspend fun getFireLocations() {

        //TODO handle errors

        val results = retrofitDSService.getDSFireLocations()
        appLevelProvider.masterController.handleFireData(results)

    }


    @Deprecated("please use new methods in same class")
    suspend fun startFireService(){
        isFiresServiceRunning.set(true)
        var countup = 0
        while(isFiresServiceRunning.get()) {
            var systemmilli = System.currentTimeMillis()

            getFireLocations()
           // delay(300000)
            delay(300000)
            Timber.i("system milli: $systemmilli")
            Timber.i("countup: ${countup++}")

        }

    }

    //Write method to send and receive data from login

    //Write method to send and receive data from register
    @Deprecated("please use new methods in same class")
    suspend fun stopFireService(){
        //Potential issue if job running?
        isFiresServiceRunning.set(false)
    }
}