package com.example.wildfire_fixed_imports

import com.example.wildfire_fixed_imports.model.*
import com.example.wildfire_fixed_imports.model.networking.RetroImplForDataScienceBackEnd
import com.example.wildfire_fixed_imports.model.networking.RetrofitImplementationForWebBackend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Before
import org.junit.Test
import retrofit2.http.*

class RetrofitImplTests () {

    val webbackend = RetrofitImplementationForWebBackend.createWEB()

    val dsbackend = RetroImplForDataScienceBackEnd.createDS()

    val webBEUserRegister = WebBEUserRegister(first_name = "firstname",last_name = "last name",email = "string@string.com",message = "",UID="")

    var token = ""
    var userid = ""
    var id =""
    var safeWebBELocation = SafeWebBELocation(address = "",
            address_label = "",
            last_alert = 0L,
            latitude =00.1,
            longitude = 00.1,
            notification_timer = 0,
            notifications = false,
            radius = 3)
    var webBELocationSubmit = WebBELocationSubmit(address = "",
            radius = 3)


    @Before
    fun stuffToDoBefore() {
        //add a real token before running
        token=""
        userid=""
    }


    fun all() {
        CoroutineScope(Dispatchers.Unconfined).launch {
            webbackend.createUser(webBEUserRegister)
            webbackend.dataFromIP()
            webbackend.getUserInfoFromBE(token)
            webbackend.login(UID(userid))
           webbackend.getUserInfoFromBE(token)

            webbackend.getWebBELocations(token)
            webbackend.deleteWebBELocation(token,id)

            webbackend.updateWebBELocation(token,id, safeWebBELocation)

            webbackend.postWebBELocation(token,webBELocationSubmit)
            webbackend.deleteWebBELocation(token,id)

        }

    }
    @Test
    fun web(){}


}