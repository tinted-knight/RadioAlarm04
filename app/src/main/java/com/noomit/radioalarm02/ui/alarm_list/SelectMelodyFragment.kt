package com.noomit.radioalarm02.ui.alarm_list

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.base.DatabaseViewModelFactory
import com.noomit.radioalarm02.data.AppDatabase
import com.noomit.radioalarm02.databinding.FragmentSelectMelodyBinding
import com.noomit.radioalarm02.ui.favorites.FavoritesViewModel

class SelectMelodyFragment : BaseFragment(R.layout.fragment_select_melody) {

    override val viewBinding: FragmentSelectMelodyBinding by viewBinding()

    private val favViewModel: FavoritesViewModel by activityViewModels {
        DatabaseViewModelFactory(AppDatabase.getInstance(requireActivity()))
    }

    private val alarmViewModel: AlarmManagerViewModel by activityViewModels()

    override fun prepareUi() {
    }

    override fun listenUiEvents() = with(viewBinding) {
        btnSetAsMelody.setOnClickListener {
            favViewModel.selected.value?.let { alarmViewModel.setMelody(it) }
            findNavController().popBackStack()
        }
    }

    override fun observeModel() {
        favViewModel.selected.observe(viewLifecycleOwner) {
            viewBinding.btnSetAsMelody.text = it.name
        }
    }
}