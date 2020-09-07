package com.noomit.radioalarm02

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val viewModel =
//            ViewModelProvider(this, ViewModelFactory(RadioBrowserService())).get(
//                RadioBrowserViewModel::class.java
//            )

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = host.navController
    }
}