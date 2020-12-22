package com.noomit.radioalarm02.alarm.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.noomit.playerservice.MediaItem
import com.noomit.playerservice.PlayerService
import com.noomit.playerservice.PlayerServiceFragment
import com.noomit.radioalarm02.alarm.DismissAlarmViewModel
import com.noomit.radioalarm02.base.AndroidViewModelFactory
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.data.AppDatabase

class AlarmFireFragment : PlayerServiceFragment() {
    private var ringtone: Ringtone? = null

    private val viewModel: DismissAlarmViewModel by activityViewModels {
        AndroidViewModelFactory(
            AppDatabase.getInstance(requireActivity()),
            requireActivity().application,
        )
    }

    private lateinit var playerBroadcastReceiver: BroadcastReceiver

    private val contour: IAlarmFireLayout
        get() = view as IAlarmFireLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return AlarmFireLayout(requireContext())
    }

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

    override fun prepareView() {}

    override fun onServiceConnected() {
        if (viewModel.melodyUrl.isNullOrEmpty()) {
            playDefaultRingtone()
            return
        }
        viewModel.melodyUrl?.let {
            service?.mediaItem = MediaItem(url = it, title = it)
            service?.play()
            contour.setStationName(it)
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
