package com.example.wildfire_fixed_imports.viewmodel.vmclasses


import androidx.lifecycle.*
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.DSFires
import com.example.wildfire_fixed_imports.viewmodel.network_controllers.FireDSController
import kotlinx.coroutines.launch
import timber.log.Timber

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wildfire_fixed_imports.methodName
import com.example.wildfire_fixed_imports.viewmodel.MasterController


class MapViewModel : ViewModel() {

    val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    lateinit var targetMaster: MasterController

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text




    fun setMyMasterController(masterController: MasterController)
    {
        targetMaster = masterController
    }

    fun startFireRetrieval() {
        viewModelScope.launch {
            targetMaster.startFireService()
        }

    }

    fun stopFireRetrieval() {
        viewModelScope.launch {
            targetMaster.stopFireService()
        }
    }

    fun startAQIRetrieval() {
        viewModelScope.launch {
            targetMaster.startAQIService()
        }

    }

    fun stopAQIRetrieval() {
        viewModelScope.launch {
            targetMaster.stopAQIService()
        }
    }


}

class MapViewModelFactory() : ViewModelProvider.Factory {


    companion object {
        val hashMapViewModel = HashMap<String, ViewModel>()
        fun addViewModel(key: String, viewModel: ViewModel){
            hashMapViewModel.put(key, viewModel)
        }
        fun getViewModel(key: String): ViewModel? {
            return hashMapViewModel[key]
        }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            val key = "HomeViewModel"
            if(hashMapViewModel.containsKey(key)){
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