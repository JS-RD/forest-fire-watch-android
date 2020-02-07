package com.example.wildfire_fixed_imports.viewmodel.view_model_classes


import com.example.wildfire_fixed_imports.ApplicationLevelProvider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wildfire_fixed_imports.util.StackTraceInfo
import com.example.wildfire_fixed_imports.util.className
import com.example.wildfire_fixed_imports.util.fileName
import com.example.wildfire_fixed_imports.viewmodel.map_controllers.MapDrawController
import timber.log.Timber


class MapViewModel : ViewModel() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val nearestNeighborApproach =applicationLevelProvider.experimentalNearestNeighborApproach
    private val TAG: String get() = "search\n class: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"
    private val mapDrawController = MapDrawController()

    fun onMapLoaded(){

        triggerMapRedraw()

    }

    fun triggerMapRedraw() {

        Timber.i(TAG)
        val aqistations = applicationLevelProvider.dataRepository.aqiGeoJson?.value ?: ""
        val firedata = applicationLevelProvider.dataRepository.fireGeoJson?.value ?: ""
        if (aqistations.isNotBlank() && firedata.isNotBlank()) {
            mapDrawController.createStyleFromGeoJson(aqistations, firedata)
            doExperimental()
        }

    }

    fun doExperimental(){
        Timber.i(TAG)
        val aqiNearestNeighborApproach=applicationLevelProvider.dataRepository.aqiNearestNeighborGeoJson?.value ?: ""
        if (aqiNearestNeighborApproach.isNotBlank() && aqiNearestNeighborApproach.isNotEmpty()) {
            nearestNeighborApproach.createCircleStyleFromGeoJson(aqiNearestNeighborApproach)
        }
    }

        fun startFireRetrieval() {
         /*   viewModelScope.launch {
               _fireServiceRunning.postValue(true)
            }*/
        }

        fun stopFireRetrieval() {
      /*      viewModelScope.launch {
               _fireServiceRunning.postValue(false)
            }*/

        }

        fun toggleFireRetrieval() {
  /*          if (fireServiceRunning.value as Boolean) {
                _fireServiceRunning.postValue(false)
                targetMaster.FIREJOBS.cancel()
                Timber.i("$TAG\n firejobs canceled \n firejobs.isactive=${targetMaster.FIREJOBS.isActive}")
            } else {
                _fireServiceRunning.postValue(true)
            }*/
        }

        fun toggleAQIRetrieval() {
   /*         if (aqiServiceRunning.value as Boolean) {
                _aqiServiceRunning.postValue(false)
                targetMaster.AQIJOBS.cancel()
                Timber.i("$TAG\n aqijobs canceled \n aqijobs.isactive=${targetMaster.AQIJOBS.isActive}")
            } else {
                _aqiServiceRunning.postValue(true)
            }*/
        }

        fun startAQIRetrieval() {
        /*    viewModelScope.launch {
                _aqiServiceRunning.postValue(true)
            }
*/
        }

        fun stopAQIRetrieval() {
   /*         viewModelScope.launch {
                _aqiServiceRunning.postValue(false)
            }*/
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