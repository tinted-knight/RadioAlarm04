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
import com.noomit.playerservice.MediaItem
import com.noomit.playerservice.PlayerService
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.PlayerServiceFragment
import com.noomit.radioalarm02.base.collect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmFireFragment : PlayerServiceFragment<IAlarmFireLayout>() {
    private var ringtone: Ringtone? = null

    private val viewModel: DismissAlarmViewModel by activityViewModels()

    private lateinit var playerBroadcastReceiver: BroadcastReceiver

    override val layout: View
        get() = AlarmFireLayout(requireContext())

    override val contour: IAlarmFireLayout
        get() = view as IAlarmFireLayout

    override val notificationCaption: String
        get() = getString(R.string.app_name)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.getIntExtra(PlayerService.BR_MEDIA_UNAVAILABLE, -1)) {
                    PlayerService.BR_CODE_ERROR -> playDefaultRingtone()
                }
            }
        }
        requireActivity().registerReceiver(
            playerBroadcastReceiver,
            IntentFilter(PlayerService.BROADCAST_FILTER),
        )
    }

    override fun onDestroyView() {
        stopRingtone()
        requireActivity().unregisterReceiver(playerBroadcastReceiver)
        super.onDestroyView()
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
            service?.mediaItem = MediaItem(
                url = it,
                title = viewModel.melodyName ?: it
            )
            service?.play()
            contour.setStationName(viewModel.melodyName ?: "")
        }
    }

    override fun initPlayerViews() {
        playerControlView = contour.playerControll
        playerView = contour.playerView
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
        ringtone = null
    }
}
