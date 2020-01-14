package com.example.wildfire_fixed_imports.networking

import com.example.wildfire_fixed_imports.WEB_BASE_URL
import com.example.wildfire_fixed_imports.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

// RetrofitImplementationForWebBackend provide retrofit for Web Backend
// full documentation at end of file,
interface RetrofitImplementationForWebBackend {

    //Auth Routes
    //likely not going to be used in final spec, here for completeness
    @POST("/api/auth/register")
    suspend fun createUser(@Body uid: UID): String

    @POST("/api/auth/login")
    suspend fun login(@Body userUID: UID): WebBELoginResponse

    @GET("/api/users/user")
    suspend fun getUserInfoFromBE(@Header("Authorization") token: String): WebBEUser

    @PUT("/api/users/")
    suspend fun updateUser(@Body user: SafeWebUser): WebBEUser

    @Deprecated("DO NOT USE NOT FUNCTIONAL AS OF 1/13/2020")
    @PUT ("/api/users/update/{user_id}")
    suspend fun updateUserByID(@Path("id") id:String, @Body user: SafeWebUser)

    @GET("/api/users/ip-address")
    suspend fun dataFromIP(): dataFromIP

    @GET("/api/locations/")
    suspend fun getWebBELocations(): List<WebBELocation>

    @PUT( "/api/locations/{id}")
    suspend fun updateWebBELocation(@Path("id") id:String, @Body user: SafeWebBELocation)

    @POST("/api/locations/")
    suspend fun postWebBELocation(@Body address: String,radius:Int): WebBELocation

    @DELETE	("/api/locations/:id")
    suspend fun deleteWebBELocation(@Path("id") id:String)


    companion object {
        
        fun createWEB(): RetrofitImplementationForWebBackend {
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

            return retrofit.create(RetrofitImplementationForWebBackend::class.java)
        }

    }
}

//method :  login(@Body user: UserLogin): LoginResponse
//used to get token for Web backend,
/*
          example of well composed body to send:

         {"UID":"0FZQ34k9Dxhs5u2sEUNrnkTO2Xk1"}

        example of succesful response:

 {"message": "Welcome Lisa!",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWJqZWN0IjoxMDQsImVtYWlsIjoibGlzYXRoaW5nQHRoaW5nLmNvbSIsImlhdCI6MTU3ODkyOTE2MSwiZXhwIjoxNTc5MDE1NTYxfQ.yOs3GWweqOie_WJmuhoMMieA82IhpdbbhVgbC8VVLoQ"}

           example of error

 {  "error": "User does not exist"}
*/

//method :   getUserInfoFromBE(@Header("Authorization") token: String): UserWebBE
//get the full backend user object from the web backend
/*
      example of well composed body to send:

   {"first_name": "sauce",
"last_name": "butt",
"email": "saucefanone@gmail.com",
"UID": "GrM7mwpzCqhMDVsgEoGLQMBbhJ73",
"cell_number": "555",
"receive_sms": true,
"receive_push": false}

    example of auth error:
    {"message": "You shall not pass"}
* */

//method: updateUser(@Body user: UserWebSafeUpdate): UserWebBE
//update the web backend user object
/*
       example of succesful response:

   {"id": 120,
"first_name": "sauce",
"last_name": "butt",
"email": "saucefanone@gmail.com",
"UID": "GrM7mwpzCqhMDVsgEoGLQMBbhJ73",
"cell_number": "555",
"receive_sms": true,
"receive_push": false}

    example of auth error:
    {"message": "You shall not pass"}
* */

//method updateUserByID(@Path("id") id:String, @Body user: UserWebSafeUpdate)
//update the web backend user object by
/*
    as of 1/13/2020 this does not appear functional
*/

//method: dataFromIP(): dataFromIP
/*
this endpoint provides information based on the user's ip address, potentially useful
 {"status":"success","country":"United States","countryCode":"US","region":"CA","regionName":"California","city":"North Hollywood","zip":"91602","lat":34.1448,"lon":-118.3667,"timezone":"America/Los_Angeles","isp":"Charter Communications","org":"Spectrum","as":"AS20001 Charter Communications Inc","query":"104.32.252.209"}
*/

//method: getLocations(): List<WebBELocation>
/*
gets a list of locations
@GET("/api/locations/")
success
[
    {
        "id": 157,
        "latitude": 34.1621,
        "longitude": -118.362,
        "address": "5043 Cahuenga Boulevard, North Hollywood, California 91601, United States",
        "address_label": null,
        "radius": 5,
        "last_alert": null,
        "notification_timer": 0,
        "notifications": false,
        "user_id": 127
    },
    {
        "id": 163,
        "latitude": 43.1536,
        "longitude": -93.201,
        "address": "Mason City, Iowa 50401, United States",
        "address_label": null,
        "radius": 4,
        "last_alert": null,
        "notification_timer": 0,
        "notifications": false,
        "user_id": 127
    }
]

*/
/*
updates a location
 @PUT( "/api/locations/:id")

{
"id": 100,
"latitude": 43.1165,
"longitude": -93.2706,
"address": "butt city, Iowa 50401, United States",
"address_label": null,
"radius": 4,
"last_alert": null,
"notification_timer": 0,
"notifications": false,
"user_id": 127
}

{
"message": "The location has been updated "
}
{
"message": "Error updating the location"
}


posts a new location
@POST("/api/locations/")
{"address":"Mason City, Iowa 50401, United States","radius":"4"}
success:
{
"id": 164,
"latitude": 43.1165,
"longitude": -93.2706,
"address": "Sue City, Iowa 50401, United States",
"address_label": null,
"radius": 4,
"last_alert": null,
"notification_timer": 0,
"notifications": false,
"user_id": 127
}

error on duplicate
{
"message": "This is a duplicate address"
}


 @DELETE	("/api/locations/:id")

 success:
 {
"message": "The location has been nuked"
}

error
{
"message": "This user doesn't have a location with that ID"
}

 */

