package com.example.wildfire_fixed_imports.com.example.wildfire_fixed_imports

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper
import com.example.wildfire_fixed_imports.model.UID
import com.google.android.material.snackbar.Snackbar
import retrofit2.HttpException
import java.io.IOException


fun View.showSnackbar(msgId: Int, length: Int) {
    showSnackbar(context.getString(msgId), length)
}

fun View.showSnackbar(msg: String, length: Int) {
    showSnackbar(msg, length, null, {})
}

fun View.showSnackbar(
    msgId: Int,
    length: Int,
    actionMessageId: Int,
    action: (View) -> Unit
) {
    showSnackbar(context.getString(msgId), length, context.getString(actionMessageId), action)
}

fun View.showSnackbar(
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(this, msg, length)
   if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(this)
        }.show()
    }
    else {
       snackbar.show()

   }}


fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(permissionsArray: Array<String>,
                                               requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}

fun String.toUID() : UID {
    return UID(this)
}

fun <T>RetrofitErrorHandler(throwable:Throwable): SuccessFailWrapper<T> {
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