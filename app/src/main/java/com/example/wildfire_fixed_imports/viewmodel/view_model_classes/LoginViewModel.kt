package com.example.wildfire_fixed_imports.viewmodel.view_model_classes


import android.util.Patterns
import androidx.lifecycle.*
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper
import com.example.wildfire_fixed_imports.util.StackTraceInfo
import com.example.wildfire_fixed_imports.util.className
import com.example.wildfire_fixed_imports.util.fileName
import com.example.wildfire_fixed_imports.view.login_registration.LoginFormState
import com.example.wildfire_fixed_imports.view.login_registration.LoginResult
import com.example.wildfire_fixed_imports.view.login_registration.RegistrationResult
import kotlinx.coroutines.launch
import timber.log.Timber


class LoginViewModel() : ViewModel() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val firebaseAuthImpl = applicationLevelProvider.firebaseAuthImpl
    private val userWebBEController = applicationLevelProvider.userWebBEController
    private val _loginForm = MutableLiveData<LoginFormState>()
    private var firstLastPair = Pair("","")
    val loginFormState: LiveData<LoginFormState> = _loginForm



    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"




    //login and registration suit
    private val _loginResult = MutableLiveData<LoginResult>().apply {
        value = LoginResult(
                "", firebase = false, webBE = false, fail = false)
    }
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _registrationResult = MutableLiveData<RegistrationResult>().apply {
        value = RegistrationResult(
                "", firebase = false, webBE = false, fail = false)
    }
    val registrationResult: LiveData<RegistrationResult> = _registrationResult
    fun login(email: String, password: String) {
        _loginResult.postValue(LoginResult(
                "", firebase = false, webBE = false, fail = false
        ))
        viewModelScope.launch {
            val result = firebaseAuthImpl.signinCoroutine(email,password)
            when (result) {
                is SuccessFailWrapper.Success -> _loginResult.postValue(LoginResult(
                        "${result.message}", firebase = true, webBE = false, fail = false
                ))
                is SuccessFailWrapper.Exception -> _loginResult.postValue(LoginResult(
                        "${result.message} ${result.e}", firebase = false, webBE = false, fail = true
                ))
                is SuccessFailWrapper.Fail -> _loginResult.postValue(LoginResult(
                        "${result.message}", firebase = false, webBE = false, fail = true
                ))
                else -> _loginResult.postValue(LoginResult(
                        "unknown error", firebase = false, webBE = false, fail = true
                ))
            }
        }
        }
    fun loginWeb() {
        viewModelScope.launch {
            val result = userWebBEController.signin()
            when (result) {
                is SuccessFailWrapper.Success -> _loginResult.postValue(LoginResult(
                        "${result.message}", firebase = true, webBE = true, fail = false
                ))
                is SuccessFailWrapper.Exception -> _loginResult.postValue(LoginResult(
                        "${result.message} ${result.e}", firebase = false, webBE = false, fail = true
                ))
                is SuccessFailWrapper.Fail -> _loginResult.postValue(LoginResult(
                        "${result.message}", firebase = false, webBE = false, fail = true
                ))
                is SuccessFailWrapper.Throwable ->_loginResult.postValue(LoginResult(
                        "${result.message}", firebase = false, webBE = false, fail = true
                ))
                else -> _loginResult.postValue(LoginResult(
                        "unknown error", firebase = false, webBE = false, fail = true
                ))
            }
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

    fun registerNewUser(email: String,password: String,firstname:String,lastname:String){
        _registrationResult.postValue(RegistrationResult(
                "", firebase = false, webBE = false, fail = false
        ))
        firstLastPair =Pair(firstname,lastname)
        viewModelScope.launch {
            when (val result = firebaseAuthImpl.registerCoroutine(email,password)) {
                is SuccessFailWrapper.Success -> _registrationResult.postValue(RegistrationResult(
                        "${result.message}", firebase = true, webBE = false, fail = false
                ))
                is SuccessFailWrapper.Exception -> _registrationResult.postValue(RegistrationResult(
                        "${result.message} ${result.e}", firebase = false, webBE = false, fail = true
                ))
                is SuccessFailWrapper.Fail -> _registrationResult.postValue(RegistrationResult(
                        "${result.message}", firebase = false, webBE = false, fail = true
                ))
                else -> _registrationResult.postValue(RegistrationResult(
                        "unknown error", firebase = false, webBE = false, fail = true
                ))
            }
        }
    }
    fun registerNewUserWeb(){
        viewModelScope.launch {
            val result = userWebBEController.register(
                    firstName = firstLastPair.first,
                    lastName = firstLastPair.second
                    )
            when (result) {
                is SuccessFailWrapper.Success -> _registrationResult.postValue(RegistrationResult(
                        "${result.message}", firebase = true, webBE = true, fail = false
                ))
                is SuccessFailWrapper.Exception -> _registrationResult.postValue(RegistrationResult(
                        "${result.message} ${result.e}", firebase = false, webBE = false, fail = true
                ))
                is SuccessFailWrapper.Fail -> _registrationResult.postValue(RegistrationResult(
                        "${result.message}", firebase = false, webBE = false, fail = true
                ))
                is SuccessFailWrapper.Throwable ->_registrationResult.postValue(RegistrationResult(
                        "${result.message}", firebase = false, webBE = false, fail = true
                ))
                else -> _registrationResult.postValue(RegistrationResult(
                        "unknown error", firebase = false, webBE = false, fail = true
                ))
            }
        }

    }


    // A placeholder username validation check
     fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Timber.i("is ${Patterns.EMAIL_ADDRESS.matcher(username).matches()}")
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            Timber.i("is ${username.isNotBlank()}")
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
     fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

}
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                    /*              loginRepository = LoginRepository(
                                          dataSource = LoginDataSource()
                                  )*/
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}