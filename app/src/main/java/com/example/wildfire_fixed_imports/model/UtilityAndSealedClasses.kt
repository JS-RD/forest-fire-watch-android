package com.example.wildfire_fixed_imports.model

import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

sealed class SuccessFailWrapper<out T>  {
    data class Success<out T>(val message: String? = null, val value: T? = null): SuccessFailWrapper<T>()
    data class Fail<out T>(val message: String? = null) : SuccessFailWrapper<T>()
    data class Throwable<out T>(val message: String? = null, val t: kotlin.Throwable? = null) : SuccessFailWrapper<T>()
    data class Exception<out T>(val message: String? = null,val e:java.lang.Exception? = null) : SuccessFailWrapper<T>()
    object NetworkError: SuccessFailWrapper<Nothing>()
}

sealed class LoadingDefinition ()  {
    data class NetworkLoad(val text:String= ""): LoadingDefinition()
    data class FireProcessing(val text:String= "") : LoadingDefinition()
    data class AQIProcessing(val text:String= "") : LoadingDefinition()
    data class UserProcessing(val text:String= "") : LoadingDefinition()
    data class Throwable(val text:String= "", val t: kotlin.Throwable? = null) : LoadingDefinition()
    data class Error(val text:String= "",val e:java.lang.Exception? = null) : LoadingDefinition()
    object NetworkError: LoadingDefinition()
}

abstract class SafeApiRequest {

    suspend fun<T: Any> apiRequest(call: suspend () -> Response<T>) : T{
        val response = call.invoke()
        if(response.isSuccessful){
            return response.body()!!
        }else{
            val error = response.errorBody()?.string()

            val message = StringBuilder()
            error?.let{
                try{
                    message.append(JSONObject(it).getString("message"))
                }catch(e: JSONException){ }
                message.append("\n")
            }
            message.append("Error Code: ${response.code()}")
            throw ApiException(message.toString())
        }
    }

}
data class ApiException(override val message: String) : IOException(message)
data class NoInternetException(override val  message: String) : IOException(message)