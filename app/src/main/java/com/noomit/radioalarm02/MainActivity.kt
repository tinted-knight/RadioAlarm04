package com.noomit.radioalarm02

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
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

        setWindowDecoration()
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
        if (!isPlaying) application.stopService(PlayerService.intent(this))
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

    private fun setWindowDecoration() {
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        window.decorView.apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                version21to23()
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                version23to26(nightMode)
                return
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                version27to29(nightMode)
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                version30plus(nightMode)
                return
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun version21to23() {
        window.statusBarColor = Color.parseColor("#40000000")
        window.decorView.apply {
            systemUiVisibility = systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun version23to26(nightMode: Int) {
        window.decorView.apply {
            when (nightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    systemUiVisibility = systemUiVisibility or
                            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                }
//                Configuration.UI_MODE_NIGHT_YES -> systemUiVisibility = systemUiVisibility xor
//                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private fun version27to29(nightMode: Int) {
        window.decorView.apply {
            when (nightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                            View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
//                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                }
            }
        }
    }

    private fun version30plus(nightMode: Int) {
        window.setDecorFitsSystemWindows(false)
        val insetController = window.insetsController ?: return
        when (nightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                insetController.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                )
                insetController.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                )
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                insetController.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
                insetController.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("key-isplaying", isPlaying)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isPlaying = savedInstanceState.getBoolean("key-isplaying", false)
    }
}
