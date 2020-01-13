package com.example.wildfire_fixed_imports.model

import com.mapbox.mapboxsdk.geometry.LatLng
import java.util.*

data class UserWebBE(
        var id: Int,
        var first_name: String,
        var last_name: String,
        var email: String,
        val UID: String,
        var cell_number: Int,
        var recieve_sms: Boolean,
        var recieve_push: Boolean,

        //for error handling
        val error:String?,
        val message: String?
)
{
    fun makeSafeUpdate() :UserWebSafeUpdate {
        return UserWebSafeUpdate(this.first_name,this.last_name,this.email,this.cell_number,this.recieve_sms,this.recieve_push)
    }
}


data class UserWebSafeUpdate(

      //  var id: Int,
        var first_name: String,
        var last_name: String,
        var email: String,
      //  val UID: String,
        var cell_number: Int,
        var recieve_sms: Boolean,
        var recieve_push: Boolean,

        //for error handling
        val error:String? = null,
        val message: String? = null
)


data class UserLogin(
    val username: String,
    val password: String
)

data class LoginResponse(
    val message: String?,
    val token: String,
    //should only populate if error is returned
    val error:String?
)

data class UID(
        val UID: String
)

data class FireLocations(
    var latitude: Float,
    var longitued: Float,
    var address: String,
    var address_label: String,
    var radius: Int,
    var last_alert: Int,
    var notification_timer: Int,
    var notifications: Boolean
) {
    fun getLatLng(): LatLng {
        return LatLng(latitude.toDouble(), longitued.toDouble())
    }
}

data class DSFires(
    val location: List<Double> = listOf<Double>(0.0,0.0),
    val name: String = "",
    val type: String = ""
)
{
    fun latlng() :LatLng {
        return LatLng(location[1],location[0])

    }
}

data class BackendNotifications(
    var type:String,
    var subscription: String
)

data class LoggedInUser(
    val userId: String,
    val displayName: String
)

data class dataFromIP(
        val `as`: String,
        val city: String,
        val country: String,
        val countryCode: String,
        val isp: String,
        val lat: Double,
        val lon: Double,
        val org: String,
        val query: String,
        val region: String,
        val regionName: String,
        val status: String,
        val timezone: String,
        val zip: String
)