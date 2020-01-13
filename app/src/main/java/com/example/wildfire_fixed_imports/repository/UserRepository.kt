package com.example.wildfire_fixed_imports.repository

import com.example.wildfire_fixed_imports.model.User
import com.example.wildfire_fixed_imports.model.UserResponse
import com.example.wildfire_fixed_imports.networking.RetrofitImplementation
import com.example.wildfire_fixed_imports.responses.SafeApiRequest

class UserRepository(
    private val retrofitImplementation:RetrofitImplementation
) : SafeApiRequest() {

    suspend fun userLogin(email: String, password: String): UserResponse {
        return apiRequest { retrofitImplementation.login(email, password) }
    }

    suspend fun userSignup(
        name: String,
        email: String,
        password: String
    ) : UserResponse {
        return apiRequest{ retrofitImplementation.userCreate(email, password)}
    }


}