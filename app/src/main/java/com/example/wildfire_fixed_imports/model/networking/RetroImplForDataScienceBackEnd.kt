package com.example.wildfire_fixed_imports.model.networking


/*
*
*
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


import com.example.wildfire_fixed_imports.util.DS_BASE_URL
import com.example.wildfire_fixed_imports.model.*
import com.example.wildfire_fixed_imports.util.LOGGING_LEVEL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface RetroImplForDataScienceBackEnd {
    /*
   /fpfiretype
Methods: ["GET"]
Returns: [{'name': "Fire Name", 'type': "Wildfire", 'location': [lat, lon]}, ...]


/check_rss_fires
Methods: ["POST"]
Request JSON Format:
{
	"position": [lat, lon],
	"radius": int
}
Returns:
{
	'nearby_fires': [{'name': "Fire Name", 'location': [lat, lon]}, ...],
	'other_fires': [{'name': "Fire Name", 'location': [lat, lon]}, ...]
    /get_aqi_data
    Methods: ["GET"]
    Request JSON Format:
    {
        "lat": latitude value,
        "lng": longitude value
    }
    Returns:
    {  "aqi": value, co": {"v": value}, "no2": {"v": value}, "o3": {"v": value}, "p": {"v": value }, "pm10": {"v": value  },
        "pm25": {"v": value}, "so2": {"v": value}, "t": {"v": value}, "w": {"v": value}
    }

    /get_aqi_stations
    Methods: ["GET"]
    quere params:
    {
        "lat": latitude value,
        "lng": longitude value.
        "distance": distance value
    }
    i.e
    ?lat=20.0&lng=20.0&distance=.01


    distance appears to equal a degree of lat/ln so aprox 69 miles, will need a transform method
    Returns:
    {
        "data": [ { "aqi": "-", "lat": value, "lon": value,
        "station": {"name": value, "time": value  },
        "uid": value
    }],
        "status": "ok"
    }
*/
    @GET("/fpfiretype")
    suspend fun getDSFireLocations(): List<DSFires>

    @GET("/check_rss_fires")
    suspend fun getDSRSSFireLocations(@Body dsfire:DSRSSFireSubmit): List<DSRRSSFireContainer>

    @GET("/get_aqi_data")
    suspend fun getAQIData(@Query("lat") lat: Double,@Query("lng") lng: Double): AQIdata

    @GET("/get_aqi_stations")
    suspend fun getAQIStations(@Query("lat") lat: Double,@Query("lng") lng: Double,@Query("distance") distance: Double ): DSStationsResponse



    companion object {

        fun createDS(): RetroImplForDataScienceBackEnd {
            val logger = HttpLoggingInterceptor()
            //      logger.level = HttpLoggingInterceptor.Level.BASIC
            logger.level = LOGGING_LEVEL

            val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(logger)
                    .retryOnConnectionFailure(true)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .build()

            val retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(DS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            return retrofit.create(RetroImplForDataScienceBackEnd::class.java)
        }

    }

}