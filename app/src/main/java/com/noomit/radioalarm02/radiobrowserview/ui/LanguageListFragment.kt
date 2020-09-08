package com.noomit.radioalarm02.radiobrowserview.ui

import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.databinding.FragmentLanguageListBinding
import com.noomit.radioalarm02.ui.RadioVMFragment

class LanguageListFragment : RadioVMFragment(R.layout.fragment_language_list) {

    private val viewBinding: FragmentLanguageListBinding by viewBinding()

    override fun prepareUi() {
        viewBinding.tvTextDebug.text = "Loading..."
    }

    override fun listenUiEvents() {
    }

    override fun observeModel() = with(viewModel) {
        languageList.observe(viewLifecycleOwner) {
            it.fold(
                onSuccess = { languageList ->
                    viewBinding.tvTextDebug.text = "${languageList.size}"
                },
                onFailure = {},
            )
        }
    }
}