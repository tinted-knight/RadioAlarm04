package com.noomit.radioalarm02.ui.alarm_fire

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.service.MediaItem
import com.noomit.radioalarm02.util.PlayerServiceFragment
import com.noomit.radioalarm02.util.collect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmFireFragment : PlayerServiceFragment<IAlarmFireLayout>() {
    private var ringtone: Ringtone? = null

    private val viewModel: DismissAlarmViewModel by activityViewModels()

    override val layout: View
        get() = AlarmFireLayout(requireContext())

    override val contour: IAlarmFireLayout
        get() = view as IAlarmFireLayout

    override val notificationCaption: String
        get() = getString(R.string.app_name)

    override fun onDestroyView() {
        stopRingtone()
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

    override fun onConnectionError() {
        playDefaultRingtone()
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
