package com.noomit.radioalarm02.ui.radio_browser.languagelist

import com.noomit.radioalarm02.model.LanguageModel
import com.noomit.radioalarm02.radiobrowserview.adapters.CategoryDiffUtil
import com.noomit.radioalarm02.radiobrowserview.adapters.CategoryListAdapter

class LanguageListAdapter(onClick: (LanguageModel) -> Unit) :
    CategoryListAdapter<LanguageModel>(onClick, LanguageDiffUtil())

class LanguageDiffUtil : CategoryDiffUtil<LanguageModel>()
