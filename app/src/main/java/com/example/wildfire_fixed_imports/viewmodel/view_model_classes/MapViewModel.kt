package com.example.wildfire_fixed_imports.viewmodel.view_model_classes


import androidx.lifecycle.*
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import kotlinx.coroutines.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wildfire_fixed_imports.model.DSFires
import com.example.wildfire_fixed_imports.util.StackTraceInfo
import com.example.wildfire_fixed_imports.util.className
import com.example.wildfire_fixed_imports.util.fileName
import com.example.wildfire_fixed_imports.viewmodel.MasterCoordinator
import com.example.wildfire_fixed_imports.viewmodel.map_controllers.MapDrawController
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import timber.log.Timber


class MapViewModel : ViewModel() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val currentActivity = applicationLevelProvider.currentActivity
    lateinit var targetMaster: MasterCoordinator
    val TAG: String get() = "search\n class: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"


    private val _fireServiceRunning = MutableLiveData<Boolean>().apply { value = false }
    val fireServiceRunning: LiveData<Boolean> = _fireServiceRunning
    private var fireObserver: Observer<Boolean> = Observer {
        if (it) {
            Timber.i("$TAG fire service logging true")
            viewModelScope.launch {
                targetMaster.startFireService()
            }
        } else {
            Timber.i("$TAG fire service logging false")
            viewModelScope.launch {
                targetMaster.stopFireService()
            }
        }
    }

    private val mediator = MediatorLiveData<Boolean>()
    private val mapDrawController = MapDrawController()
    private val _aqiServiceRunning = MutableLiveData<Boolean>().apply { value = false }
    val aqiServiceRunning: LiveData<Boolean> = _aqiServiceRunning
    private var aqiObserver: Observer<Boolean> = Observer {
        if (it) {
            Timber.i("$TAG fire service logging true")
            viewModelScope.launch {
                targetMaster.startAQIService()
            }
        } else {
            Timber.i("$TAG fire service logging false")
            viewModelScope.launch {
                targetMaster.stopAQIService()
            }
        }
    }

    fun triggerMapRedraw() {
        Timber.i(TAG)
        if (::targetMaster.isInitialized) {
            val aqistations = targetMaster.AQIGeoJson.value
            val firedata = targetMaster.fireGeoJson.value
            if (!aqistations.isNullOrEmpty() && !firedata.isNullOrEmpty()) {
                Timber.i("$TAG \naqi + fire not mull not empty")
                mapDrawController.createStyleFromGeoJson(aqistations, firedata)

            }
        }

    }


    fun onMapLoaded() {
        if (!::targetMaster.isInitialized) {
            Timber.i("$TAG \n resume initialize check ")

            targetMaster = MasterCoordinator()
            applicationLevelProvider.masterCoordinator = targetMaster
            fireServiceRunning.observe(currentActivity as LifecycleOwner, fireObserver)
            aqiServiceRunning.observe(currentActivity as LifecycleOwner, aqiObserver)

            //initialize symbol manager

            //initialize markert controller


        }
    }
        fun startFireRetrieval() {
            viewModelScope.launch {
                _fireServiceRunning.postValue(true)
            }

        }

        fun stopFireRetrieval() {
            viewModelScope.launch {
                _fireServiceRunning.postValue(false)
            }

        }

        fun toggleFireRetrieval() {
            if (fireServiceRunning.value as Boolean) {
                _fireServiceRunning.postValue(false)
            } else {
                _fireServiceRunning.postValue(true)
            }
        }

        fun toggleAQIRetrieval() {
            if (aqiServiceRunning.value as Boolean) {
                _aqiServiceRunning.postValue(false)
            } else {
                _aqiServiceRunning.postValue(true)
            }
        }

        fun startAQIRetrieval() {
            viewModelScope.launch {
                _aqiServiceRunning.postValue(true)
            }

        }

        fun stopAQIRetrieval() {
            viewModelScope.launch {
                _aqiServiceRunning.postValue(false)
            }
        }


    }

    @Suppress("UNCHECKED_CAST")
    class MapViewModelFactory() : ViewModelProvider.Factory {


        companion object {
            val hashMapViewModel = HashMap<String, ViewModel>()
            fun addViewModel(key: String, viewModel: ViewModel) {
                hashMapViewModel.put(key, viewModel)
            }

            fun getViewModel(key: String): ViewModel? {
                return hashMapViewModel[key]
            }
        }

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                val key = "HomeViewModel"
                if (hashMapViewModel.containsKey(key)) {
                    return getViewModel(key) as T
                } else {
                    addViewModel(key, MapViewModel())
                    return getViewModel(key) as T
                }
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }




/*   public fun mapIT(){
        print("oit got to fragment")
        targetMap.addbackgroundtomap()
    }*/