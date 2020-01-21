package com.example.wildfire_fixed_imports.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.wildfire_fixed_imports.*
import com.example.wildfire_fixed_imports.model.AQIStations
import com.example.wildfire_fixed_imports.model.AQIdata
import com.example.wildfire_fixed_imports.model.DSFires
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.viewmodel.map_controllers.AQIDrawController
import com.example.wildfire_fixed_imports.viewmodel.map_controllers.SymbolController
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.style.layers.BackgroundLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.whileSelect
import timber.log.Timber
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
* */
 class MasterCoordinator() {

    //set correct mapbox map and the view containing the mapbox map via dependency injection

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private val targetMap: MapboxMap by lazy {
        applicationLevelProvider.mapboxMap
    }
    private val mapboxView: View by lazy {
        applicationLevelProvider.mapboxView
    }

    private val fireDSController by lazy {
        applicationLevelProvider.fireDSController
    }

    private val aqidsController by lazy {
        applicationLevelProvider.aqidsController
    }

    private val aqiDrawController by lazy {
        AQIDrawController().also { applicationLevelProvider.aqiDrawController=it }
    }

    //additional dependency injection
    private val currentActivity : Activity = applicationLevelProvider.currentActivity


    //grab the viewmodel
    private val mapViewModel = applicationLevelProvider.appMapViewModel




    private var fireInitialized=false
    private var AQIInitialized=false
    private var AQIDataInitialized=false

    //create live data, mutables amd observers
    private val _fireData = MutableLiveData<List<DSFires>>().apply { value= listOf<DSFires>() }
    val fireData: LiveData<List<DSFires>> = _fireData
    private  var fireObserver:Observer<List<DSFires>>

    private val _AQIStations = MutableLiveData<List<AQIStations>>().apply { value= listOf<AQIStations>() }
    val AQIStations: LiveData<List<AQIStations>> = _AQIStations
    private var AQIStationObserver:Observer<List<AQIStations>>

    private val _AQImap = MutableLiveData<MutableMap<AQIStations,AQIdata>>().apply { value= mutableMapOf() }
    val AQImap: LiveData<MutableMap<AQIStations,AQIdata>> = _AQImap
    private var AQImapObserver:Observer<MutableMap<AQIStations,AQIdata>>

    private var mapAQIStationToAQIData = mutableMapOf<AQIStations,AQIdata>()
    private var oldAQIData= mutableMapOf<AQIStations,AQIdata>()

    //atomicbooleans to allow for properly checking if streams are functioning or not even in asyncronous code
    var isFiresServiceRunning = AtomicBoolean()
    var isAQIdatasServiceRunning = AtomicBoolean()

    //the symbol manager for maintaining icons
    private val symbolManager:SymbolManager by lazy {
        applicationLevelProvider.symbolManager
    }
    //symbol controller ref
    private val symbolController:SymbolController  by lazy {
        applicationLevelProvider.symbolController
    }

    val TAG:String get() =  "search\n class: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"

   /*
CoroutineScope(Dispatchers.IO).launch {
                    currentActivity.getLatestLocation()
    }
*/



    init {

       Timber.i("$TAG init")



        // Create the fire observer which updates the UI.
        fireObserver = Observer { list ->
            // Update the UI, in this case, a TextView.
            Timber.i("$TAG init create fire observer")
            if (!fireInitialized  && !list.isNullOrEmpty()) {
                Timber.i("$TAG init fire list reached observer ${list.toString()}")
                removeAllFires()
                addAllFires(list)
            } else if (!list.isNullOrEmpty()){
                Timber.i("$TAG new aqi list reached observer ${list.toString()}")
                addAllFires(list)
            }
        }

        AQIStationObserver = Observer { list ->
            // Update the UI, in this case, a TextView.
            Timber.i("$TAG init create aqi observer")
            if (!AQIInitialized && !list.isNullOrEmpty()) {
                Timber.i("$TAG  init aqi station list reached observer ${list.toString()}")
                //while we load the exact AQI data using the next function ,lets load the data we have now
                CoroutineScope(Dispatchers.Main).launch {
                    assembleInterimData()
                }
                AQIInitialized=true
                CoroutineScope(Dispatchers.IO).launch {
                    // at this point we have the option to use the aqi provided at the station level  as rough data,
                    // should likely do this
                    getAQIdata()
                    Timber.i("$TAG calling aqidata from aqistationobserver")
                }


            } else if (!list.isNullOrEmpty()){
                Timber.i("$TAG new aqi list reached observer ${list.toString()}")
                CoroutineScope(Dispatchers.IO).launch {
                    assembleInterimData()
                }
            }


        }
        AQImapObserver =Observer { map:MutableMap<AQIStations,AQIdata> ->
            Timber.i("$TAG aqi observer")
            if (!AQIDataInitialized  && !map.isNullOrEmpty()) {
                Timber.i("$TAG sau init aqi list reached observer ")
                AQIDataInitialized = true
                aqiDrawController.writeNewAqiData(map)
                oldAQIData=map
            } else if (!map.isNullOrEmpty()) {
                Timber.i("$TAG sau new aqi list reached observer ")
                removeAllAQIdata(oldAQIData)
                oldAQIData=map
                aqiDrawController.writeNewAqiData(map)
            }
        }

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        fireData.observe(currentActivity as LifecycleOwner, fireObserver)
       /* AQIData.observe(currentActivity as LifecycleOwner, AQIObserver)*/
        AQIStations.observe(currentActivity as LifecycleOwner, AQIStationObserver)
        AQImap.observe(currentActivity as LifecycleOwner, AQImapObserver)




        //the following can be deleted easily enough
        var iterator = 0
        val arrayOfStyles:ArrayList<String> = arrayListOf(
                Style.DARK,
                Style.MAPBOX_STREETS,
                Style.OUTDOORS,
                Style.LIGHT,
                Style.SATELLITE,
                Style.SATELLITE_STREETS,
                Style.TRAFFIC_DAY,
                Style.TRAFFIC_NIGHT)


        if (currentActivity is MainActivity) {
            currentActivity.setFabOnclick {
                Timber.i("$TAG iterator = ${iterator} \n size = ${arrayOfStyles.size}")
                    if(iterator>=arrayOfStyles.size-1) {
                        Timber.i("$TAG iterator>=arrayOfStyles.siz")
                        iterator=0
                    }
                else {
                        iterator++
                        Timber.i("$TAG iterator++ = $iterator")
                    }
                Timber.i("$TAG setting map style to ${arrayOfStyles[iterator]}")
                targetMap.setStyle(arrayOfStyles[iterator])
                   // heatMapController.initializeHeatMapExtended()
                }
                }


        }


    suspend fun getAQIstations():List<AQIStations>?{
        //TODO this will eventually need to check user prefs and react accordingly,
        // for now, we simply will check from the users location

        // 8.0 is max distance that can be set and still expect maximum local resolution (or very close to it), any higher
        // and you will start to notice local stations dropping off the list
        // 15-20 can be a nice middle ground as you'll get your half of north america (West or east coast or central, a bit of central america
        // and a bit of canada -- will lose noticable number of local stations
        // 49-50 will get you from new york to LA but with SIGNIFICANT local resolution loss, I went from 9 stations within 15 miles of me, to 3 stations,
        // however this setting is really nice for doing an overview of the country or similar
        // 80 will cover the hemisphere you're on (roughly),  will lead to massive drop off of local resolution
        //      50 is the current demo setting as it allows us to explore the us but also see how there may be several aqi stations in your city and many in your
        //                  state
        val currentLocal= applicationLevelProvider.userLocation.LatLng()

        val result=aqidsController.getAQIStations(
                currentLocal.latitude,
                currentLocal.longitude,
                50.0)
        if (result is SuccessFailWrapper.Success){
            Timber.i("$TAG result: ${result.value}")
            return result.value

        }
        else {
            when(result) {
                is SuccessFailWrapper.Throwable ->  Timber.i(result.message)
                is SuccessFailWrapper.Fail -> Timber.i(result.message).also { isAQIdatasServiceRunning.set(false) }.also {
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(applicationLevelProvider.applicationContext,
                                result.message, Toast.LENGTH_LONG).show()
                        applicationLevelProvider.showSnackbar("AQI service quitting", Snackbar.LENGTH_INDEFINITE)
                    }
                }
                else -> Timber.i(result.toString())
            }
        }
        return null
    }

    fun forceRedraw() {
        val mapboxMap =applicationLevelProvider.mapboxMap
        val mapboxStyle=applicationLevelProvider.mapboxStyle

        CoroutineScope(Dispatchers.Main).launch{
            aqiDrawController.writeNewAqiData(AQImap?.value ?: mutableMapOf<com.example.wildfire_fixed_imports.model.AQIStations,AQIdata>()
            )
            applicationLevelProvider.symbolManager.
            removeAllFires()
            addAllFires(fireData.value as List<DSFires>)
        }


    }


    //get aqi stations for each location, or for users current location if unspecified
    //get aqi data for each station
    //send that to the view controller
    suspend fun assembleInterimData() {
        if(AQIStations.value.isNullOrEmpty()){
            //if this is empty, delay 1 second to avoid any ugly loops and attempt to get the servers again
            startAQIService()
            Timber.i("$TAG aqi stations is empty or null")
        }
        else{


            /*  var listOfFreshNodes = mutableListOf<AQIdata>()*/
            val mapStationToData = mutableMapOf<AQIStations,AQIdata>()
            for (i in (AQIStations.value as List<AQIStations>).indices) {
                val current =(AQIStations.value as List<AQIStations>)[i]
                    /*  listOfFreshNodes.add(result.value)*/
            //    Timber.i("$TAG \n search $current \n current.aqi")
                if (current.aqi.toIntOrNull() !=null) {
                    mapStationToData[current] = AQIdata(current.aqi.toInt())
                   // Timber.i("$TAG map entry $i \n ${mapStationToData[current]}\n ${current} $mapStationToData")
                    }
            }
            Timber.i("$TAG final map $mapStationToData")
            handleAQIData(mapStationToData)

        }
    }
    @SuppressLint("BinaryOperationInTimber")
    suspend fun getAQIdata() {
        Timber.i("$TAG begin getaqidata method")
        if(AQIStations.value.isNullOrEmpty()){
            //if this is empty, delay 1 second to avoid any ugly loops and attempt to get the servers again
            startAQIService()
        }
        else{
          /*  var listOfFreshNodes = mutableListOf<AQIdata>()*/
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
                        /*  listOfFreshNodes.add(result.value)*/
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
    suspend fun startAQIService() {
        Timber.i("$javaClass $methodName initialized")
        isAQIdatasServiceRunning.set(true)
        var countup = 0
        while (isAQIdatasServiceRunning.get()) {
            val systemmilli = System.currentTimeMillis()
            //if we don't have aqistations yet, go get em
            if (AQIStations.value.isNullOrEmpty()) {
                AQIJOBS = CoroutineScope(Dispatchers.IO).launch {
                    val result = getAQIstations()
                    if (result != null) {
                        //now that we got em, call the data retriever
                        _AQIStations.postValue(result)
                    } else {
                        Timber.i("$TAG failed to load AQI Stations")
                    }
                }


            }
            //if we have aqi stations then...
            else {
                getAQIdata()
            }
            // delay a pretty long time, unlikely to be updated terribly quickly
            delay(6000000)
            Timber.i("$TAG system milli: $systemmilli")
            Timber.i("\"$TAG countup: ${countup++}")

        }

    }

    suspend fun stopAQIService(){
        //Potential issue if job running?
        isAQIdatasServiceRunning.set(false)
    }

    fun addAllFires(DSFires:List<DSFires>) {
        for (i in DSFires.indices) {
            val current = DSFires[i]
            Timber.i("$i and ${current.toString()}")

            CoroutineScope(Dispatchers.Main).launch {

                symbolController.addFireSymbol(current.latlng(), current.name, current.type)

            }

        }

    }

    suspend fun startFireService(){
        Timber.i("$TAG initialized")
        isFiresServiceRunning.set(true)
        var countup = 0
        while(isFiresServiceRunning.get()) {
            val systemmilli = System.currentTimeMillis()

            val result=fireDSController.getDSFireLocations()
            if (result is SuccessFailWrapper.Success){
                handleFireData(result.value?: listOf())
            }
            else {
                when(result) {
                    is SuccessFailWrapper.Throwable ->  Timber.i(result.message)
                    is SuccessFailWrapper.Fail -> Timber.i(result.message)
                    else -> Timber.i(result.toString())
                }
            }
            // delay(300000)
            delay(300000)
            Timber.i("$TAG system milli: $systemmilli")
            Timber.i("\"$TAG countup: ${countup++}")

        }


    }

    suspend fun stopFireService(){
        //Potential issue if job running?
        isFiresServiceRunning.set(false)
    }

    fun handleFireData(fireList: List<DSFires>){
        Timber.i(fireList.toString())
        diffFireData(fireList)
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



/*    private val _AQIData = MutableLiveData<List<AQIdata>>().apply {
        value= listOf<AQIdata>()
    }
    val AQIData: LiveData<List<AQIdata>> = _AQIData

     private var AQIObserver:Observer<List<AQIdata>>*/
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