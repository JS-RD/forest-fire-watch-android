package com.example.wildfire_fixed_imports.viewmodel.UserControllers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/*

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T): ResultWrapper<T>()
    data class GenericError(val code: Int? = null, val error: Any? = null): ResultWrapper<Nothing>()
    object NetworkError: ResultWrapper<Nothing>()
}


suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ResultWrapper.GenericError(code, errorResponse)
                }
                else -> {
                    ResultWrapper.GenericError(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): Any? {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            val Gson= Gson()

            Gson.fromJson(it,)
        }
    } catch (exception: Exception) {
        null
    }
}


interface Repository {
    suspend fun getRedditPosts(): ResultWrapper<String>
}

class RepositoryImpl(private val service: RedditService,
                     private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : Repository {

    override suspend fun getRedditPosts(): ResultWrapper<RedditPosts> {
        return safeApiCall(dispatcher) { service.getRedditPosts().toRedditPosts() }
    }
}*/
