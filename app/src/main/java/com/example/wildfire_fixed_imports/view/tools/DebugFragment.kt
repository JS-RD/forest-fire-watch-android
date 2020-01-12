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
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.DebugViewModel
import kotlinx.android.synthetic.main.fragment_debug.*

class DebugFragment : Fragment() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private lateinit var debugViewModel: DebugViewModel

    private lateinit var textView: TextView
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private  lateinit var btn3: Button
    private  lateinit var btn4: Button
    private  lateinit var btn5: Button
    private lateinit var btnSetup1: Button
    private lateinit var btnSetup2: Button

    private lateinit var btnMap:Map<String,Button>

    init {
        applicationLevelProvider.debugFragment = this

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        debugViewModel =
            ViewModelProviders.of(this).get(DebugViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_debug, container, false)
        setUpUI(root)
        btnMap = mapOf(
                "btn1" to btn1,
                "btn2" to btn2,
                "btn3" to btn3,
                "btn4" to btn4,
                "btn5" to btn5,
                "btnSetup1" to btnSetup1,
                "btnSetup2" to btnSetup2
        )
        return root
    }

    private fun setUpUI(root: View) {
        textView = root.findViewById(R.id.text_tools)
        btn1 = root.findViewById(R.id.debug_btn_1)
        btn2 = root.findViewById(R.id.debug_btn_2)
        btn3 = root.findViewById(R.id.debug_btn_3)
        btn4 = root.findViewById(R.id.debug_btn_4)
        btn5 = root.findViewById(R.id.debug_btn_5)
        btnSetup1 = root.findViewById(R.id.debug_btn_setup_1)
        btnSetup2 = root.findViewById(R.id.debug_btn_setup_2)
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
        debugViewModel.btn4.observe(this, Observer {
            btn4.text = it
        })
        debugViewModel.btn5.observe(this, Observer {
            btn5.text = it
        })
        debugViewModel.btnSetup1.observe(this, Observer {
            btnSetup1.text = it
        })
        debugViewModel.btnSetup2.observe(this, Observer {
            btnSetup2.text = it
        })

        btnSetup1.setOnClickListener{
            debugViewModel.setUpAuthTesting()
        }

    }

    fun changeBtnFunction(btn:String, lambda: () -> Unit) {
        val targetButton =btnMap.get(btn)
        if (targetButton!=null) {
            targetButton.setOnClickListener{
                lambda.invoke()
            }
        }

    }
}