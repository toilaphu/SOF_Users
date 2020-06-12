package com.phunguyen.stackoverflowuser.utils

import android.content.Context
import android.content.SharedPreferences

object SharedData {

    private var sharedPref: SharedPreferences? = null
    private const val PreferencesFile = "sof_users_preferences"
    private const val Display_Bookmark = "Display_Bookmarks_Only"

    fun init(context: Context) {
        sharedPref = context.getSharedPreferences(
            PreferencesFile,
            Context.MODE_PRIVATE
        )
    }

    @Synchronized
    fun setBool(key: String?, value: Boolean?) {
        val editor = sharedPref?.edit()
        editor?.putBoolean(key, value!!)
        editor?.apply()
    }

    @Synchronized
    fun getBool(key: String?): Boolean {
        return sharedPref?.getBoolean(key, false) ?: false
    }

    var showBookmarkSetting: Boolean
        get() = getBool(Display_Bookmark)
        set(isShowBookmarkOnly) {
            setBool(Display_Bookmark, isShowBookmarkOnly)
        }
}