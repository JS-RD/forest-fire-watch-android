package com.example.wildfire_fixed_imports.view.auth



interface AuthListener {
    fun onStarted()
    fun onSuccess(user: String)
    fun onFailure(message: String)
}