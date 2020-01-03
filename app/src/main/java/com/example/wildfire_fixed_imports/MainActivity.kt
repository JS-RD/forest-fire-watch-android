package com.example.wildfire_fixed_imports

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.wildfire_fixed_imports.view.home.HomeFragment
import com.example.wildfire_fixed_imports.view.home.HomeFragment.OnFabHomePress
import com.example.wildfire_fixed_imports.viewmodel.vmclasses.HomeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), OnFabHomePress {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        homeViewModel = ViewModelProviders.of(this, ApplicationLevelProvider.viewModelFactory).get(
            HomeViewModel::class.java)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->

            homeViewModel.mapIT()
            /* val fm = supportFragmentManager
             val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
             val fragment2= navHostFragment!!.childFragmentManager.fragments[0]
              println(fragment2.toString())
                      (fragment2 as HomeFragment).mapIT()*/
           // val homeid = findNavController(R.id.nav_host_fragment).currentDestination!!.id
         /*   will give you your current Fragment's id where
*/
          //  private fun navController() = Navigation.findNavController(this, R.id.navHostFragment)
/*            this id is the id which you have given in your Navigation Graph XML file under fragment tag.
            You could also compare currentDestination.label if you wan*/

       /*   *//*  val fragment: HomeFragment =
                fm.findFragmentById(R.id.home) as HomeFragment*//*
         //   NewsItemFragment tag = ( NewsItemFragment)getFragmentManager().findFragmentByTag(MainActivity.NEWSITEM_FRAGMENT)
            val fragment2 = fm.findFragmentById(R.id.nav_host_fragment)
                val fraglist  = fm.fragments
            print(fraglist.toString())
            println("huh")
            println(fragment2)
       *//*  //   println(fragment2!!.tag)
            println(fragment2.id)

            *//*            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()*/
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_login_register, R.id.nav_settings,
                R.id.nav_debug, R.id.nav_share, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onFabPRess() {
        Toast.makeText(this,"yup",Toast.LENGTH_LONG).show()
    }
}
