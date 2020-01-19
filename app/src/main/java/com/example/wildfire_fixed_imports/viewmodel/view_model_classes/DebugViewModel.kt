package com.example.wildfire_fixed_imports.viewmodel.view_model_classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper

import kotlinx.coroutines.launch
import timber.log.Timber

data class ETnTVpair(val et:MutableLiveData<String>,val tv:MutableLiveData<String>)

/*
*
* this class has quickly become completely absurd and needs to be removed, it's been fun to play with
* but this terrible, will delete asap
* */
class DebugViewModel : ViewModel() {

    var publicStringHolder1 =""
    var publicStringHolder2 =""
    var publicStringHolder3 =""
    var publicStringHolder4 =""

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private val firebaseAuthImpl by lazy {
     applicationLevelProvider.firebaseAuthImpl
 }
    private val debugFragment by lazy {
        applicationLevelProvider.debugFragment
    }
    private val firebaseAuth by lazy {
        applicationLevelProvider.firebaseAuth
    }
    private val retroImplWeb by lazy {
        applicationLevelProvider.retrofitWebService
    }
    private val userWebBEController by lazy {
        applicationLevelProvider.userWebBEController
    }
    private val userLocationWebBEController by lazy {
        applicationLevelProvider.userLocationWebBEController
    }
    private val TAG = "DebugViewModel"



   /* private val authenticationState  by lazy {
        applicationLevelProvider.authenticationState
    }*/


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

    private val _btn5 = MutableLiveData<String>().apply {
        value = "debug btn 5"
    }
    val btn5: LiveData<String> = _btn5

    private val _tv1 = MutableLiveData<String>().apply {
        value = "tv1"
    }
    val tv1: LiveData<String> = _tv1

    private val _tv2 = MutableLiveData<String>().apply {
        value = "tv2"
    }
    val tv2: LiveData<String> = _tv2

    private val _tv3 = MutableLiveData<String>().apply {
        value = "tv3"
    }
    val tv3: LiveData<String> = _tv3

    private val _tv4 = MutableLiveData<String>().apply {
        value = "tv4"
    }
    val tv4: LiveData<String> = _tv4

    private val _et1 = MutableLiveData<String>().apply {
        value = "et1"
    }
    val et1: LiveData<String> = _et1

    private val _et2 = MutableLiveData<String>().apply {
        value = "et2"
    }
    val et2: LiveData<String> = _et2

    private val _et3 = MutableLiveData<String>().apply {
        value = "et3"
    }
    val et3: LiveData<String> = _et3

    private val _et4 = MutableLiveData<String>().apply {
        value = "tv4"
    }
    val et4: LiveData<String> = _et4


    private val _btnCrash = MutableLiveData<String>().apply {
        value = "crash btn!"
    }
    val btnCrash: LiveData<String> = _btnCrash



    private val _btnSetup1 = MutableLiveData<String>().apply {
        value = "auth debug"
    }
    val btnSetup1: LiveData<String> = _btnSetup1

    private val _btnSetup2 = MutableLiveData<String>().apply {
        value = "setup btn 2"
    }
    val btnSetup2: LiveData<String> = _btnSetup2


    private val listOfLiveBtnStrings = mutableListOf<MutableLiveData<String>>(_btn1,_btn2,_btn3,_btn4,_btn5)



    fun resetBtnDisplayValues() {
        debugFragment.changeFocusedSetupgButton("blank")
        for (i in listOfLiveBtnStrings.indices){
            val current = listOfLiveBtnStrings[i]
            current.postValue("btn $i")
        }
    }
/*
    deleteWebBELocation
    getWebBELocations
    updateWebBELocation
    postWebBELocation
*/

    fun setUpWebLocationTesting() {
        debugFragment.changeFocusedSetupgButton("webloc")

        val btn1Lambda = {
            var sauce = "sacue"
            viewModelScope.launch {

                val result = userLocationWebBEController.postWebBELocation(publicStringHolder2,publicStringHolder1.toInt())

                when (result) {
                    is SuccessFailWrapper.Success -> Timber.i("success ${result.value}")
                    is SuccessFailWrapper.Fail -> Timber.i("fail $result")
                    is SuccessFailWrapper.Throwable -> Timber.i("throwable $result")
                    else -> Timber.i("else $result")


                }
                _text.postValue("$TAG registerNewUserAccount = ${result}")
            }
            sauce += sauce


        }
        _tv1.postValue("radius")
        _tv2.postValue("address")
        _tv3.postValue("lat")
        _tv4.postValue("lng")
        _et1.postValue("radius")
        _et2.postValue("address")
        _et3.postValue("lat")
        _et4.postValue("lng")

        debugFragment.changeBtnFunction("btn1", btn1Lambda)
        _btn1.postValue("post addy plus radius")

        val btn2Lambda = {
            var sauce = "sacue"
            viewModelScope.launch {


                val result = userLocationWebBEController.getWebBELocations()

                when (result) {
                    is SuccessFailWrapper.Success -> Timber.i("success ${result.value}")
                    is SuccessFailWrapper.Fail -> Timber.i("fail $result")
                    is SuccessFailWrapper.Throwable -> Timber.i("throwable $result")
                    else -> Timber.i("else $result")


                }
                _text.postValue("$TAG registerNewUserAccount = ${result}")
            }
            sauce += sauce

        }
        debugFragment.changeBtnFunction("btn2", btn2Lambda)
        _btn2.postValue("get locations")

        val btn3Lambda = {

            var sauce = "sacue"
            viewModelScope.launch {

                val target = applicationLevelProvider.webUser
                if (target!=null) {
                    with(target) {
                        email = publicStringHolder1
                        first_name = publicStringHolder2
                        last_name = publicStringHolder3
                        cell_number = publicStringHolder4
                    }
                    val result = userWebBEController.updateUserObject(
                            target
                    )

                    when (result) {
                        is SuccessFailWrapper.Success -> Timber.i("success")
                        is SuccessFailWrapper.Fail -> Timber.i(result.message, result.javaClass)
                        is SuccessFailWrapper.Throwable -> Timber.i("throwable")
                        else -> Timber.i("else")


                    }
                    _text.postValue("$TAG registerNewUserAccount = ${result}")
                }
                else {
                    _text.postValue("web user is null")
                }



            }
            sauce += sauce

        }


        debugFragment.changeBtnFunction("btn3", btn3Lambda)
        _btn3.postValue("null")

        val btn4Lambda = {
            val job = viewModelScope.launch {
            }

        }
        debugFragment.changeBtnFunction("btn4", btn4Lambda)
        _btn4.postValue("null")

        val btn5Lambda = {


        }
        debugFragment.changeBtnFunction("btn5", btn5Lambda)
        _btn5.postValue("null")

    }
    fun setUpWebBEAuthTesting() {
        debugFragment.changeFocusedSetupgButton("webAuth")

        val btn1Lambda = {
            var sauce = "sacue"
            viewModelScope.launch {

             val result = userWebBEController.signin()
                when (result) {
                    is SuccessFailWrapper.Success -> Timber.i("success")
                    is SuccessFailWrapper.Fail -> Timber.i("fail")
                    is SuccessFailWrapper.Throwable -> Timber.i("throwable")
                    else -> Timber.i("else")


                }
                _text.postValue("$TAG registerNewUserAccount = ${result}")
            }
            sauce += sauce


        }
        _tv1.postValue("email")
        _tv2.postValue("first name")
        _tv3.postValue("last name")
        _tv4.postValue("phone number")
        _et1.postValue("email")
        _et2.postValue("first name")
        _et3.postValue("last name")
        _et4.postValue("#######")

        debugFragment.changeBtnFunction("btn1", btn1Lambda)
        _btn1.postValue("signin web be")

        val btn2Lambda = {
            var sauce = "sacue"
            viewModelScope.launch {

                val result = userWebBEController.register(
                        firstName =  publicStringHolder2,
                        lastName = publicStringHolder3
                )

                when (result) {
                    is SuccessFailWrapper.Success -> Timber.i("success")
                    is SuccessFailWrapper.Fail -> Timber.i("fail")
                    is SuccessFailWrapper.Throwable -> Timber.i("throwable")
                    else -> Timber.i("else")


                }
                _text.postValue("$TAG registerNewUserAccount = ${result}")
            }
            sauce += sauce

        }
        debugFragment.changeBtnFunction("btn2", btn2Lambda)
        _btn2.postValue("register")

        val btn3Lambda = {

            var sauce = "sacue"
            viewModelScope.launch {

               val target = applicationLevelProvider.webUser
            if (target!=null) {
                with(target) {
                    email = publicStringHolder1
                    first_name = publicStringHolder2
                    last_name = publicStringHolder3
                    cell_number = publicStringHolder4
                }
                val result = userWebBEController.updateUserObject(
                        target
                )

                when (result) {
                    is SuccessFailWrapper.Success -> Timber.i("success")
                    is SuccessFailWrapper.Fail -> Timber.i(result.message, result.javaClass)
                    is SuccessFailWrapper.Throwable -> Timber.i("throwable")
                    else -> Timber.i("else")


                }
                _text.postValue("$TAG registerNewUserAccount = ${result}")
            }
                else {
                _text.postValue("web user is null")
            }



            }
            sauce += sauce

        }


        debugFragment.changeBtnFunction("btn3", btn3Lambda)
        _btn3.postValue("update user")

        val btn4Lambda = {
            val job = viewModelScope.launch {
                      }

        }
        debugFragment.changeBtnFunction("btn4", btn4Lambda)
        _btn4.postValue("null")

        val btn5Lambda = {


        }
        debugFragment.changeBtnFunction("btn5", btn5Lambda)
        _btn5.postValue("null")

    }
    fun setUpAuthTesting() {
        debugFragment.changeFocusedSetupgButton("auth")
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

                Timber.i("$TAG, Success ${result.toString()} \n result email: ${result} \n applevel email: ${applicationLevelProvider.firebaseUser?.email}")
            }

        }
        debugFragment.changeBtnFunction("btn4", btn4Lambda)
        _btn4.postValue("register alt coroutine")

        val btn5Lambda = {
            /*         val job = viewModelScope.launch {
                         val randNumber =Math.random().toString()*/
            if (firebaseAuth.currentUser!=null) {
                val result = firebaseAuthImpl.signoutUser()

                Timber.i("$TAG, Success signed out: ${result}\n fb user:${firebaseAuth.currentUser}\n appplicationlevel user: ${applicationLevelProvider.firebaseUser}")

            }
            else {
                Timber.i("$TAG, didn't run sign out\n fb user:${firebaseAuth.currentUser}\n appplicationlevel user: ${applicationLevelProvider.firebaseUser}")
            }


        }
        debugFragment.changeBtnFunction("btn5", btn5Lambda)
        _btn5.postValue("sign out")


    }
}