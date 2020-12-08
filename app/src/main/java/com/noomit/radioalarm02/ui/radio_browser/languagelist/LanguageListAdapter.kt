package com.noomit.radioalarm02.ui.radio_browser.languagelist

import com.noomit.radioalarm02.data.CategoryModel
import com.noomit.radioalarm02.radiobrowserview.adapters.CategoryDiffUtil
import com.noomit.radioalarm02.radiobrowserview.adapters.CategoryListAdapter

class LanguageListAdapter(onClick: (CategoryModel) -> Unit) :
    CategoryListAdapter<CategoryModel>(onClick, LanguageDiffUtil())

class LanguageDiffUtil : CategoryDiffUtil<CategoryModel>()
