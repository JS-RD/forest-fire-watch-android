package com.example.wildfire_fixed_imports.view.auth

import com.example.wildfire_fixed_imports.model.User

interface AuthListener {
    fun onStarted()
    fun onSuccess(user: User)
    fun onFailure(message: String)
}