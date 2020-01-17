package com.example.wildfire_fixed_imports.com.example.wildfire_fixed_imports

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.location.Location
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wildfire_fixed_imports.methodName
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper
import com.example.wildfire_fixed_imports.model.UID
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.geometry.LatLng
import retrofit2.HttpException
import timber.log.Timber
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


fun Location.LatLng() : LatLng {
    return LatLng(this.latitude,this.longitude)
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

fun getBitmap(vectorDrawable: VectorDrawable): Bitmap {
    val bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)
    val canvas =  Canvas(bitmap)
    vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    vectorDrawable.draw(canvas)
    Timber.e( "Main Activity getBitmap: 1 $methodName")
    return bitmap;
}

fun getBitmap(context: Context, drawableId:Int): Bitmap {
    Timber.e( "Main Activity getbitmap2 $methodName")
    var drawable = ContextCompat.getDrawable(context, drawableId)
    if (drawable is BitmapDrawable) {
        return BitmapFactory.decodeResource(context.getResources(), drawableId)
    } else if (drawable is VectorDrawable) {
        return getBitmap( drawable)
    } else {
        throw  IllegalArgumentException("unsupported drawable type")
    }
}
fun getBitmapFromVectorDrawable(context: Context, drawableId:Int) : Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    /*  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
          drawable = (DrawableCompat.wrap(drawable as Drawable)).mutate();
      }
  */
    val bitmap = Bitmap.createBitmap(drawable!!.getIntrinsicWidth(),
            drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    val canvas =  Canvas(bitmap);
    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    drawable.draw(canvas);

    return bitmap
}