package com.example.wildfire_fixed_imports.model

import com.mapbox.mapboxsdk.geometry.LatLng

data class ErrorClass(
        val error:String? = null,
        val message: String? = null
)

data class WebBEUser(
        var id: Int?=null,
        var first_name: String,
        var last_name: String,
        var email: String,
        val UID: String,
        var cell_number: String? =null,
        var receive_sms: Boolean =false,
        var receive_push: Boolean = false,
        var token: String? =null,
        //for error handling
        val error:String? =null,
        val message: String? =null
)
{
    fun makeSafeUpdate() :SafeWebUser {
        return SafeWebUser(this.first_name,this.last_name,this.email,this.cell_number,this.receive_sms,this.receive_push)
    }
    fun toWebBEUserRegister() :WebBEUserRegister {
        return WebBEUserRegister(this.first_name,this.last_name,this.email,this.UID)
    }
}

data class WebBEUserRegister(
        var first_name: String,
        var last_name: String,
        var email: String,
        val UID: String,

        //for error handling
        val error:String? =null,
        val message: String? =null
)
{

}

data class SafeWebUser(

      //  var id: Int,
        var first_name: String,
        var last_name: String,
        var email: String,
      //  val UID: String,
        var cell_number: String? =null,
        var receive_sms: Boolean,
        var receive_push: Boolean,

        //for error handling
        val error:String? = null,
        val message: String? = null
)


data class UserLogin(
    val username: String,
    val password: String
)

data class WebBELoginResponse(
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

data class WebBELocation(
        val address: String,
        val address_label: String,
        val id: Int,
        val last_alert: Long,
        val latitude: Double,
        val longitude: Double,
        val notification_timer: Int,
        val notifications: Boolean,
        val radius: Int,
        val user_id: Int
) {
    fun toSafeWebBELocation() :SafeWebBELocation {
        return SafeWebBELocation(this.address,this.address_label,this.last_alert,this.latitude,this.longitude,this.notification_timer,
                this.notifications,this.radius)
    }
}


data class SafeWebBELocation(
        val address: String,
        val address_label: String,
       // val id: Int,
        val last_alert: Long,
        val latitude: Double,
        val longitude: Double,
        val notification_timer: Int,
        val notifications: Boolean,
        val radius: Int
       // val user_id: Int
)

sealed class SuccessFailWrapper<out T>  {
    data class Success<out T>(val message: String? = null, val value: T? = null): SuccessFailWrapper<T>()
    data class Fail<out T>(val message: String? = null) : SuccessFailWrapper<T>()
    data class Throwable<out T>(val message: String? = null, val t: kotlin.Throwable? = null) : SuccessFailWrapper<T>()
    data class Exception<out T>(val message: String? = null,val e:java.lang.Exception? = null) : SuccessFailWrapper<T>()
        object NetworkError: SuccessFailWrapper<Nothing>()
    }