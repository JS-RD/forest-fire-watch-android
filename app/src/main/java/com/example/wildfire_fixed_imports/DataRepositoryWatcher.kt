package com.example.wildfire_fixed_imports

import androidx.lifecycle.LiveData

interface DataRepositoryWatcher {

    fun getCurrentLoading() : LiveData<String>

    fun loadingComplete() : LiveData<Boolean>

}