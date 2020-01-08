package com.example.wildfire_fixed_imports.model

import androidx.lifecycle.ViewModel
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean


/*
*
* retrofit controlelr
*
* will manage the data calls through retrofit
*
* class that runns periodic method calls to retrofit to get data and then sends it on the the view model
*
* every 60 seconds, call a ssuspended function to retrofitimplementation, when async await completed then send that data
* to the viewmodel and if neccesarry viewmodel.difutil, if viewmodel finds difference, it tells map controler which draws the differences.
*
*
*
* */

class RetroController (){

    val appLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    val retrofitService = appLevelProvider.retrofitService

    var isFiresServiceRunning = AtomicBoolean()

    var runningFireServiceJob = Job()

    val ioCoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getFireLocations() {

        //TODO handle errors

        val results = retrofitService.getLocations()

        results.await()

    }

    suspend fun startFireService(Target: ViewModel){
        isFiresServiceRunning.set(true)

        while(isFiresServiceRunning.get()) {
            getFireLocations()
            delay(300000)
        }

    }

    suspend fun stopFireService(){
        //Potential issue if job running?
        isFiresServiceRunning.set(false)
    }





}