package com.example.wildfire_fixed_imports.viewmodel.vmclasses


import androidx.lifecycle.*
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.DSFires
import com.example.wildfire_fixed_imports.model.FireLocations
import com.example.wildfire_fixed_imports.model.RetroDSController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.interfaces.DSAKey

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wildfire_fixed_imports.viewmodel.view_controllers.MapController


class MapViewModel : ViewModel() {

    val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    val retroDSController by lazy {
        RetroDSController(this)
    }

    lateinit var targetMap: MapController

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    private val _fireData = MutableLiveData<List<DSFires>>().apply {
       value= listOf<DSFires>()
    }
    val fireData: LiveData<List<DSFires>> = _fireData

    fun setMyTargetMap(mapController: MapController)
    {
        targetMap = mapController
    }

    fun startFireRetrieval() {
        viewModelScope.launch {
            retroDSController.startFireService()
        }
    }


    fun stopFireRetrieval() {
        viewModelScope.launch {
            retroDSController.stopFireService()
        }
    }
    fun handleFireData(fireList: List<DSFires>){

       Timber.i(fireList.toString())
        diffFireData(fireList)
    }

    fun diffFireData(fireList: List<DSFires>) {
        //TODO("implement quality diffing, for now we will just check the whole list and replace if needed")
        if (fireList !=_fireData.value) {
            _fireData.postValue(fireList)
            fireData.value
            Timber.i("firedata live data after diff ${fireData.value}")
            Timber.i("_firedata live data after diff ${fireData.value}")
        }
        _fireData.postValue(fireList)
        fireData.value
        Timber.i("firedata live data after diff ${fireData.value}")
        Timber.i("_firedata live data after diff ${fireData.value}")
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