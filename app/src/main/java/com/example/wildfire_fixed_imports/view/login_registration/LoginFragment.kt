package com.example.wildfire_fixed_imports.view.login_registration

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.core.graphics.blue
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.wildfire_fixed_imports.ApplicationLevelProvider
import com.example.wildfire_fixed_imports.R
import com.example.wildfire_fixed_imports.util.showSnackbar
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.LoginViewModel
import com.example.wildfire_fixed_imports.viewmodel.view_model_classes.LoginViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : Fragment() {


    private lateinit var loginViewModel: LoginViewModel
    private val applicationLevelProvider = ApplicationLevelProvider.getApplicaationLevelProviderInstance()
    //private lateinit var imgAppbarAqiGauge: ImageView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {



        applicationLevelProvider.bottomSheet?.visibility = View.INVISIBLE
        applicationLevelProvider.aqiGaugeExpanded?.visibility = View.INVISIBLE


        /*
        Makes toggle disappear
        applicationLevelProvider.drawerToggle.isDrawerIndicatorEnabled = true
        applicationLevelProvider.appBarLayout.isInvisible = true*/



        return inflater.inflate(R.layout.fragment_login, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
                .get(LoginViewModel::class.java)


        val usernameEditText = view.findViewById<EditText>(R.id.says_username)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val loginButton = view.findViewById<Button>(R.id.btn_login) as Button
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.loading)
        val button_reg = view.findViewById<View>(R.id.button_register) as Button





            loginViewModel.loginFormState.observe(this,
                Observer { loginFormState ->
                    if (loginFormState == null) {
                        return@Observer
                    }
                    loginButton.isEnabled = loginFormState.isDataValid
                    loginFormState.usernameError?.let {
                        usernameEditText.error = getString(it)
                    }
                    loginFormState.passwordError?.let {
                        passwordEditText.error = getString(it)
                    }
                })

        loginViewModel.loginResult.observe(this,
                Observer { loginResult ->
               if (loginResult.fail){
                   applicationLevelProvider.showSnackbar(loginResult.message, Snackbar.LENGTH_SHORT)
               }
                    else if (loginResult.webBE && loginResult.firebase){
                   applicationLevelProvider.showSnackbar(loginResult.message, Snackbar.LENGTH_SHORT)
                   Navigation.findNavController(btn_login).navigate(R.id.nav_home)
               }
                    else if (loginResult.firebase) {
                   applicationLevelProvider.showSnackbar(loginResult.message, Snackbar.LENGTH_SHORT)
                   loginViewModel.loginWeb()
               } else {
                   applicationLevelProvider.showSnackbar(loginResult.message, Snackbar.LENGTH_SHORT)
               }
                })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString()
                )
            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString()
                )
            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
            )
        }

        button_reg.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.nav_reg)


        }







    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }


}