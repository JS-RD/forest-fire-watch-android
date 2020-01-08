package com.example.wildfire_fixed_imports.model

data class User(
    var id: UUID,
    var first_name: String,
    var last_name: String,
    var email: String,
    var cell_number: Int,
    var recieve_sms: Boolean,
    var recieve_push: Boolean
)

data class UserLogin(
    val username: String,
    val password: String
)

data class UserResponse(
    val username: String,
    val id: Int,
    val token: String
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

data class BackendNotifications(
    var type:String,
    var subscription: String
)

