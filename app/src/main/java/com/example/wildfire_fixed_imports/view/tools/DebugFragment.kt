package com.example.wildfire_fixed_imports.view.tools

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.Crashlytics
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.DebugViewModel

class DebugFragment : Fragment() {

    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()

    private lateinit var debugViewModel: DebugViewModel

    private lateinit var textView: TextView
    private lateinit var btn1: Button
    private lateinit var btn2: Button
    private lateinit var btn3: Button
    private lateinit var btn4: Button
    private lateinit var btn5: Button
    private lateinit var tv1:TextView
    private lateinit var tv2:TextView
    private lateinit var tv3:TextView
    private lateinit var tv4:TextView
    private lateinit var et1:EditText
    private lateinit var et2:EditText
    private lateinit var et3:EditText
    private lateinit var et4:EditText
    private lateinit var btnCrash: Button
    private lateinit var btnSetup1: Button
    private lateinit var btnSetup2: Button
    private lateinit var rootLayout: View

    private lateinit var tvMap: Map<String, TextView>
    private lateinit var etMap: Map<String, EditText>
    private lateinit var btnMap: Map<String, Button>
    private lateinit var setupBtnMap: Map<String, Button>
    private var selectedModeBtn:Button? =null



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
        //map our buttons to easy to call strings
        btnMap = mapOf(
                "btn1" to btn1,
                "btn2" to btn2,
                "btn3" to btn3,
                "btn4" to btn4,
                "btn5" to btn5,
                "btncrash" to btnCrash,
                "btnSetup1" to btnSetup1,
                "btnSetup2" to btnSetup2
        )
        tvMap = mapOf(
                "tv1" to tv1,
                "tv2" to tv2,
                "tv3" to tv3,
                "tv4" to tv4
        )
        etMap = mapOf(
                "et1" to et1,
                "et2" to et2,
                "et3" to et3,
                "et4" to et4
        )
        setupBtnMap = mapOf(
                "btnSetup1" to btnSetup1,
                "btnSetup2" to btnSetup2,
                "auth" to btnSetup1,
                "webAuth" to btnSetup2
        )
        rootLayout=root
        return root
    }

    private fun setUpUI(root: View) {
        textView = root.findViewById(R.id.text_tools)
        btn1 = root.findViewById(R.id.debug_btn_1)
        btn2 = root.findViewById(R.id.debug_btn_2)
        btn3 = root.findViewById(R.id.debug_btn_3)
        btn4 = root.findViewById(R.id.debug_btn_4)
        btn5 = root.findViewById(R.id.debug_btn_5)
        tv1 = root.findViewById(R.id.debug_tv_1)
        tv2 = root.findViewById(R.id.debug_tv_2)
        tv3 = root.findViewById(R.id.debug_tv_3)
        tv4 = root.findViewById(R.id.debug_tv_4)
        et1 = root.findViewById(R.id.debug_et_1)
        et2 = root.findViewById(R.id.debug_et_2)
        et3 = root.findViewById(R.id.debug_et_3)
        et4 = root.findViewById(R.id.debug_et_4)
        btnCrash = root.findViewById(R.id.debug_btn_crash)
        btnSetup1 = root.findViewById(R.id.debug_btn_setup_1)
        btnSetup2 = root.findViewById(R.id.debug_btn_setup_2)
        et1.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                debugViewModel.publicStringHolder1 =s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
        et2.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                debugViewModel.publicStringHolder2 =s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
        et3.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                debugViewModel.publicStringHolder3 =s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })
        et4.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                debugViewModel.publicStringHolder4 =s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

        debugViewModel.text.observe(this, Observer {
            textView.text = it
        })
        debugViewModel.tv1.observe(this, Observer {
            tv1.text = it
        })
        debugViewModel.tv2.observe(this, Observer {
            tv2.text = it
        })
        debugViewModel.tv3.observe(this, Observer {
            tv3.text = it
        })
        debugViewModel.tv4.observe(this, Observer {
            tv4.text = it
        })
        debugViewModel.et1.observe(this, Observer {
            et1.hint = it
        })
        debugViewModel.et2.observe(this, Observer {
            et2.hint = it
        })
        debugViewModel.et3.observe(this, Observer {
            et3.hint = it
        })
        debugViewModel.et4.observe(this, Observer {
            et4.hint = it
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

        btnSetup1.setOnClickListener {
            debugViewModel.setUpAuthTesting()
        }
        btnSetup2.setOnClickListener {
            debugViewModel.setUpWebBEAuthTesting()

        }


        //set crash button to a default crash value

        debugViewModel.btnCrash.observe(this, Observer {
            btnCrash.text = "reset btn values"//it
        })
        btnCrash.setOnClickListener {
            debugViewModel.resetBtnDisplayValues()
          //  Crashlytics.getInstance().crash() // Force a crash
        }


    }

    fun changeFocusedSetupgButton(btn: String) {
        selectedModeBtn = setupBtnMap[btn]
        selectedModeBtn?.setBackgroundColor(Color.RED)
        for (i in setupBtnMap) {
            val current = i.value
            if (current!=selectedModeBtn) {
                current.setBackgroundColor(resources.getColor(R.color.lb_control_button_color))
            }

        }


    }
    fun changeBtnFunction(btn: String, lambda: () -> Unit) {
        btnMap[btn]?.setOnClickListener {
            lambda.invoke()
        }

    }
}