package com.example.wildfire_fixed_imports.view.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.SaveData
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.LoginViewModel
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.SettingsViewModel

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var loginViewModel: LoginViewModel
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    lateinit var switchCompat: SwitchCompat
    private lateinit var saveData: SaveData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Darkmode savedata
        saveData = SaveData(this.context!!)
        applicationLevelProvider.saveData = saveData


        applicationLevelProvider.bottomSheet?.visibility = View.INVISIBLE
        applicationLevelProvider.aqiGaugeExpanded.visibility = View.INVISIBLE
        applicationLevelProvider.drawerToggle.drawerArrowDrawable.setColor(Color.BLACK)


        settingsViewModel =
            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        switchCompat = view.findViewById<SwitchCompat>(R.id.settings_switch_darkmode)

        //Darkmode switch for on off

        if (saveData.loadDarkModeState() == true){
            switchCompat!!.isChecked = true
        }

        //Darkmode On click On Off
        switchCompat!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveData.setDarkModeState(true)

            }else {
                saveData.setDarkModeState(false)
            }

        }

    }

    override fun onDetach() {
        applicationLevelProvider.bottomSheet?.visibility = View.VISIBLE
        applicationLevelProvider.drawerToggle.drawerArrowDrawable.setColor(Color.WHITE)
        super.onDetach()
    }
}