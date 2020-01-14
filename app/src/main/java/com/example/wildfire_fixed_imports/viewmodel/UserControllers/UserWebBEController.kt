package com.example.wildfire_fixed_imports.viewmodel.UserControllers

import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.*
import retrofit2.HttpException
import java.io.IOException

class UserWebBEController () {

    val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    val retroImpl = applicationLevelProvider.retrofitWebService

    var firebaseUser = applicationLevelProvider.firebaseUser


    suspend fun signin(): SuccessFailWrapper<WebBELoginResponse>? {
        firebaseUser= applicationLevelProvider.firebaseUser
        //if firebase isn't logged in, fail
        if (firebaseUser==null) {

            return SuccessFailWrapper.FailWrapper("user is not logged in to firebase")
        }
        try {
            val result = retroImpl.login(UID(firebaseUser?.uid ?: "send a bad string if somehow firebase is providing null"))
            return SuccessFailWrapper.SuccessWrapper("$result", result)
        }
        catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> return SuccessFailWrapper.ThrowableWrapper("IO Exception error",throwable)
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.ThrowableWrapper(" HTTP EXCEPTION \n code: $code \n throwable: $errorResponse", throwable)
                }
                else -> {
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.FailWrapper("unknown error \n" +
                            " throwable: $errorResponse")
                }
            }
        }

    }

}