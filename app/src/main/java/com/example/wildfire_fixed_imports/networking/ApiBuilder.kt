package com.example.wildfire_fixed_imports.networking

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import com.example.wildfire_fixed_imports.model.User
import com.example.wildfire_fixed_imports.model.UserLogin
import com.example.wildfire_fixed_imports.model.UserResponse

interface ApiBuilder {
/*@GET("api/users")

fun getAllUsers(): Call<List<User>>*/

    //Auth Routes
    @POST("/api/auth/register")
    fun createUser(@Body user: User): Call<UserResponse>

    @POST("/api/auth/login")
    fun login(@Body user: UserLogin): Call<UserResponse>

    //User Routes
    @GET("/api/users/session")

    @PUT("/api/users/")

    @DELETE	("/api/users/")

    @PUT ("/api/users/update/:id")

    @GET( "/api/users/ip-address")


    //Location Routes

    @GET ("/api/locations/")

    @POST("/api/locations/")

    @PUT( "/api/locations/:id")

    @DELETE	("/api/locations/:id")


companion object {
    const val BASE_URL = "https://wildfire-watch.herokuapp.com/"

    fun create(): ApiBuilder {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC
        logger.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logger)
            .retryOnConnectionFailure(false)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiBuilder::class.java)
    }
}
  
}