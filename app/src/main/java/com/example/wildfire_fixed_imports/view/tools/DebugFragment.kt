package com.example.wildfire_fixed_imports.view.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.DebugViewModel

class DebugFragment : Fragment() {

    private lateinit var debugViewModel: DebugViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        debugViewModel =
            ViewModelProviders.of(this).get(DebugViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_debug, container, false)
        val textView: TextView = root.findViewById(R.id.text_tools)
        debugViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}