package com.example.wildfire_fixed_imports.model

import com.google.gson.annotations.SerializedName
import com.mapbox.mapboxsdk.geometry.LatLng

data class WebMessage(
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
        //can't change primary keys (or we shouldn't anyway...)
        //  val UID: String,
      //  var id: Int,
        var first_name: String,
        var last_name: String,
        var email: String,

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

    fun latDouble():Double {
        return location[1]
    }
    fun lngDouble():Double {
        return location[0]
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


data class WebBELocationSubmit(
        val address: String,
        val radius: Int
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
        val radius: Double,
        val user_id: Int

) {

    val latLng: LatLng = LatLng(this.latitude,this.longitude)

    fun toSafeWebBELocation() :SafeWebBELocation {
        return SafeWebBELocation(this.address,this.address_label,this.last_alert,this.latitude,this.longitude,this.notification_timer,
                this.notifications,this.radius)
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
            val radius: Double
            // val user_id: Int
    )
}



data class requestAQI(
        val latitude: Double,
        val lng:Double,
        val distance: Int

)



data class DSStationsResponse(
        val `data`: List<AQIStations>,
        val status: String
) {}


data class AQIStations(
        val aqi: String,
        val lat: Double,
        val lon: Double,
        val station: Name,
        val uid: Int
) {

    fun getLatLng():LatLng {
        return LatLng(this.lat,this.lon)
    }
    data class Name(
            val name: String,
            val time: String
    )

}


data class AQIdata(
        @SerializedName("aqi")
        val aqi: Int?,
        @SerializedName("co")
        val co:Co? =null,
        @SerializedName("dew")
        val dew: Dew? =null,
        @SerializedName("h")
        val h: H? =null,
        @SerializedName("no2")
        val no2: No2? =null,
        @SerializedName("o3")
        val o3: O3? =null,
        @SerializedName("p")
        val p: P? =null,
        @SerializedName("pm10")
        val pm10: Pm10? =null,
        @SerializedName("pm25")
        val pm25: Pm25? =null,
        @SerializedName("r")
        val r: R? =null,
        @SerializedName("so2")
        val so2: So2? =null,
        @SerializedName("t")
        val t: T? =null,
        @SerializedName("w")
        val w: W? =null,
        @SerializedName("wg")
        val wg: Wg? =null
) {
    data class Co(
            @SerializedName("v")
            val v: Double?
    )
    data class Dew(
            @SerializedName("v")
            val v: Double?
    )
    data class H(
            @SerializedName("v")
            val v: Double?
    )
    data class No2(
            @SerializedName("v")
            val v: Double?
    )
    data class O3(
            @SerializedName("v")
            val v: Double?
    )
    data class P(
            @SerializedName("v")
            val v: Double?
    )
    data class Pm10(
            @SerializedName("v")
            val v: Double?
    )
    data class Pm25(
            @SerializedName("v")
            val v: Double?
    )
    data class R(
            @SerializedName("v")
            val v: Double?
    )
    data class So2(
            @SerializedName("v")
            val v: Double?
    )
    data class T(
            @SerializedName("v")
            val v: Double?
    )
    data class W(
            @SerializedName("v")
            val v: Double?
    )
    data class Wg(
            @SerializedName("v")
            val v: Double?
    )
    fun co(): Double? { return this.co?.v }
    fun dew(): Double? { return this.dew?.v }
    fun h():Double? {return this.h?.v}
    fun no2(): Double? { return this.no2?.v }
    fun o3(): Double? { return this.o3?.v }
    fun p(): Double? { return this.p?.v }
    fun pm10(): Double? { return this.pm10?.v }
    fun pm25(): Double? { return this.pm25?.v }
    fun r(): Double? { return this.r?.v }
    fun so2(): Double? { return this.so2?.v }
    fun t(): Double? { return this.t?.v }
    fun wg(): Double? { return this.wg?.v }
    fun w(): Double? { return this.w?.v }

}

data class DSRRSSFireContainer(
        @SerializedName("nearby_fires")
        val nearbyFires: List<DSRSSFire>,
        @SerializedName("other_fires")
        val otherFires: List<DSRSSFire>
) {
    data class DSRSSFire(
            @SerializedName("location")
            val location: List<Double>,
            @SerializedName("name")
            val name: String
    )
}

data class DSRSSFireSubmit(
        @SerializedName("position")
        // lat, lng
        val position: List<Double>,
        @SerializedName("radius")
        val radius: Int
)


