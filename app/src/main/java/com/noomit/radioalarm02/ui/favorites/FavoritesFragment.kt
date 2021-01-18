package com.noomit.radioalarm02.ui.favorites

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.noomit.playerservice.MediaItem
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.DatabaseViewModelFactory
import com.noomit.radioalarm02.base.PlayerServiceFragment
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter

class FavoritesFragment : PlayerServiceFragment<IFavoritesLayout>() {

    private val favoritesViewModel: FavoritesViewModel by viewModels {
        DatabaseViewModelFactory(requireActivity().application as Application00)
    }

    override val layout: View
        get() = FavoritesLayout(requireContext())

    override val contour: IFavoritesLayout
        get() = view as IFavoritesLayout

    override val notificationCaption: String
        get() = getString(R.string.app_name)

    override fun initPlayerViews() {
        playerControlView = contour.playerControll
        playerView = contour.playerView
    }

    override fun prepareView(savedState: Bundle?) {
        val adapter = StationListAdapter(delegate = favoritesViewModel)
        contour.apply {
            setStationsAdapter(adapter)
            showLoading()
        }
        contour.listener = favoritesViewModel
    }

    override fun observeViewModel() {
        collect(favoritesViewModel.selectAll) {
            contour.showContent(it)
        }

        collect(favoritesViewModel.nowPlaying) {
            if (it != null) {
                service?.mediaItem = MediaItem(url = it.station.streamUrl, title = it.station.name)
                service?.play()
                contour.nowPlaying(it.station, it.inFavorites)
            } else {
                service?.stop()
                contour.nowPlayingEmpty()
            }
        }

        favoritesViewModel.commands.observe(viewLifecycleOwner) { command ->
            when (command) {
                is FavoritesDirections.OpenExternalLink -> startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(command.url)
                    })
            }
        }
    }
}
