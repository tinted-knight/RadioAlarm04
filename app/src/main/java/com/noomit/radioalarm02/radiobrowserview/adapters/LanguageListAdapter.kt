package com.noomit.radioalarm02.radiobrowserview.adapters

import com.noomit.radioalarm02.model.LanguageModel

class LanguageListAdapter(onClick: (LanguageModel) -> Unit) :
    CategoryListAdapter<LanguageModel>(onClick, LanguageDiffUtil())

class LanguageDiffUtil : CategoryDiffUtil<LanguageModel>()
