package com.example.wildfire_fixed_imports

import android.content.Context
import android.content.SharedPreferences

class SaveData(context: Context) {
    private var sharedPreferences: SharedPreferences = context!!.getSharedPreferences("file", Context.MODE_PRIVATE)

    // This method will save the night mode state :True or False

    fun setDarkModeState(state: Boolean?){
        val editor: SharedPreferences.Editor? = sharedPreferences.edit()
        editor!!.putBoolean("Dark", state!!)
        editor.apply()

    }

    // This method will load the night mode state
    fun loadDarkModeState(): Boolean?{
        val state = sharedPreferences.getBoolean("Dark", false)
        return (state)
    }
}