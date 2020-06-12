package com.phunguyen.stackoverflowuser.utils
import android.view.View

object AppUtils {
    /**
     * Disable a view in a duration time, after that the view will be enabled.
     */
    fun disableViewInDuration(view: View?, duration: Long) {
        view?.let {
            it.isEnabled = false
            it.postDelayed({
                it.isEnabled = true
            }, duration)
        }
    }
}