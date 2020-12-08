package com.noomit.radioalarm02.ui.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.playerservice.MediaItem
import com.noomit.radioalarm02.Favorite
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.DatabaseViewModelFactory
import com.noomit.radioalarm02.base.PlayerBaseFragment
import com.noomit.radioalarm02.data.AppDatabase
import com.noomit.radioalarm02.databinding.FragmentStationListBinding
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.favorites.adapters.FavoriteListAdapter

class FavoritesFragment : PlayerBaseFragment(
    playerViewId = R.id.exo_player_view,
    playerControlId = R.id.exo_player_controls,
    contentLayoutId = R.layout.fragment_station_list,
) {

    override val viewBinding: FragmentStationListBinding by viewBinding()

    private val favoritesViewModel: FavoritesViewModel by activityViewModels {
        DatabaseViewModelFactory(AppDatabase.getInstance(requireActivity()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.exoPlayerControls.player = playerView.player
    }

    override fun prepareUi() {
        showLoading()
        viewBinding.rvStationList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            adapter = FavoriteListAdapter(
                onClick = { value ->
                    requireContext().toast(value.name)
                    favoritesViewModel.onClick(value)
                    play(value)
                },
            )
            // #todo StationList restore state
//            layoutManager?.onRestoreInstanceState()
        }
    }

    override fun listenUiEvents() {}

    override fun observeModel() = with(favoritesViewModel) {
        selectAll.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                showContent(it)
            } else {
                showEmpty()
            }
        }
    }

    override fun onServiceConnected() {}

    private fun showLoading() = with(viewBinding) {
        progressIndicator.visibility = View.VISIBLE
        rvStationList.visibility = View.INVISIBLE
    }

    private fun showContent(values: List<Favorite>) = with(viewBinding) {
        (rvStationList.adapter as FavoriteListAdapter).submitList(values)
        rvStationList.visibility = View.VISIBLE
        progressIndicator.visibility = View.GONE
    }

    private fun showEmpty() = with(viewBinding) {
        progressIndicator.visibility = View.INVISIBLE
        rvStationList.visibility = View.INVISIBLE
        tvEmpty.visibility = View.VISIBLE
    }

    private fun play(station: Favorite) {
        service?.mediaItem = MediaItem(station.stream_url, station.name)
        service?.play()
    }
}