package com.example.wildfire_fixed_imports.view.loginRegistration
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R

import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationFragment : Fragment() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    private val firebaseAuthImpl =applicationLevelProvider.firebaseAuthImpl

    lateinit var email: String
    lateinit var password: String
    val button_reg = view?.findViewById<View>(R.id.button_register) as Button



    // TODO: Rename and change types of parameters
    private var listener: OnFragmentInteractionListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        button_reg.setOnClickListener {
            email = et_EmailAddress.text.toString().trim()
            password = et_input_password.text.toString().trim()


            if(email.isEmpty()){
                et_EmailAddress.error = "Email required"
                et_EmailAddress.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                et_input_password.error = "password required"
                et_input_password.requestFocus()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
               val result = firebaseAuthImpl.registerCoroutine(email,password)

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
    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    /*fun createUser(){
        val call: UserResponse = RetrofitImplementation.createWEB().createUser(User())
    }*/
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }
}