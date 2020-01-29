package com.example.wildfire_fixed_imports.view.bottom_sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
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
