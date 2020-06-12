package com.phunguyen.stackoverflowuser

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.phunguyen.stackoverflowuser.di.Injectable
import com.phunguyen.stackoverflowuser.ui.common.SharedViewModel
import com.phunguyen.stackoverflowuser.utils.SharedData
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val sharedViewModel: SharedViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_show_bookmarks_settings).isChecked =
            SharedData.showBookmarkSetting
        sharedViewModel.setShowBookmarkOption(SharedData.showBookmarkSetting)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_show_bookmarks_settings -> {
                item.isChecked = !item.isChecked
                SharedData.showBookmarkSetting = item.isChecked
                sharedViewModel.setShowBookmarkOption(item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
