package com.noomit.radioalarm02.ui.favorites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.fragment.app.viewModels
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.service.ServiceMediaItem
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.radio_browser.stationlist.IStationListLayout
import com.noomit.radioalarm02.ui.radio_browser.stationlist.StationListLayout
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import com.noomit.radioalarm02.util.fragment.PlayerServiceFragment
import com.noomit.radioalarm02.util.fragment.collect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : PlayerServiceFragment<IStationListLayout>() {

    private val favoritesViewModel: FavoritesViewModel by viewModels()

    override val layout: View
        get() = StationListLayout(requireContext())

    override val contour: IStationListLayout
        get() = view as IStationListLayout

    override val notificationCaption: String
        get() = getString(R.string.app_name)

    private var recyclerState: Parcelable? = null

    override fun initPlayerViews() {
        playerControlView = contour.playerControl
    }

    override fun prepareView(savedState: Bundle?) {
        val adapter = StationListAdapter(delegate = favoritesViewModel)
        contour.apply {
            setStationsAdapter(adapter)
            showLoading()
            recyclerState?.let {
                contour.setRecyclerState(it)
                return@apply
            }
            savedState?.let { bundle ->
                bundle.getParcelable<Parcelable>(RECYCLER_STATE)?.let { state ->
                    contour.setRecyclerState(state)
                }
            }
        }
        contour.listener = favoritesViewModel
    }

    override fun observeViewModel() {
        collect(favoritesViewModel.selectAll) {
            contour.showContent(it)
        }

        collect(favoritesViewModel.nowPlaying) {
            if (it != null) {
                service?.mediaItem = ServiceMediaItem(url = it.station.streamUrl, title = it.station.name)
                service?.play()
                contour.nowPlaying(it.station, it.inFavorites)
            } else {
                service?.pause()
                contour.nowPlayingEmpty()
            }
        }
    }

    override fun observeCommands() {
        collect(favoritesViewModel.commands) { command ->
            when (command) {
                is FavoritesDirections.OpenExternalLink -> startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(command.url)
                    })
            }
        }
    }

    override fun onConnectionError() {
        requireContext().toast(getString(R.string.toast_cannot_connect_to_station))
    }

    override fun onPause() {
        val state = contour.getRecyclerState()
        recyclerState = state
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(RECYCLER_STATE, recyclerState)
        super.onSaveInstanceState(outState)
    }
}
