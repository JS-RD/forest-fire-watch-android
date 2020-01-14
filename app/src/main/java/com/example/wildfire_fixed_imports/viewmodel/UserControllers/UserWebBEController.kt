package com.example.wildfire_fixed_imports.viewmodel.UserControllers

import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.*
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class UserWebBEController () {

    private  val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private val retroImpl = applicationLevelProvider.retrofitWebService

    private  var firebaseUser = applicationLevelProvider.firebaseUser

    private var webBEUser = applicationLevelProvider.webUser

    private val TAG = "UserWebBEController"

    suspend fun getUserObject(token:String):SuccessFailWrapper<WebBEUser> {

        try {
            Timber.i("$TAG try triggered")
            val userObjectCompleteFromBackend = retroImpl.getUserInfoFromBE(token)

            applicationLevelProvider.webUser=userObjectCompleteFromBackend
            Timber.i("$TAG success\n tokenstore = $token \n returned user = $userObjectCompleteFromBackend \n APL user = ${applicationLevelProvider.webUser}" )
            return SuccessFailWrapper.Success("Success",userObjectCompleteFromBackend)
        }
        catch (throwable: Throwable) {
            Timber.i("$TAG catch triggered in getUSerOBject()")
            Timber.i("$TAG does webuser exist? ${applicationLevelProvider.webUser.toString()}")
            when (throwable) {
                is IOException -> return SuccessFailWrapper.Throwable("IO Exception error",throwable)
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.Throwable(" HTTP EXCEPTION \n code: $code \n throwable: $errorResponse", throwable)
                }
                else -> {
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.Fail("unknown error \n" +
                            " throwable: $errorResponse")
                }
            }
        }
    }

    suspend fun register(firstName:String,lastName:String,email:String): SuccessFailWrapper<WebBELoginResponse>? {
        firebaseUser= applicationLevelProvider.firebaseUser
        //if firebase isn't logged in, fail
        if (firebaseUser==null) {
            Timber.i("$TAG fire base user null")
            return SuccessFailWrapper.Fail("user is not logged in to firebase")
        }
        try {
            Timber.i("$TAG try triggered")

            val userToCreate = WebBEUser(first_name = firstName,
                    last_name = lastName,
                    email = email,
                    UID = firebaseUser!!.uid)

            //create the new user object on the web backend
            val result = retroImpl.createUser(userToCreate.toWebBEUserRegister())

            //if we don't fall over to catch here, we are successful and can finish out
            when (val fullWebBEUser = getUserObject(result.token)) {
                is SuccessFailWrapper.Success -> applicationLevelProvider.webUser = fullWebBEUser.value.also {
                    Timber.i("$TAG full web from BE $fullWebBEUser")
                }
                else -> {
                    Timber.i("$TAG something went wrong when getting user object from backend ${fullWebBEUser.toString()}")
                }
            }

            //token is provided when registering and when logging is so much be added to our local model of the web
            //backend user object manually from the result of the register call
            applicationLevelProvider.webUser?.token = result.token

            Timber.i("$TAG user registered and created ${applicationLevelProvider.webUser}")


            return SuccessFailWrapper.Success("$result", result)
        }
        catch (throwable: Throwable) {
            Timber.i("$TAG catch triggered")
            Timber.i("$TAG does webuser exist ${applicationLevelProvider.webUser.toString()}")
            when (throwable) {
                is IOException -> return SuccessFailWrapper.Throwable("IO Exception error",throwable)
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.Throwable(" HTTP EXCEPTION \n code: $code \n throwable: $errorResponse", throwable)
                }
                else -> {
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.Fail("unknown error \n" +
                            " throwable: $errorResponse")
                }
            }
        }

    }
    suspend fun signin(): SuccessFailWrapper<WebBELoginResponse>? {
        firebaseUser= applicationLevelProvider.firebaseUser
        //if firebase isn't logged in, fail
        if (firebaseUser==null) {
            Timber.i("$TAG fire base user null")
            return SuccessFailWrapper.Fail("user is not logged in to firebase")
        }
        try {
            Timber.i("$TAG try triggered")
            val result = retroImpl.login(UID(firebaseUser?.uid ?: "send a bad string if somehow firebase is providing null"))

            //if we don't fall over to catch here, we are successful and can finish out

            Timber.i("$TAG result successful $result")

            val fullWebBEUser = getUserObject(result.token)
            when (fullWebBEUser) {
                is SuccessFailWrapper.Success -> applicationLevelProvider.webUser = fullWebBEUser.value.also {
                    Timber.i("$TAG full web from BE $fullWebBEUser")
                }
                else -> {
                    Timber.i("$TAG something went wrong when getting user object from backend ${fullWebBEUser.toString()}")
                }
            }

            //finally set the newly minted webUser object's token
            applicationLevelProvider.webUser?.token = result.token
            Timber.i("$TAG full user log in completed\n ${applicationLevelProvider.webUser}")

            return SuccessFailWrapper.Success("$result", result)
        }
        catch (throwable: Throwable) {
            Timber.i("$TAG catch triggered")
            when (throwable) {
                is IOException -> return SuccessFailWrapper.Throwable("IO Exception error",throwable)
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.Throwable(" HTTP EXCEPTION \n code: $code \n throwable: $errorResponse", throwable)
                }
                else -> {
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.Fail("unknown error \n" +
                            " throwable: $errorResponse")
                }
            }
        }

    }


    suspend fun updateUserObject(webBEUser: WebBEUser):SuccessFailWrapper<List<WebBEUser>> {
        if (applicationLevelProvider.webUser?.token == null) {
            Timber.i("$TAG user not logged in")
            return SuccessFailWrapper.Fail("user not logged in")
        }
        val token = applicationLevelProvider.webUser?.token

        try {
            Timber.i("$TAG try triggered")
            val result = retroImpl.updateUser(token!!,webBEUser.makeSafeUpdate())
            Timber.i("$TAG update succesful ${result[0]}")
            //if we don't fall over to catch here, we are successful and can finish out

            Timber.i("$TAG result successful ${result[0]}")
            //set the local model of webuser to the result of the update
            applicationLevelProvider.webUser = result[0]

            //finally set the newly updated webUser object's token
            applicationLevelProvider.webUser.apply {
                this?.token = token
            }

            Timber.i("$TAG user update completed\n ${applicationLevelProvider.webUser}")

            return SuccessFailWrapper.Success("$result", result)
        } catch (throwable: Throwable) {
            Timber.i("$TAG catch triggered")
            when (throwable) {
                is IOException -> return SuccessFailWrapper.Throwable("IO Exception error", throwable)
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.Throwable(" HTTP EXCEPTION \n code: $code \n throwable: $errorResponse", throwable)
                }
                else -> {
                    val errorResponse = throwable.toString()
                    return SuccessFailWrapper.Fail("unknown error \n" +
                            " throwable: $errorResponse")
                }
            }
        }
    }
    }

