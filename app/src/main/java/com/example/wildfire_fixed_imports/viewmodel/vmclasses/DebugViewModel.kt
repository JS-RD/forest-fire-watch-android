package com.example.wildfire_fixed_imports.viewmodel.vmclasses

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildfire_fixed_imports.ApplicationLevelProvider

import com.example.wildfire_fixed_imports.networking.AuthenticationDataRepository
import com.example.wildfire_fixed_imports.networking.FirebaseAuthImpl
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch
import timber.log.Timber

class DebugViewModel : ViewModel() {


    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private val firebaseAuthImpl by lazy {
     applicationLevelProvider.firebaseAuthImpl
 }
    private val debugFragment by lazy {
        applicationLevelProvider.debugFragment
    }
    private val TAG = "DebugViewModel"

    private val authenticationState  by lazy {
        applicationLevelProvider.authenticationState
    }
    fun resetBtnDisplayValues() {

    }

    val autheticationRepo = AuthenticationDataRepository(applicationLevelProvider.firebaseAuth)

 fun setUpAuthTesting() {
     _btnSetup1.postValue("Auth Testing (current)")
     val btn1Lambda = {

         val randNumber =Math.random().toString()

         val result =firebaseAuthImpl.registerNewUserAccount("mygosh$randNumber@sauce.com","apasswordhere")
         Timber.i("$TAG email: mygosh$randNumber@sauce.com")
         Timber.i("$TAG password: apasswordhere")
         Timber.i("$TAG registerNewUserAccount = ${applicationLevelProvider.firebaseUser.toString()}")
         _text.value ="$TAG registerNewUserAccount = ${applicationLevelProvider.firebaseUser.toString()}"

         Timber.i("$TAG  ${applicationLevelProvider.firebaseUser?.email}" +
                 "${applicationLevelProvider.firebaseUser?.describeContents().toString()} " +
                 "${applicationLevelProvider.firebaseUser?.displayName}")

     }
     debugFragment.changeBtnFunction("btn1", btn1Lambda)
     _btn1.postValue("register new account")

     val btn2Lambda = {

         val result = firebaseAuthImpl.signinUser("saucefanone@gmail.com", "apassword")
         Timber.i("$TAG saucefanone@gmail.com")
         Timber.i("$TAG password: apassword")
         Timber.i("$TAG signinUser = ${applicationLevelProvider.firebaseUser.toString()}")
         _text.value = "$TAG signinUser = ${applicationLevelProvider.firebaseUser.toString()}"
         Timber.i("$TAG  ${applicationLevelProvider.firebaseUser?.email}" +
                 "${applicationLevelProvider.firebaseUser?.describeContents().toString()} " +
                 "${applicationLevelProvider.firebaseUser?.displayName}")
     }
     debugFragment.changeBtnFunction("btn2", btn2Lambda)
     _btn2.postValue("login")

     val btn3Lambda = {
         val job = viewModelScope.launch {

              val result = firebaseAuthImpl.signinCoroutine("saucefanone@gmail.com","apassword")

             Timber.i("$TAG, Success ${result.toString()}")
        /*     try {
                 Timber.i("$TAG saucefanone@gmail.com")
                 Timber.i("$TAG password: apassword")
           autheticationRepo.authenticate("saucefanone@gmail.com", "apassword")?.let {
                     authenticationState.postValue(
                             true
                     )
               applicationLevelProvider.firebaseUser=it
               Timber.i("$TAG.vmscope ${it.toString()} and $it")
               Timber.i("$TAG signinUser = ${applicationLevelProvider.firebaseUser.toString()}")
               _text.value = "$TAG signinUser = ${applicationLevelProvider.firebaseUser.toString()}"
               Timber.i("$TAG  ${applicationLevelProvider.firebaseUser?.email}" +
                       "${applicationLevelProvider.firebaseUser?.describeContents().toString()} " +
                       "${applicationLevelProvider.firebaseUser?.displayName}")
                 } ?: run {
                     authenticationState.postValue(
                             false)

                 }
             } catch (e: FirebaseAuthException) {
                 authenticationState.postValue(
                         false)
                 Timber.i("$TAG.vmscope exception: $e")
             }*/
         }

     }
     debugFragment.changeBtnFunction("btn3", btn3Lambda)
     _btn3.postValue("login alt coroutine")

     val btn4Lambda = {
         val job = viewModelScope.launch {
             val randNumber =Math.random().toString()

             val result = firebaseAuthImpl.registerCoroutine( "mygosh$randNumber@sauce.com","apasswordhere")

             Timber.i("$TAG, Success ${result.toString()} \n result email: ${result?.email} \n applevel email: ${applicationLevelProvider.firebaseUser?.email}")
         }

     }
     debugFragment.changeBtnFunction("btn4", btn4Lambda)
     _btn4.postValue("register alt coroutine")

 }
/*

    init {
        val btnSetup1Lambda = { setUpAuthTesting()}
        debugFragment.changeBtnFunction("btnSetup1",btnSetup1Lambda)


    }
*/

    //sets up live data for various components of the UI, a bit overkill but will allow testing to avoid issues with config changes, etc
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

    private val _btn4 = MutableLiveData<String>().apply {
        value = "debug btn 4"
    }
    val btn4: LiveData<String> = _btn4

    private val _btnSetup1 = MutableLiveData<String>().apply {
        value = "auth debug"
    }
    val btnSetup1: LiveData<String> = _btnSetup1

    private val _btnSetup2 = MutableLiveData<String>().apply {
        value = "setup btn 2"
    }
    val btnSetup2: LiveData<String> = _btnSetup2
}