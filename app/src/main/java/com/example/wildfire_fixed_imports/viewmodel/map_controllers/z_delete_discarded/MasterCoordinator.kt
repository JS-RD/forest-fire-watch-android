
package com.example.wildfire_fixed_imports.viewmodel.map_controllers.z_delete_discarded

import android.app.Activity

import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.AQIStations
import com.example.wildfire_fixed_imports.model.DSFires
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper
import com.example.wildfire_fixed_imports.model.WebBELocation
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.viewmodel.map_controllers.MapDrawController
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean


/*
*           MasterCoordinator
*   MasterCoordinator is repsonsible for recieving instructions on what lens are to be drawn to the map and then gathering data from network controller
*  consoloidating and managing that data and then sending  instructions to the view controllers on what to draw
*
*  MasterCoordinator is the central joining point of the controllers, the viewmodel and the view
* this is perhaps a violation of MVVM as stated but it really seems to be the best choice from what we can reckon for this app.
*
*
* *//*

 class MasterCoordinator() {

    //set correct mapbox map and the view containing the mapbox map via dependency injection

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()


    private val fireDSController by lazy {
        applicationLevelProvider.fireDSController
    }

    private val aqidsController by lazy {
        applicationLevelProvider.aqidsController
    }

    private val mapDrawController by lazy {
        applicationLevelProvider.mapDrawController
    }
    //additional dependency injection
    private val currentActivity: Activity = applicationLevelProvider.currentActivity


    //grab the viewmodel
    private val mapViewModel = applicationLevelProvider.appMapViewModel


    private var fireInitialized = false
    private var AQIInitialized = false


    //create live data, mutables amd observers
    private val _fireData = MutableLiveData<List<DSFires>>().apply { value = listOf<DSFires>() }
    val fireData: LiveData<List<DSFires>> = _fireData
    private var fireObserver: Observer<List<DSFires>>

    private val _AQIStations = MutableLiveData<List<AQIStations>>() //.apply { value = listOf<AQIStations>() }
    val AQIStations: LiveData<List<AQIStations>> = _AQIStations
    private var AQIStationObserver: Observer<List<AQIStations>>

    private val _fireGeoJson = MutableLiveData<String>().apply { value = "" }
    val fireGeoJson: LiveData<String> = _fireGeoJson
    private var fireGeoJsonObserver: Observer<String>

    private val _AQIGeoJson = MutableLiveData<String>().apply { value = "" }
    val AQIGeoJson: LiveData<String> = _AQIGeoJson
    private var AQIGeoJsonObserver: Observer<String>


    //atomicbooleans to allow for properly checking if streams are functioning or not even in asyncronous code
    var isFiresServiceRunning = AtomicBoolean()
    var isAQIdatasServiceRunning = AtomicBoolean()

    //the symbol manager for maintaining icons
    private val symbolManager: SymbolManager by lazy {
        applicationLevelProvider.symbolManager
    }

var AQIJOBS:Job = Job()
var FIREJOBS:Job = Job()
    val TAG: String get() = "search\n class: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"


    init {

        Timber.i("$TAG init")
        fireObserver = Observer { list ->
            Timber.i("$TAG init create fire observer")
            if (!fireInitialized && !list.isNullOrEmpty()) {
              Timber.i("$TAG init fire list reached observer ")
                // removeAllFires()
                _fireGeoJson.postValue(mapDrawController.makeFireGeoJson(list))
            } else if (!list.isNullOrEmpty()) {
                Timber.i("$TAG new aqi list reached observer")
                _fireGeoJson.postValue(mapDrawController.makeFireGeoJson(list))
            }
        }

        AQIStationObserver = Observer { list ->
            Timber.i("$TAG init create aqi observer")
            if (!AQIInitialized && !list.isNullOrEmpty()) {
                Timber.i("$TAG  init aqi station list reached observer ")
                // make some geojson out of the data
                _AQIGeoJson.postValue(mapDrawController.makeAQIGeoJson(list))
                AQIInitialized = true


            } else if (!list.isNullOrEmpty()) {
                _AQIGeoJson.postValue(mapDrawController.makeAQIGeoJson(list))

            }

        }

        fireGeoJsonObserver = Observer {
            Timber.i("$TAG init create fire geojson observer")
            if (!it.isNullOrBlank()) {
                Timber.i("$TAG force redraw from firegeojson")
                mapViewModel.triggerMapRedraw()
            }

        }


        AQIGeoJsonObserver = Observer {
            Timber.i("$TAG init create AQI geojson observer")
            if (!it.isNullOrBlank()) {
                Timber.i("$TAG force redraw from aqigeojson")
                mapViewModel.triggerMapRedraw()
            }

        }



        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        fireData.observe(currentActivity as LifecycleOwner, fireObserver)
        */
/* AQIData.observe(currentActivity as LifecycleOwner, AQIObserver)*//*

        AQIStations.observe(currentActivity as LifecycleOwner, AQIStationObserver)

        fireGeoJson.observe(currentActivity as LifecycleOwner, fireGeoJsonObserver)
        AQIGeoJson.observe(currentActivity as LifecycleOwner, AQIGeoJsonObserver)

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

    suspend fun startFireService() {
        Timber.i("$TAG initialized")
        isFiresServiceRunning.set(true)
        var countup = 0
        while (isFiresServiceRunning.get()) {
            FIREJOBS = Job()
            val systemmilli = System.currentTimeMillis()
            FIREJOBS = withContext(FIREJOBS) {
                this.launch {
                    val result = fireDSController.getDSFireLocations()
                    if (result is SuccessFailWrapper.Success) {
                        _fireData.postValue(result.value ?: listOf())
                    } else {
                        when (result) {
                            is SuccessFailWrapper.Throwable -> Timber.i(result.message)
                            is SuccessFailWrapper.Fail -> Timber.i(result.message)
                            else -> Timber.i(result.toString())
                        }
                    }
                }
            }
            // delay(300000)
            delay(300000)
            Timber.i("$TAG system milli: $systemmilli")
            Timber.i("\"$TAG countup: ${countup++}")

        }


    }
    suspend fun startAQIService() {
        Timber.i("$javaClass $methodName startaqi initialized")
        isAQIdatasServiceRunning.set(true)
        var countup = 0
        while (isAQIdatasServiceRunning.get()) {
            val systemmilli = System.currentTimeMillis()
            //if we don't have aqistations yet, go get ee
            AQIJOBS=Job()
                AQIJOBS = withContext(AQIJOBS) {
                    this.launch {
                        val result = getAQIstations()
                        if (result != null) {
                            //now that we got em, call the data retriever
                            _AQIStations.postValue(result)
                            applicationLevelProvider.localUser?.mAqiStations = result.toMutableList()
                        } else {
                            Timber.i("$TAG failed to load AQI Stations")
                        }
                    }
                }



            delay(6000000)
            Timber.i("$TAG system milli: $systemmilli")
            Timber.i("\"$TAG countup: ${countup++}")

        }

    }
     fun stopFireService() {
        //Potential issue if job running?
        AQIJOBS.cancel()
        isFiresServiceRunning.set(false)
    }

     fun stopAQIService(){
        //Potential issue if job running?
        FIREJOBS.cancel()
        isAQIdatasServiceRunning.set(false)
    }

}
    //get aqi stations for each location, or for users current location if unspecified
    //send that to the view controller

 */
/*   @SuppressLint("BinaryOperationInTimber")
    suspend fun getAQIdata() {
        Timber.i("$TAG begin getaqidata method")
        if(AQIStations.value.isNullOrEmpty()){
            //if this is empty, delay 1 second to avoid any ugly loops and attempt to get the servers again
            startAQIService()
        }
        else{
          *//*
*/
/*  var listOfFreshNodes = mutableListOf<AQIdata>()*//*
*/
/*
            val mapStationToData = mutableMapOf<AQIStations,AQIdata>()
            for (i in (AQIStations.value as List<AQIStations>).indices) {
                //TODO() IMPLEMENT SYSTEM TO RESTRICT DETAILED DATA CALLS OR AT LEAST ALERT USER THAT IT MAY TAKE A LONNNNNGGGG TIME (SEVERAL MINUTES FOR 500+ CALLS(
                //AS THE CODE CAN DO NOTHING WITH DETAILED DATA AT THIS TIME, RUN ONLY OVER THE FIRST 30 ENTRIES AND THEN QUIT.
                if (i<= 30) {
                    Timber.i("$TAG getaqidata i=$i")
                    val current = (AQIStations.value as List<AQIStations>)[i]
                    val result = aqidsController.getAQIData(current.lat, current.lon)

                    if (result is SuccessFailWrapper.Success
                            && result.value != null
                           ) {
                        *//*
*/
/*  listOfFreshNodes.add(result.value)*//*
*/
/*
                        mapStationToData[current] = result.value

                    } else {
                        Timber.i("$TAG failure at \n current AQI Station: $current \n result failure: ${result}")
                    }
                }
                else {
                    Timber.i("$TAG broke at i=$i")
                    return
                }
            }
            Timber.i("\n $TAG final full data \n size of new data ${mapStationToData.size}\n size of old data: ${mapAQIStationToAQIData.size}" +
                    " \n old data ${mapAQIStationToAQIData.values}" +
                    "\n new data ${mapStationToData.values}")
            if (!mapStationToData.isNullOrEmpty()) {
                if (mapStationToData.size > 30) {
                    handleAQIData(mapStationToData)
                    Timber.i("final full triggered for ${mapStationToData.values}")
                }
            }
            else{
                Timber.i("final full not triggered as mapstation is null or empty")
            }
        }
    }
    fun sendAQIDataToView(aqiList: MutableMap<AQIStations,AQIdata>){
        aqiDrawController.writeNewAqiData(aqiList)
    }

   fun handleAQIData(aqiList: MutableMap<AQIStations,AQIdata>){

       //TODO IMPLEMENT BETTER SYSTEM!

        Timber.i("$TAG ${aqiList}")
       diffAQIData(aqiList)
    }

   fun diffAQIData(AQIlist: MutableMap<AQIStations,AQIdata>) {
        //TODO("implement quality diffing, for now we will just check the whole list and replace if needed")
        if (AQIlist !=_AQImap.value) {
            _AQImap.postValue(AQIlist)
            Timber.i("aqi live data after diff ${_AQImap.value}")

        }
       _AQImap.postValue(AQIlist)
        Timber.i("aqi live data after diff ${_AQImap.value}")

    }


    fun removeAllAQIdata(AQIlist: MutableMap<AQIStations,AQIdata>) {
        aqiDrawController.eraseAqiData(AQIlist)
    }
      var AQIJOBS:Job = Job()

*//*


*/
/*
    fun addAllFires(DSFires:List<DSFires>) {
        for (i in DSFires.indices) {
            val current = DSFires[i]
            Timber.i("$i and ${current.toString()}")

            CoroutineScope(Dispatchers.Main).launch {

                symbolController.addFireSymbol(current.latlng(), current.name, current.type)

            }

        }

    }



    fun diffFireData(fireList: List<DSFires>) {
        //TODO("implement quality diffing, for now we will just check the whole list and replace if needed")
        if (fireList !=_fireData.value) {
            _fireData.postValue(fireList)
            Timber.i("firedata live data after diff ${fireData.value}")
        }

        Timber.i("firedata live data after diff ${fireData.value}")

    }

    fun removeAllFires() {
        targetMap.markers.removeAll(targetMap.markers)
    }

    @Deprecated("not used at this time")
    fun addbackgroundtomap() {
        targetMap.getStyle {
            val backgroundLayer = BackgroundLayer("background-layer")
            backgroundLayer.setProperties(PropertyFactory.backgroundColor(Color.BLUE))

            // Add background layer to map
            it.addLayer(backgroundLayer)
        }
    }



    }

*//*



*/
/*    private val _AQIData = MutableLiveData<List<AQIdata>>().apply {
        value= listOf<AQIdata>()
    }
    val AQIData: LiveData<List<AQIdata>> = _AQIData

     private var AQIObserver:Observer<List<AQIdata>>*//*

*/
/*    AQIObserver = Observer { list ->
           // Update the UI, in this case, a TextView.
           Timber.i("$TAG aqi observer")
           if (!AQIDataInitialized) {
               Timber.i("$TAG  init aqi list reached observer ${list.toString()}")
               AQIDataInitialized = true
               aqiDrawController.writeNewAqiData(list)
               oldAQIData=list.toMutableList()
           } else {
               Timber.i("$TAG new aqi list reached observer ${list.toString()}")
               removeAllAQIdata(oldAQIData)
               oldAQIData=list.toMutableList()
               aqiDrawController.writeNewAqiData(list)
           }
       }*/
