package com.example.wildfire_fixed_imports.viewmodel.vmclasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DebugViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is debug Fragment"
    }
    val text: LiveData<String> = _text

    private val _btn1 = MutableLiveData<String>().apply {
        value = "debug btn 1"
    }
    val btn1: LiveData<String> = _btn1

    private val _btn2 = MutableLiveData<String>().apply {
        value = "debug btn 2"
    }
    val btn2: LiveData<String> = _btn2

    private val _btn3 = MutableLiveData<String>().apply {
        value = "debug btn 3"
    }
    val btn3: LiveData<String> = _btn3

    private val _btnSetup1 = MutableLiveData<String>().apply {
        value = "setup btn 1"
    }
    val btnSetup1: LiveData<String> = _btnSetup1

    private val _btnSetup2 = MutableLiveData<String>().apply {
        value = "setup btn 2"
    }
    val btnSetup2: LiveData<String> = _btnSetup2
}