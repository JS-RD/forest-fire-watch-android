package com.example.wildfire_fixed_imports.view.bottomSheet

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.wildfire_fixed_imports.R

class GetInfoFragment : Fragment() {

    companion object {
        fun newInstance() = GetInfoFragment()
    }

    private lateinit var viewModel: GetInfoViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.get_info_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(GetInfoViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
