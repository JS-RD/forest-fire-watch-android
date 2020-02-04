package com.example.wildfire_fixed_imports.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.location.Location
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper
import com.example.wildfire_fixed_imports.model.WebBELocation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.sources.Source
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.coroutines.*
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun View.showSnackbar(msgId: Int, length: Int) {
    showSnackbar(this.context.getString(msgId), length)
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
    showSnackbar(this.context.getString(msgId), length, this.context.getString(actionMessageId), action)
}


fun View.showSnackbar(    msg: String,
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

    }
    if (length==Snackbar.LENGTH_INDEFINITE){
        CoroutineScope(Dispatchers.Main).launch {
            delay(8000)
            snackbar.dismiss()
        }
    }
}
fun ApplicationLevelProvider.showSnackbar(msgId: Int, length: Int) {
    showSnackbar(applicationContext.getString(msgId), length)
}

fun ApplicationLevelProvider.showSnackbar(msg: String, length: Int) {
    showSnackbar(msg, length, null, {})
}

fun ApplicationLevelProvider.showSnackbar(
    msgId: Int,
    length: Int,
    actionMessageId: Int,
    action: (View) -> Unit
) {
    showSnackbar(applicationContext.getString(msgId), length, applicationContext.getString(actionMessageId), action)
}

fun ApplicationLevelProvider.showSnackbar(
    msg: String,
    length: Int,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackbar = Snackbar.make(nav_view, msg, length)

   if (actionMessage != null) {
        snackbar.setAction(actionMessage) {
            action(nav_view)
        }.show()
    }
    else {
       snackbar.show()

   }
    if (length==Snackbar.LENGTH_INDEFINITE){
        CoroutineScope(Dispatchers.Main).launch {
            delay(8000)
            snackbar.dismiss()
        }
    }
}


fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(permissionsArray: Array<String>,
                                               requestCode: Int) {
    ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}


// THIS SUCKS
// this is a very silly workaround for a very silly behavior of mapbox
// it is unclear if this is somehow our fault or if it is a weakness of mapbox, however, they only way we've managed
// to successfully create a dynamic styling system is by manually deleting all of our custom layers and sources
// before we need to update them... and additionally, before we even create them the first time! Absolutely bizzare..
// regardless, this function simply need to be updated with any custom sources/layers created, and for it to run whenever the
// map box style is updated.

// i truly have to hope there is a better way to do this, but it works for now.
fun Style.flushLayersAndSources() {
    this.removeLayer(AQI_BASE_TEXT_LAYER)
    this.removeLayer(AQI_CLUSTERED_COUNT_LAYER)
    this.removeLayer(AQI_HEATLITE_BASE_LAYER)
    this.removeLayer("cluster-hml-0")
    this.removeLayer("cluster-hml-1")
    this.removeLayer("cluster-hml-2")
    this.removeLayer(FIRE_SYMBOL_LAYER)

    if (this.getSource(AQI_SOURCE_ID) !=null) {
        this.removeSource(AQI_SOURCE_ID)
    }
    if (this.getSource(FIRE_SOURCE_ID) !=null) {
        this.removeSource(FIRE_SOURCE_ID)
    }
}


fun Style.resetIconsForNewStyle() {
    val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    val id = R.drawable.ic_fireicon

    this.removeImage(crossIconTarget)
    this.removeImage(fireIconTarget)
    this.addImage(
        crossIconTarget,
            BitmapUtils.getBitmapFromDrawable(applicationLevelProvider.resources.getDrawable(R.drawable.ic_cross))!!,
            true
    )
    this.addImage(
        fireIconTarget,
            applicationLevelProvider.fireIconAlt
    )
    this.addImage(
            aqiCloudIcon,
            applicationLevelProvider.aqiIconCircle
    )


}

data class LayersAndSources(val layers:List<Layer>? =null, val sources:List<Source>?=null )
fun Style.logLayersAndSources(): LayersAndSources {

    val layers = mutableListOf<Layer>()
    val sources = mutableListOf<Source>()
    this.layers.forEach {
        print("\n")
        Timber.i(it.id)
        layers.add(it)
    }
    this.sources.forEach {
        print("\n")
        Timber.i(it.id)
        sources.add(it)
    }

    return LayersAndSources(
        layers,
        sources
    )
}


fun Location.LatLng() : LatLng {
    return LatLng(this.latitude,this.longitude)
}

fun <T>RetrofitErrorHandler(throwable:Throwable): SuccessFailWrapper<T> {
    when (throwable) {
        is IOException -> return SuccessFailWrapper.Throwable("IO Exception error", throwable)
        is HttpException -> {
            val code = throwable.code()
            val errorResponse = throwable.response().toString()
            return SuccessFailWrapper.Throwable("${throwable.message()} $errorResponse \n HTTP EXCEPTION code: $code/ " , throwable)
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
        return getBitmap(drawable)
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

fun Location.toWebBELocation(radius:Double = 5.0): WebBELocation {
    return WebBELocation(
            address = "",
            address_label = "",
            id = 100001,
            last_alert = 0L,
            latitude = this.latitude,
            longitude = this.longitude,
            notifications = true,
            notification_timer = 0,
            radius = radius,
            user_id = ApplicationLevelProvider.getApplicaationLevelProviderInstance().localUser?.mWebBEUser?.id ?: 10101
    )

}


fun ApplicationLevelProvider.zoomCameraToUser() {
    CoroutineScope(Dispatchers.Main).launch {
        val res = ApplicationLevelProvider.getApplicaationLevelProviderInstance().userLocation?.LatLng()
        //val res = ApplicationLevelProvider.getApplicaationLevelProviderInstance().localUser?.mLocations?.get(0)?.latLng
        res?.let {
            mapboxMap?.let {
                it.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        res, 8.0), 18000)
            }
        }
    }
}



object Coroutines {

    fun main(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }

    fun io(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.IO).launch {
            work()
        }

    fun default(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Default).launch {
            work()
        }
    fun unconfined(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Unconfined).launch {
            work()
        }

}



// this extension function provides a means for using coroutines to handle Tasks within gooogles Firebase framework
suspend fun <T> Task<T>.await(): T? {
    // fast path
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                throw CancellationException(
                    "Task $this was cancelled normally.")
            } else {
                result.also { Timber.i("task (${this}) complete and result is $result \n ${result.toString()} \n ") }

            }
        } else {
            throw e
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                if (isCanceled) cont.cancel() else cont.resume(result)
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}
