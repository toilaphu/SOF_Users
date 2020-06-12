package com.phunguyen.stackoverflowuser.ui.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedViewModel @Inject constructor() : ViewModel() {

    val showBookmarkOnlySelected = MutableLiveData<Boolean>()

    fun setShowBookmarkOption(isSelected: Boolean) {
        showBookmarkOnlySelected.value = isSelected
    }
}