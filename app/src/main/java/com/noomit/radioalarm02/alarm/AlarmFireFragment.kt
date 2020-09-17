package com.noomit.radioalarm02.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.playerservice.MediaItem
import com.noomit.playerservice.PlayerService
import com.noomit.radioalarm02.DismissAlarmViewModel
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.AndroidViewModelFactory
import com.noomit.radioalarm02.base.PlayerBaseFragment
import com.noomit.radioalarm02.databinding.FragmentAlarmFireBinding
import com.noomit.radioalarm02.model.AppDatabase
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-alarm_activity").i(message)

class AlarmFireFragment : PlayerBaseFragment(
    playerViewId = R.id.exo_player_view,
    playerControlId = R.id.exo_player_controls,
    contentLayoutId = R.layout.fragment_alarm_fire,
) {
    override val viewBinding: FragmentAlarmFireBinding by viewBinding()

    private var ringtone: Ringtone? = null

    private val viewModel: DismissAlarmViewModel by activityViewModels {
        AndroidViewModelFactory(
            AppDatabase.getInstance(requireActivity()),
            requireActivity().application,
        )
    }

    private lateinit var playerBroadcastReceiver: BroadcastReceiver

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
        super.onDestroyView()
        stopRingtone()
        requireActivity().unregisterReceiver(playerBroadcastReceiver)
    }

    override fun prepareUi() {
        viewModel.alarmFired()
    }

    override fun onServiceConnected() {
        if (viewModel.melodyUrl.isNullOrEmpty()) {
            playDefaultRingtone()
            return
        }
        viewModel.melodyUrl?.let {
            service?.mediaItem = MediaItem(url = it, title = it)
            service?.play()
            viewBinding.tvStationName.text = it
            isPlaying = true
        }
    }

    override fun listenUiEvents() {
        viewBinding.btnDismiss.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    override fun observeModel() {
    }

    override fun renderPlayingView() {
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