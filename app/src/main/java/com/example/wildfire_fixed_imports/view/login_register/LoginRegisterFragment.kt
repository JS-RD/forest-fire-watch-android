package com.example.wildfire_fixed_imports.view.login_register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.LoginRegisterViewModel

class LoginRegisterFragment : Fragment() {

    private lateinit var galleryViewModel: LoginRegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(LoginRegisterViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        val textView: TextView = root.findViewById(R.id.text_login)
        galleryViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}