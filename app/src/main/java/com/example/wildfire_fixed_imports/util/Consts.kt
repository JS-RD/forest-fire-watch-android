package com.example.wildfire_fixed_imports.util

import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.model.WebBELocation
import okhttp3.logging.HttpLoggingInterceptor

//File provide static constants usable across the application,

const val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 666 // magic number for fine location request

const val MY_PERMISSIONS_REQUEST_INTERNET = 667 // magic number for internet permission request

const val MY_PERMISSIONS_COARSE_LOCATION = 668 // magic number for internet permission request

const val WEB_BASE_URL = "https://wildfire-watch.herokuapp.com/"
// the new backend is at https://web-wildfirewatch.herokuapp.com/ and we should be able to switch in this url
// as soon as the web folks have their stuff set up, as of 1/14 we're holding off on switching.

const val AQI_NEAREST_NEIGHBOR_SOURCE_ID = "temporary_source_id_for_manual_circles"
const val AQI_NEAREST_NEIGHBOR_LAYER_ID = "temporary_layer_id_for_manual_circles"

const val SIMPLE_HEATMAP_LAYER_ID = "heat-map-layer-uno"

const val SIMPLE_CIRCLE_LAYER_ID = "circle-layer-dos"

const val DS_BASE_URL = "https://appwildfirewatch.herokuapp.com/"

//this is the string web's backend sends back on auth errors
const val AUTH_ERROR_STRING_WEB_BE = "You shall not pass"

//this is another error string occasionally provided
const val ALT_AUTH_ERROR_STRING_WEB_BE = "provide a token"

//this is the source id for aqi
const val AQI_SOURCE_ID = "aqiID"

//shared prefs keys
const val KEY_THEME = "prefs.theme"
const val KEY_DEFAULT_RADIUS = "prefs.default_radius"


//text layer aqi
const val AQI_BASE_TEXT_LAYER = "aqi-text-layer"

//bottom heatmap lite aqi circles
const val AQI_HEATLITE_BASE_LAYER="heatmap_lite_base"

//bottom heatmap lite aqi circles
const val AQI_HEATLITE_CLUSTER_LAYER="heatmap_lite_cluster"
//aqi count layer
const val AQI_CLUSTERED_COUNT_LAYER = "count"


//CIRCLE LAYERS AQI
val AQI_CIRCLE_LAYERS = arrayListOf("cluster-0", "cluster-1", "cluster-2")
//CIRCLE heatmaplite LAYERS AQI
val AQI_HML_CIRCLE_LAYERS = arrayListOf("cluster-hml-0", "cluster-hml-1", "cluster-hml-2")


const val aqiCloudIcon = "cloudicon"
//source id for fire
const val FIRE_SOURCE_ID = "fireID"

//fire layers
const val FIRE_SYMBOL_LAYER = "fire-symbols"


const val fireIconTarget = "fire_icon_50"

const val crossIconTarget = "cross-icon-id"


//DarkMode
const val PREFS_NAME = "theme_prefs"
const val THEME_UNDEFINED = -1
const val THEME_LIGHT = 0
const val THEME_DARK = 1
const val THEME_SYSTEM = 2
const val THEME_BATTERY = 3

//http logging level

val LOGGING_LEVEL = HttpLoggingInterceptor.Level.BASIC

val methodName
    get() = StackTraceInfo.invokingMethodName

val className
    get() = StackTraceInfo.invokingClassName

val fileName
    get() = StackTraceInfo.invokingFileName

val DEFAULT_WEBBELOCATION = WebBELocation(
        address = "",
        address_label = "",
        id = 100000,
        last_alert = 0L,
        latitude = 18.0,
        longitude = 26.0,
        notifications = true,
        notification_timer = 0,
        radius = 5.0,
        user_id = ApplicationLevelProvider.getApplicaationLevelProviderInstance().localUser?.mWebBEUser?.id ?: 10000
)


//Thread.currentThread().stackTrace[2].methodName
object MethodNameTest {
    private var CLIENT_CODE_STACK_INDEX = 0
    @JvmStatic
    fun main(args: Array<String>) {
        println("methodName() = " + methodName())
        println("CLIENT_CODE_STACK_INDEX = $CLIENT_CODE_STACK_INDEX")
    }

    fun methodName(): String {
        return Thread.currentThread().stackTrace[CLIENT_CODE_STACK_INDEX].methodName
    }

    init { // Finds out the index of "this code" in the returned stack trace - funny but it differs in JDK 1.5 and 1.6
        var i = 0
        for (ste in Thread.currentThread().stackTrace) {
            i++
            if (ste.className == MethodNameTest::class.java.name) {
                break
            }
        }
        CLIENT_CODE_STACK_INDEX = i
    }
}

/* Utility class: Getting the name of the current executing method
 * https://stackoverflow.com/questions/442747/getting-the-name-of-the-current-executing-method
 *
 * Provides:
 *
 *      getCurrentClassName()
 *      getCurrentMethodName()
 *      getCurrentFileName()
 *
 *      getInvokingClassName()
 *      getInvokingMethodName()
 *      getInvokingFileName()
 *
 * Nb. Using StackTrace's to get this info is expensive. There are more optimised ways to obtain
 * method names. See other stackoverflow posts eg. https://stackoverflow.com/questions/421280/in-java-how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection/2924426#2924426
 *
 * 29/09/2012 (lem) - added methods to return (1) fully qualified names and (2) invoking class/method names
 */

object StackTraceInfo {
    /* (Lifted from virgo47's stackoverflow answer) */
    private var CLIENT_CODE_STACK_INDEX = 0

    // making additional overloaded method call requires +1 offset
    val currentMethodName: String
        get() = getCurrentMethodName(
            1
        ) // making additional overloaded method call requires +1 offset

    private fun getCurrentMethodName(offset: Int): String {
        return Thread.currentThread().stackTrace[CLIENT_CODE_STACK_INDEX + offset].methodName
    }

    // making additional overloaded method call requires +1 offset
    val currentClassName: String
        get() = getCurrentClassName(
            1
        ) // making additional overloaded method call requires +1 offset

    private fun getCurrentClassName(offset: Int): String {
        return Thread.currentThread().stackTrace[CLIENT_CODE_STACK_INDEX + offset].className
    }

    // making additional overloaded method call requires +1 offset
    val currentFileName: String
        get() = getCurrentFileName(
            1
        ) // making additional overloaded method call requires +1 offset

    private fun getCurrentFileName(offset: Int): String {
        val filename = Thread.currentThread().stackTrace[CLIENT_CODE_STACK_INDEX + offset].fileName
        val lineNumber = Thread.currentThread().stackTrace[CLIENT_CODE_STACK_INDEX + offset].lineNumber
        return "$filename:$lineNumber"
    }

    val invokingMethodName: String
        get() = getInvokingMethodName(
            2
        )

    private fun getInvokingMethodName(offset: Int): String {
        return getCurrentMethodName(
            offset + 1
        ) // re-uses getCurrentMethodName() with desired index
    }

    val invokingClassName: String
        get() = getInvokingClassName(
            2
        )

    private fun getInvokingClassName(offset: Int): String {
        return getCurrentClassName(
            offset + 1
        ) // re-uses getCurrentClassName() with desired index
    }

    val invokingFileName: String
        get() = getInvokingFileName(
            2
        )

    private fun getInvokingFileName(offset: Int): String {
        return getCurrentFileName(
            offset + 1
        ) // re-uses getCurrentFileName() with desired index
    }

    val currentMethodNameFqn: String
        get() = getCurrentMethodNameFqn(
            1
        )

    private fun getCurrentMethodNameFqn(offset: Int): String {
        val currentClassName =
            getCurrentClassName(
                offset + 1
            )
        val currentMethodName =
            getCurrentMethodName(
                offset + 1
            )
        return "$currentClassName.$currentMethodName"
    }

    val currentFileNameFqn: String
        get() {
            val CurrentMethodNameFqn =
                getCurrentMethodNameFqn(
                    1
                )
            val currentFileName =
                getCurrentFileName(
                    1
                )
            return "$CurrentMethodNameFqn($currentFileName)"
        }

    val invokingMethodNameFqn: String
        get() = getInvokingMethodNameFqn(
            2
        )

    private fun getInvokingMethodNameFqn(offset: Int): String {
        val invokingClassName =
            getInvokingClassName(
                offset + 1
            )
        val invokingMethodName =
            getInvokingMethodName(
                offset + 1
            )
        return "$invokingClassName.$invokingMethodName"
    }

    val invokingFileNameFqn: String
        get() {
            val invokingMethodNameFqn =
                getInvokingMethodNameFqn(
                    2
                )
            val invokingFileName =
                getInvokingFileName(
                    2
                )
            return "$invokingMethodNameFqn($invokingFileName)"
        }

    init { // Finds out the index of "this code" in the returned stack trace - funny but it differs in JDK 1.5 and 1.6
        var i = 0
        for (ste in Thread.currentThread().stackTrace) {
            i++
            if (ste.className == StackTraceInfo::class.java.name) {
                break
            }
        }
        CLIENT_CODE_STACK_INDEX = i
    }
}