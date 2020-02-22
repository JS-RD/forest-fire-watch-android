package com.example.wildfire_fixed_imports.model.local_store

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.util.StackTraceInfo
import com.example.wildfire_fixed_imports.util.className
import com.example.wildfire_fixed_imports.util.fileName


/*
*
*  SharedPreferences Helper API curteosy of https://github.com/Florianisme/SharedPreferences
*
*
*
* */
class SharedPreferencesHelper {
    private val applicationLevelProvider: ApplicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    val TAG: String
        get() = "\nclass: $className -- file name: $fileName -- method: ${StackTraceInfo.invokingMethodName} \n"
    private val prefs: android.content.SharedPreferences
    private val editor: android.content.SharedPreferences.Editor
    private val filename = "preferences"
    var isLoggingEnabled = false
        private set


    constructor (context: Context) {
        prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }

    @SuppressLint("CommitPrefEdits")
    private constructor(context: Context, filename: String) {
        prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }

    fun setLogging(enabled: Boolean) {
        isLoggingEnabled = enabled
    }

    @Deprecated("HERE BE DRAGONS, you sure you want to be deleting everything with out any checks? be careful")
    fun clearAll() {
        editor.clear()
        editor.apply()
    }

    operator fun contains(key: String?): Boolean {
        return prefs.contains(key)
    }

    fun removeValue(key: String) {
        if (isLoggingEnabled) Log.d(TAG, "Removing key $key from preferences")
        editor.remove(key)
        editor.apply()
    }

    /*
        Retrieving methods
     */
    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        if (isLoggingEnabled) Log.d(TAG, "Value: " + key + " is " + prefs.getBoolean(key, defaultValue))
        return prefs.getBoolean(key, defaultValue)
    }

    fun getInteger(key: String, defaultValue: Int): Int {
        if (isLoggingEnabled) Log.d(TAG, "Value: " + key + " is " + prefs.getInt(key, defaultValue))
        return prefs.getInt(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String?): String? {
        if (isLoggingEnabled) Log.d(TAG, "Value: " + key + " is " + if (defaultValue != null) prefs.getString(key, defaultValue)!!.trim { it <= ' ' } else null)
        return if (defaultValue != null) prefs.getString(key, defaultValue)!!.trim { it <= ' ' } else null
    }

    fun getFloat(key: String, defaultValue: Float): Float {
        if (isLoggingEnabled) Log.d(TAG, "Value: " + key + " is " + prefs.getFloat(key, defaultValue))
        return prefs.getFloat(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long): Long {
        if (isLoggingEnabled) Log.d(TAG, "Value: " + key + " is " + prefs.getLong(key, defaultValue))
        return prefs.getLong(key, defaultValue)
    }

    fun getDouble(key: String, defaultValue: Double): Double {
        if (isLoggingEnabled) Log.d(TAG, "Value: " + key + " is " + java.lang.Double.longBitsToDouble(prefs.getLong(key, java.lang.Double.doubleToLongBits(defaultValue))))
        return java.lang.Double.longBitsToDouble(prefs.getLong(key, java.lang.Double.doubleToLongBits(defaultValue)))
    }

    fun getStringSet(key: String, defaultValue: Set<String?>?): Set<String>? {
        if (isLoggingEnabled) Log.d(TAG, "Value: " + key + " is " + prefs.getStringSet(key, defaultValue).toString())
        return prefs.getStringSet(key, defaultValue)
    }

    val all: Map<String, *>
        get() {
            if (isLoggingEnabled) Log.d(TAG, "Total of " + prefs.all.size + " values stored")
            return prefs.all
        }

    /*
        Saving methods
    */
    fun saveBoolean(key: String, value: Boolean) {
        if (isLoggingEnabled) Log.d(TAG, "Saving $key with value $value")
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun saveInteger(key: String, value: Int) {
        if (isLoggingEnabled) Log.d(TAG, "Saving $key with value $value")
        editor.putInt(key, value)
        editor.apply()
    }

    fun saveString(key: String, value: String?) {
        if (isLoggingEnabled) Log.d(TAG, "Saving $key with value $value")
        editor.putString(key, value?.trim { it <= ' ' })
        editor.apply()
    }

    fun saveFloat(key: String, value: Float) {
        if (isLoggingEnabled) Log.d(TAG, "Saving $key with value $value")
        editor.putFloat(key, value)
        editor.apply()
    }

    fun saveLong(key: String, value: Long) {
        if (isLoggingEnabled) Log.d(TAG, "Saving $key with value $value")
        editor.putLong(key, value)
        editor.apply()
    }

    fun saveDouble(key: String, value: Double) {
        if (isLoggingEnabled) Log.d(TAG, "Saving $key with value $value")
        editor.putLong(key, java.lang.Double.doubleToRawLongBits(value))
        editor.apply()
    }

    fun saveStringSet(key: String, set: Set<String?>) {
        if (isLoggingEnabled) Log.d(TAG, "Saving $key with value $set")
        editor.putStringSet(key, set)
        editor.apply()
    }

    companion object {
        private var mInstance: SharedPreferencesHelper? = null
        fun getInstance(context: Context): SharedPreferencesHelper? {
            if (mInstance == null) mInstance = SharedPreferencesHelper(context)
            return mInstance
        }

        fun getInstance(context: Context, filename: String): SharedPreferencesHelper? {
            if (mInstance != null && mInstance!!.filename != filename || mInstance == null) mInstance = SharedPreferencesHelper(context, filename)
            return mInstance
        }
    }
}