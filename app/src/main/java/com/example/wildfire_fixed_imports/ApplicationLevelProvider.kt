package com.example.wildfire_fixed_imports

import android.app.Application
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.ViewModelFactory

class ApplicationLevelProvider : Application() {

    companion object {
        val viewModelFactory = ViewModelFactory()
    }


}