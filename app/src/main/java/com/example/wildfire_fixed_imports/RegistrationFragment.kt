package com.example.wildfire_fixed_imports

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wildfire_fixed_imports.model.RetroController
import com.example.wildfire_fixed_imports.model.User
import com.example.wildfire_fixed_imports.model.UserResponse
import com.example.wildfire_fixed_imports.networking.RetrofitImplementation
import io.reactivex.internal.util.NotificationLite.isComplete
import kotlinx.coroutines.*
import retrofit2.Call


class RegistrationFragment : Fragment() {
    var call = Job()
    lateinit var email: String
    lateinit var password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

/*    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }*/


    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

/*

    fun createUser(){

        val newjob = CoroutineScope(Dispatchers.IO).launch {
           // val newuser = User()
            val response = RetrofitImplementation.createWEB().createUser(newuser)
            response.token

        }
        call= newjob as CompletableJob
    }
*/

    fun userCreated(response:UserResponse) {
        //do you stuff with the created user resposnse like save the token or whatever

    }
    override fun onDetach() {
        super.onDetach()

    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }


}
