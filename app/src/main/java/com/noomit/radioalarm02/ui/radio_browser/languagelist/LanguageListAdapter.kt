package com.noomit.radioalarm02.ui.radio_browser.languagelist

import com.noomit.domain.CategoryModel
import com.noomit.radioalarm02.ui.radio_browser.adapters.CategoryDiffUtil
import com.noomit.radioalarm02.ui.radio_browser.adapters.CategoryListAdapter

// #todo Do not need this. Looks like CategoryListAdapter is enough
class LanguageListAdapter(onClick: (CategoryModel) -> Unit) :
    CategoryListAdapter<CategoryModel>(onClick, LanguageDiffUtil())

class LanguageDiffUtil : CategoryDiffUtil<CategoryModel>()
