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
import com.example.wildfire_fixed_imports.*
import com.example.wildfire_fixed_imports.model.SuccessFailWrapper
import com.example.wildfire_fixed_imports.model.UID
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.Layer
import com.mapbox.mapboxsdk.style.sources.Source
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException


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
    this.addImage(fireIconTarget,
            applicationLevelProvider.fireIconAlt
    )
}

data class LayersAndSources(val layers:List<Layer>? =null, val sources:List<Source>?=null )
fun Style.logLayersAndSources():LayersAndSources {

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

    return LayersAndSources(layers,sources)
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

 fun ApplicationLevelProvider.hideFab(){
    currentActivity.fab.hide()
}
 fun ApplicationLevelProvider.showFab(){
    currentActivity.fab.show()
}

fun ApplicationLevelProvider.zoomCameraToUser() {
    CoroutineScope(Dispatchers.Main).launch {
        val res = currentActivity.getLatestLocation()?.LatLng()
        res?.let {
            mapboxMap?.let {
                it.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        res, 8.0), 18000)
            }
        }
    }
}
