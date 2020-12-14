package com.noomit.radioalarm02.ui.alarm_list

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.base.DatabaseViewModelFactory
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.databinding.FragmentSelectMelodyBinding
import com.noomit.radioalarm02.ui.favorites.FavoritesViewModel
import kotlinx.coroutines.flow.filterNotNull

class SelectMelodyFragment : BaseFragment(R.layout.fragment_select_melody) {

    override val viewBinding: FragmentSelectMelodyBinding by viewBinding()

    private val favViewModel: FavoritesViewModel by viewModels {
        DatabaseViewModelFactory(requireActivity().application as Application00)
    }

    private val alarmViewModel: AlarmManagerViewModel by activityViewModels()

    override fun prepareUi() {
    }

    override fun listenUiEvents() = with(viewBinding) {
        btnSetAsMelody.setOnClickListener {
            favViewModel.nowPlaying.value?.let {
                alarmViewModel.setMelody(it.station)
            }
            findNavController().popBackStack()
        }
    }

    override fun observeModel() {
        collect(favViewModel.nowPlaying.filterNotNull()) {
            viewBinding.btnSetAsMelody.text = it.station.name
        }
    }
}