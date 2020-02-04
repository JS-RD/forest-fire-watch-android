package com.example.wildfire_fixed_imports.view.login_registration

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.util.*
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.LoginViewModel
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.LoginViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment(){

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private lateinit var loginViewModel: LoginViewModel
    lateinit var email: String
    lateinit var password: String
    lateinit var firstname: String
    lateinit var lastename: String
    lateinit var button_reg:Button


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)
        button_reg = view.findViewById(R.id.regfrag_btn_register)


        loginViewModel.registrationResult.observe(this,
                Observer { registrationResult ->
                    if (registrationResult.fail){
                        Toast.makeText(this.context,
                                "Thanks ${applicationLevelProvider.webUser?.first_name}! You've succesfully registered ${applicationLevelProvider.webUser?.email}!",
                                Toast.LENGTH_SHORT).show()
                        applicationLevelProvider.showSnackbar(registrationResult.message, Snackbar.LENGTH_SHORT)
                    }
                    else if (registrationResult.webBE && registrationResult.firebase){
                        applicationLevelProvider.showSnackbar(registrationResult.message, Snackbar.LENGTH_SHORT)
                        Navigation.findNavController(button_reg).navigate(R.id.nav_home)
                    }
                    else if (registrationResult.firebase) {
                        applicationLevelProvider.showSnackbar(registrationResult.message, Snackbar.LENGTH_SHORT)
                        loginViewModel.registerNewUserWeb()
                    }
                })
        button_reg.setOnClickListener {
            email = regfrag_et_EmailAddress.text.toString().trim()
            password = regfrag_et_input_password.text.toString().trim()
            firstname = regfrag_et_firstname.text.toString().trim()
            lastename = regfrag_et_lastname.text.toString().trim()


            if (!loginViewModel.isUserNameValid(email)) {
                regfrag_et_EmailAddress.error = "Email required"
                regfrag_et_EmailAddress.requestFocus()
                return@setOnClickListener
            }

            if (!loginViewModel.isPasswordValid((password))) {
                regfrag_et_input_password.error = "password required"
                regfrag_et_input_password.requestFocus()
                return@setOnClickListener
            }

            if (firstname.isEmpty()) {
                regfrag_et_firstname.error = "password required"
                regfrag_et_firstname.requestFocus()
                return@setOnClickListener
            }
            if (lastename.isEmpty()) {
                regfrag_et_lastname.error = "password required"
                regfrag_et_lastname.requestFocus()
                return@setOnClickListener
            }
            AQI_BASE_TEXT_LAYER
            AQI_CLUSTERED_COUNT_LAYER
            AQI_HEATLITE_BASE_LAYER
            "cluster-hml-0"
            "cluster-hml-1"
            "cluster-hml-2"
            FIRE_SYMBOL_LAYER
            CoroutineScope(Dispatchers.IO).launch {
                loginViewModel.registerNewUser(email,password,firstname,lastename)

            }

        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ):View?{

        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_registration, container, false)

    }





}