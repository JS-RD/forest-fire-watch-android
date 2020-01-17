package com.example.wildfire_fixed_imports.view.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.wildfire_fixed_imports.repository.UserRepository
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.LoginAuthViewModel

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginAuthViewModel(repository) as T
    }
}