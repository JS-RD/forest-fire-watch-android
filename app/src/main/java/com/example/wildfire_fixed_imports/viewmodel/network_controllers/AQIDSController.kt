package com.example.wildfire_fixed_imports.viewmodel.network_controllers

import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.com.example.wildfire_fixed_imports.RetrofitErrorHandler
import com.example.wildfire_fixed_imports.model.AQIStations
import com.example.wildfire_fixed_imports.model.AQIdata
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

/*
*
* This class handles CRUD functionality for the fire objects used by the data science backend
*
*   this class also manages making repeated calls to the backend to remain updated on fires
* */



class AQIDSController() {
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private val retrofitDSService = applicationLevelProvider.retrofitDSService

    private val TAG = "LocationWebBEController"


    suspend fun getAQIStations (lat: Double,lng: Double,distance: Double):SuccessFailWrapper<List<AQIStations>>{

        return try {
            Timber.i("$TAG try postWebBELocation triggered")
            val result =retrofitDSService.getAQIStations(lat,lng,distance)

            Timber.i("$TAG success\n list of aqi stations for lat:$lat lng$lng distance:$distance  = \n$result ")
            SuccessFailWrapper.Success("Success", result)

        } catch (throwable: Throwable) {
            Timber.i("$TAG catch triggered in postWebBELocation")
            RetrofitErrorHandler(throwable)

        }

    }

    suspend fun getAQIData(lat: Double,lng:Double): SuccessFailWrapper<List<AQIdata>> {

        return try {
            Timber.i("$TAG try postWebBELocation triggered")
            val result =retrofitDSService.getAQIData(lat,lng)

            Timber.i("$TAG success\n list of aqi for lat:$lat and lng$lng = \n$result ")
            SuccessFailWrapper.Success("Success", result)

        } catch (throwable: Throwable) {
            Timber.i("$TAG catch triggered in postWebBELocation")
            RetrofitErrorHandler(throwable)

        }
    }




}