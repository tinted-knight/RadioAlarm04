package com.noomit.radioalarm02.ui.alarm_fire

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.service.PlayerService
import com.noomit.radioalarm02.service.ServiceMediaItem
import com.noomit.radioalarm02.util.fragment.PlayerServiceFragment
import com.noomit.radioalarm02.util.fragment.collect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmFireFragment : PlayerServiceFragment<IAlarmFireLayout>() {

    private var ringtone: Ringtone? = null

    private val viewModel: DismissAlarmViewModel by activityViewModels()

    private var playerBroadcastReceiver: BroadcastReceiver? = null

    override val layout: View
        get() = AlarmFireLayout(requireContext())

    override val contour: IAlarmFireLayout
        get() = view as IAlarmFireLayout

    override val notificationCaption: String
        get() = getString(R.string.app_name)

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        stopRingtone()
        activity?.unregisterReceiver(playerBroadcastReceiver)
        playerBroadcastReceiver = null
        super.onStop()
    }

    override fun prepareView(savedState: Bundle?) {
        contour.setDay(viewModel.day)
    }

    override fun onServiceConnected() {
        if (viewModel.melodyUrl.isNullOrEmpty()) {
            playDefaultRingtone()
            return
        }
        viewModel.melodyUrl?.let {
            service?.mediaItem = ServiceMediaItem(
                url = it,
                title = viewModel.melodyName ?: it
            )
            service?.play()
            contour.setStationName(viewModel.melodyName ?: "")
        }
    }

    override fun onConnectionError() {
        playDefaultRingtone()
    }

    override fun initPlayerViews() {
        playerControlView = contour.playerControll
    }

    override fun observeViewModel() {
        collect(viewModel.time) { contour.setTime(it) }
    }

    private fun playDefaultRingtone() {
        ringtone?.let { stopRingtone() }

        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(requireContext(), ringtoneUri)
        ringtone?.play()
    }

    private fun stopRingtone() {
        ringtone?.stop()
        service?.pause()
        ringtone = null
    }

    private fun registerBroadcastReceiver() {
        playerBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val codeError = intent?.getIntExtra(PlayerService.BR_MEDIA_UNAVAILABLE, -1)
                when (codeError) {
                    PlayerService.BR_CODE_ERROR -> onConnectionError()
                }
            }
        }
        requireActivity().registerReceiver(
            playerBroadcastReceiver,
            IntentFilter(PlayerService.BR_ACTION_ERROR),
        )
    }
}
