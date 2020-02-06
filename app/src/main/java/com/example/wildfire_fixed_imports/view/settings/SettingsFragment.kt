package com.example.wildfire_fixed_imports.view.settings

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.LoginViewModel
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.SettingsViewModel
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var loginViewModel: LoginViewModel
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    lateinit var switchCompat: SwitchCompat
    private val sharedPrefs by lazy {   this.activity!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }
    private lateinit var settingsTVSaver:TextView

  /*  var preferences =
        this.activity!!.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)*/


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        applicationLevelProvider.aqiGaugeExpanded?.visibility = View.INVISIBLE
        applicationLevelProvider.bottomSheet?.visibility = View.INVISIBLE


        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initThemeListener()
        initTheme()





    }

    private fun initThemeListener(){
        themeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.themeLight -> {setTheme(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)


                }
                R.id.themeDark -> {setTheme(AppCompatDelegate.MODE_NIGHT_YES, THEME_DARK)


                }
                R.id.themeBattery -> {setTheme(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY, THEME_BATTERY)
                    settings_textView_saver_info.visibility = View.VISIBLE

                }
                R.id.themeSystem -> {setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, THEME_SYSTEM)}
            }


        }
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        saveTheme(prefsMode)
    }

    private fun getSavedTheme() = applicationLevelProvider.localUser?.mTheme

    private fun saveTheme(theme: Int) = applicationLevelProvider.localUser?.saveTheme(theme)


    private fun initTheme() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            themeSystem.visibility = View.VISIBLE
        } else {
            themeSystem.visibility = View.GONE
        }
        when (getSavedTheme()) {
            THEME_LIGHT -> themeLight.isChecked = true
            THEME_DARK -> themeDark.isChecked = true
            THEME_SYSTEM -> themeSystem.isChecked = true
            THEME_BATTERY -> themeBattery.isChecked = true
            THEME_UNDEFINED -> {
                when (resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_NO -> themeLight.isChecked = true
                    Configuration.UI_MODE_NIGHT_YES -> themeDark.isChecked = true
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> themeLight.isChecked = true
                }
            }
        }
    }

    override fun onAttach(context: Context) {

        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }



}