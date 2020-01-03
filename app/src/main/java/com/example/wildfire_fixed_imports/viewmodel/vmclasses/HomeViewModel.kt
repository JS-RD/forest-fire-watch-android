package com.example.wildfire_fixed_imports.viewmodel.vmclasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wildfire_fixed_imports.viewmodel.view_controllers.MapController

class HomeViewModel : ViewModel() {

    lateinit var targetMap: MapController

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    fun setMyTargetMap(mapController: MapController)
    {
        targetMap = mapController
    }

    public fun mapIT(){
        print("oit got to fragment")
        targetMap.addbackgroundtomap()
    }



}

class ViewModelFactory() : ViewModelProvider.Factory {


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
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val key = "HomeViewModel"
            if(hashMapViewModel.containsKey(key)){
                return getViewModel(key) as T
            } else {
                addViewModel(key, HomeViewModel())
                return getViewModel(key) as T
            }
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
