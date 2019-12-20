package com.example.wildfire_fixed_imports.model

data class User (
    val first_name: String,
    val last_name: String,
    val email: String,
    val username: String,
    val password: String,
    val owner: Boolean,
    val avatar: String
)

data class UserLogin(
    val username: String,
    val password: String
)

data class UserResponse(
    val username: String,
    val id: Int,
    val owner: Boolean,
    val token: String
)