package com.noomit.radioalarm02

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.base.ViewModelFactory
import com.noomit.radioalarm02.radiobrowserview.RadioBrowserViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel =
            ViewModelProvider(this, ViewModelFactory(RadioBrowserService())).get(
                RadioBrowserViewModel::class.java
            )

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = host.navController
    }
}