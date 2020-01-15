package com.example.wildfire_fixed_imports.viewmodel.vmclasses

import android.util.Patterns
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wildfire_fixed_imports.Coroutines
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.repository.UserRepository
import com.example.wildfire_fixed_imports.util.ApiException
import com.example.wildfire_fixed_imports.util.NoInternetException
import com.example.wildfire_fixed_imports.view.auth.AuthListener
import com.example.wildfire_fixed_imports.view.loginRegistration.LoginFormState
import com.example.wildfire_fixed_imports.view.loginRegistration.LoginResult

class LoginAuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    var email: String? = null
    var password: String? = null
    var passwordconfirm: String? = null

    var authListener: AuthListener? = null

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult



    fun onLoginButtonClick(view: View){
        authListener?.onStarted()
        if(email.isNullOrEmpty() || password.isNullOrEmpty()){
            authListener?.onFailure("Invalid email or password")
            return
        }

        Coroutines.main {
            try {
                val authResponse = repository.userLogin(email!!, password!!)
                authResponse.user?.let {
                    authListener?.onSuccess(it)
                    repository.userSignup(it)
                    return@main
                }
                authListener?.onFailure(authResponse.message!!)
            }catch(e: ApiException){
                authListener?.onFailure(e.message!!)
            }catch (e: NoInternetException){
                authListener?.onFailure(e.message!!)
            }
        }

    }


    fun onSignupButtonClick(view: View){
        authListener?.onStarted()


        if(email.isNullOrEmpty()){
            authListener?.onFailure("Email is required")
            return
        }

        if(password.isNullOrEmpty()){
            authListener?.onFailure("Please enter a password")
            return
        }

        if(password != passwordconfirm){
            authListener?.onFailure("Password did not match")
            return
        }


        Coroutines.main {
            try {
                val authResponse = repository.userSignup( email!!, password!!)
                authResponse.user?.let {
                    authListener?.onSuccess(it)

                    return@main
                }
                authListener?.onFailure(authResponse.message!!)
            }catch(e: ApiException){
                authListener?.onFailure(e.message!!)
            }catch (e: NoInternetException){
                authListener?.onFailure(e.message!!)
            }
        }

    }


    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = repository.userLogin(username, password)

        if (result is Result.Success) {
            _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }




}