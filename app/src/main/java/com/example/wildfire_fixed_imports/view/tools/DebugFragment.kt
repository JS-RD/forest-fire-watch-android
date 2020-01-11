package com.example.wildfire_fixed_imports.view.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.DebugViewModel
import kotlinx.android.synthetic.main.fragment_debug.*

class DebugFragment : Fragment() {

    private lateinit var debugViewModel: DebugViewModel

    lateinit var textView: TextView
    lateinit var btn1: Button
    lateinit var btn2: Button
    lateinit var btn3: Button
    lateinit var btnSetup1: Button
    lateinit var btnSetup2: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        debugViewModel =
            ViewModelProviders.of(this).get(DebugViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_debug, container, false)
        textView= root.findViewById(R.id.text_tools)
       btn1= debug_btn_1
        btn2= debug_btn_2
        btn3= debug_btn_3
        btnSetup1= debug_btn_setup_1
        btnSetup2= debug_btn_setup_2
        debugViewModel.text.observe(this, Observer {
            textView.text = it
        })
        debugViewModel.btn1.observe(this, Observer {
            btn1.text = it
        })
        debugViewModel.btn2.observe(this, Observer {
            btn2.text = it
        })
        debugViewModel.btn3.observe(this, Observer {
            btn3.text = it
        })
        debugViewModel.bt


        return root
    }
}