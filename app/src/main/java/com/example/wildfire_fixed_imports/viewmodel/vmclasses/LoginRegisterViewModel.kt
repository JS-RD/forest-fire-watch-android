package com.example.wildfire_fixed_imports.viewmodel.vmclasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginRegisterViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is login Fragment"
    }
    val text: LiveData<String> = _text
}