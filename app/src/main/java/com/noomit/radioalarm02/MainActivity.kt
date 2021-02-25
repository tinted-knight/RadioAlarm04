package com.noomit.radioalarm02

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.noomit.radioalarm02.service.PlayerService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var playerBroadcastReceiver: BroadcastReceiver? = null

    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = host.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        setWindowsTransparency()
    }

    override fun onResume() {
        super.onResume()

        application.startService(PlayerService.intent(this))

        playerBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    PlayerService.BR_ACTION_ERROR -> {
                        val codeError = intent.getIntExtra(PlayerService.BR_MEDIA_UNAVAILABLE, -1)
                        if (codeError == PlayerService.BR_CODE_ERROR) toast(getString(R.string.toast_cannot_connect_to_station))
                    }
                    PlayerService.BR_ACTION_STATE -> {
                        isPlaying = intent.getBooleanExtra(PlayerService.BR_MEDIA_IS_PLAYING, false)
                    }
                }
            }
        }
        registerReceiver(playerBroadcastReceiver, IntentFilter(PlayerService.BR_ACTION_ERROR))
        registerReceiver(playerBroadcastReceiver, IntentFilter(PlayerService.BR_ACTION_STATE))
    }

    override fun onPause() {
        unregisterReceiver(playerBroadcastReceiver)
        if (!isPlaying) stopService(PlayerService.intent(this))
        playerBroadcastReceiver = null
        super.onPause()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    // #todo doesn't do what it says :))
    //  need to be fixed
    private fun setWindowsTransparency() {
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        window.decorView.apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                window.statusBarColor = Color.TRANSPARENT
                return
            }
            if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                when (nightMode) {
                    Configuration.UI_MODE_NIGHT_NO -> systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    Configuration.UI_MODE_NIGHT_YES -> window.statusBarColor =
                        resources.getColor(R.color.clStausBarBackground, null)
                }
                return
            }
        }
    }
}
