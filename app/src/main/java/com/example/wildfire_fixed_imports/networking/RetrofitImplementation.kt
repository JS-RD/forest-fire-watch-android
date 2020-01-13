package com.example.wildfire_fixed_imports.networking


/*
*
* Retrofit implementation class
* will provide the actually interface and service for retrofit
*
* i.e.
* get methods
*
* and a retrofitimplementation.factory.create style object
*
* will not actually make any call, but isntead the methods for other classes to get the data
*
* */


import com.example.wildfire_fixed_imports.WEB_BASE_URL
import com.example.wildfire_fixed_imports.DS_BASE_URL
import com.example.wildfire_fixed_imports.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Deferred
import retrofit2.Response

interface RetrofitImplementation {
/*@GET("api/users")

fun getAllUsers(): Call<List<User>>*/

    //Auth Routes
    @FormUrlEncoded
    @POST("/api/auth/register")
   suspend fun userCreate(@Field("email") email: String,
                          @Field("password") password: String): Response<UserResponse>

    @POST("/api/auth/login")
   suspend fun login(@Field("email") email: String,
                     @Field("password") password: String): Response<UserResponse>

   /* //User Routes
    @GET("/api/users/session")
    suspend fun

    @PUT("/api/users/")


    @DELETE	("/api/users/")

    @PUT ("/api/users/update/:id")

    @GET( "/api/users/ip-address")*/


    //Location Routes

    @GET ("/api/locations/")

   suspend fun getLocations(): Deferred<List<FireLocations>>

   /* @POST("/api/locations/")

    @PUT( "/api/locations/:id")

    @DELETE	("/api/locations/:id")*/

    @GET ("/fpfiretype")
    suspend fun getDSFireLocations(): List<DSFires>

    companion object {


        fun createWEB(): RetrofitImplementation {
            val logger = HttpLoggingInterceptor()
      //      logger.level = HttpLoggingInterceptor.Level.BASIC
            logger.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logger)
                .retryOnConnectionFailure(false)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(WEB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RetrofitImplementation::class.java)
        }

        fun createDS(): RetrofitImplementation {
            val logger = HttpLoggingInterceptor()
            //      logger.level = HttpLoggingInterceptor.Level.BASIC
            logger.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logger)
                .retryOnConnectionFailure(false)
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(DS_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(RetrofitImplementation::class.java)
        }

    }

}