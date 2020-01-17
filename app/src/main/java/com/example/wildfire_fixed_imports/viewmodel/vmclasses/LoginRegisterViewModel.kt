package com.example.wildfire_fixed_imports.viewmodel.vmclasses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wildfire_fixed_imports.repository.UserRepository
import com.example.wildfire_fixed_imports.view.auth.AuthListener

class LoginRegisterViewModel(private val repository: UserRepository): ViewModel() {

    var email: String? = null
    var password: String? = null

    var authListener: AuthListener? = null
}