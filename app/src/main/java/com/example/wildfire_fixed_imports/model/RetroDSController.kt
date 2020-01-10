package com.example.wildfire_fixed_imports.model

import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.MapViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean

class RetroDSController ( val mapViewModel: MapViewModel) {


    val appLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    val retrofitDSService = appLevelProvider.retrofitDSService

    var isFiresServiceRunning = AtomicBoolean()

    var runningFireServiceJob = Job()

    val ioCoroutineScope = CoroutineScope(Dispatchers.IO)

    suspend fun getFireLocations() {

        //TODO handle errors

        val results = retrofitDSService.getDSFireLocations()
        mapViewModel.handleFireData(results)

    }

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

    suspend fun stopFireService(){
        //Potential issue if job running?
        isFiresServiceRunning.set(false)
    }
}