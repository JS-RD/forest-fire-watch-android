package com.example.wildfire_fixed_imports.model


import androidx.lifecycle.MutableLiveData
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.util.Coroutines
import com.example.wildfire_fixed_imports.util.StackTraceInfo
import com.example.wildfire_fixed_imports.util.className
import com.example.wildfire_fixed_imports.util.fileName
import com.example.wildfire_fixed_imports.viewmodel.map_controllers.MapDrawController
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.properties.Delegates

class DataRepository () {

    private val applicationLevelProvider: ApplicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val aqidsController = applicationLevelProvider.aqidsController
    private val fireDSController =applicationLevelProvider.fireDSController
    private val mapDrawController = applicationLevelProvider.mapDrawController


    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"

    var isFiresServiceRunning = AtomicBoolean()
    var isAQIdatasServiceRunning = AtomicBoolean()

    var observableData: String by Delegates.observable("Initial value") {
        property, oldValue, newValue ->
        println("${property.name}: $oldValue -> $newValue")
    }
    var aqiStations: MutableList<AQIStations> by Delegates.observable(
            mutableListOf()
    ){ property, oldValue, newValue ->
        oldValue.addAll(newValue)
        oldValue.toSet()
       aqiGeoJson= mapDrawController.makeAQIGeoJson(oldValue)
        Timber.d("$TAG \n aqiGeoJson wirrten \n ${aqiGeoJson.subSequence(0,aqiGeoJson.length)}")
    }
    var fireLocations: MutableList<DSFires> by Delegates.observable(
    mutableListOf()
    ){ property, oldValue, newValue ->
        oldValue.addAll(newValue)
        oldValue.toSet()
        fireGeoJson = mapDrawController.makeFireGeoJson(oldValue)
        Timber.d("$TAG \n firegeojson wirrten \n ${fireGeoJson.subSequence(0,fireGeoJson.length)}")
    }

     var aqiGeoJson:String =""
     var fireGeoJson:String = ""


    fun initData() {
        CoroutineScope(Dispatchers.IO).launch {
            aqiStations = getAQIstations()?.toMutableList() ?: mutableListOf()
        }
        CoroutineScope(Dispatchers.IO).launch  {
            fireLocations = getFires()?.toMutableList() ?: mutableListOf()
        }
    }

    suspend fun getFires(): List<DSFires>? {
        val result = fireDSController.getDSFireLocations()
        if (result is SuccessFailWrapper.Success) {
            return result.value ?: listOf()
        } else {
            when (result) {
                is SuccessFailWrapper.Throwable -> Timber.i(result.message)
                is SuccessFailWrapper.Fail -> Timber.i(result.message)
                else -> Timber.i(result.toString())
            }
        }
        return null
    }



    suspend fun getAQIstations(): List<AQIStations>? {
        // 8.0 is max distance that can be set and still expect maximum local resolution (or very close to it), any higher
        // and you will start to notice local stations dropping off the list
        // 15-20 can be a nice middle ground as you'll get your half of north america (West or east coast or central, a bit of central america
        // and a bit of canada -- will lose noticable number of local stations
        // 49-50 will get you from new york to LA but with SIGNIFICANT local resolution loss, I went from 9 stations within 15 miles of me, to 3 stations,
        // however this setting is really nice for doing an overview of the country or similar
        // 80 will cover the hemisphere you're on (roughly),  will lead to massive drop off of local resolution
        //      50 is the current demo setting as it allows us to explore the us but also see how there may be several aqi stations in your city and many in your
        //                  state
        //
        val userLocations:MutableList<WebBELocation?> = applicationLevelProvider.localUser?.mLocations
                ?: mutableListOf<WebBELocation?>()
                        .also{ Timber.e("$TAG mLocations is null, exiting getaqistations unsuccessfully"); return null}

        Timber.e("user locations in get aqi stations" )
        userLocations.forEach {
            Timber.i("$it another test "+it?.latitude)
        }

        val lstOfReturnData = ConcurrentLinkedQueue<SuccessFailWrapper<List<AQIStations>>>()
        userLocations.forEach {
            Timber.i("test 1-${System.currentTimeMillis()}")
            lstOfReturnData.add(
                    aqidsController.getAQIStations(
                            it?.latitude ?: 20.0,
                            it?.longitude ?: 20.0,
                            it?.radius ?: 5.0)
            )
        }
        val compositeResult = mutableListOf<AQIStations>()
        lstOfReturnData.forEach {
            Timber.i("test 2-${System.currentTimeMillis()}")
            if (it is SuccessFailWrapper.Success) {
                //this is what happens when you let the IDE demand shit out of you.
                compositeResult.addAll(it.value ?: listOf())
            } else {
                when (it) {
                    is SuccessFailWrapper.Throwable -> Timber.i("fail at ${it.message} ${it.t.toString()}")
                    is SuccessFailWrapper.Fail -> Timber.i("fail at ${it.message}")
                    else -> Timber.i("fail! $it")
                }
            }
        }
        Timber.e("test + "+compositeResult.toString().subSequence(0,30))
        return cleanAQIStationData(compositeResult)


    }

    fun cleanAQIStationData(aqiStations: List<AQIStations>?) :List<AQIStations> {
        val mutListResult = mutableListOf<AQIStations>()
        aqiStations?.toSet()?.forEach{
            if (!it.aqi.isBlank() && it.aqi.toIntOrNull() != null) {
                mutListResult.add(it)
            }
        }
        return mutListResult
    }




    companion object {
        private var mInstance: DataRepository? = null
        fun getInstance(): DataRepository {
            if (mInstance == null) mInstance = DataRepository()
            return mInstance as DataRepository
        }
    }
}