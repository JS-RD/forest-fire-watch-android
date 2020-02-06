package com.example.wildfire_fixed_imports

import androidx.lifecycle.LiveData
import com.example.wildfire_fixed_imports.model.LoadingDefinition

interface DataRepositoryWatcher {

    fun getCurrentLoading() :    LiveData<LoadingDefinition>


        fun loadingComplete() : LiveData<Boolean>

}