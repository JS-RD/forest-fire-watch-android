package com.example.wildfire_fixed_imports.view.loginRegistration

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
        val message:String,
        val firebase:Boolean,
        val webBE:Boolean,
        val fail:Boolean
)
data class LoginFormState(val usernameError: Int? = null,
                          val passwordError: Int? = null,
                          val isDataValid: Boolean = false)

data class LoggedInUserView(
        val displayName: String
        //... other data fields that may be accessible to the UI
)

data class RegistrationResult(
        val message:String,
        val firebase:Boolean,
        val webBE:Boolean,
        val fail:Boolean
)