package com.example.wildfire_fixed_imports.model.networking

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

//ensures that there is an active network connection
class NetworkConnectionInterceptor (context: Context) : Interceptor{
    private val applicationContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isInternetAvailable())
            throw NoInternetException("Make sure you have an active data connection")
        return chain.proceed(chain.request())
    }

    private fun isInternetAvailable(): Boolean{
        var result = false
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when{
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }


        }

        return result
    }
}

class ApiException(message: String) : IOException(message)
class NoInternetException(message: String) : IOException(message)