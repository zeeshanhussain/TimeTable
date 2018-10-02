package com.zeeshanhussain.timetable.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

import com.ncapdevi.fragnav.FragNavController
import com.roughike.bottombar.BottomBar
import com.roughike.bottombar.OnTabReselectListener
import com.roughike.bottombar.OnTabSelectListener
import com.zeeshanhussain.timetable.R
import com.zeeshanhussain.timetable.ui.fragments.AttendanceFragment
import com.zeeshanhussain.timetable.ui.fragments.SubjectsFragment
import com.zeeshanhussain.timetable.ui.fragments.TimetableFragment

import java.util.ArrayList

import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private val TAB_FIRST = FragNavController.TAB1
    private val TAB_SECOND = FragNavController.TAB2
    private val TAB_THIRD = FragNavController.TAB3
    private var doubleBackToExitPressedOnce = false
    private var fragNavController: FragNavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragments = ArrayList<Fragment>(3)
        fragments.add(TimetableFragment())
        fragments.add(AttendanceFragment())
        fragments.add(SubjectsFragment())
        fragNavController = FragNavController(savedInstanceState, supportFragmentManager, R.id.contentContainer, fragments, TAB_FIRST)
        val bottomBar = findViewById<BottomBar>(R.id.bottomBar)
        bottomBar.setDefaultTabPosition(1)
        bottomBar.setOnTabSelectListener { tabId ->
            when (tabId) {
                R.id.tab_timetable -> fragNavController!!.switchTab(TAB_FIRST)
                R.id.tab_attendance -> fragNavController!!.switchTab(TAB_SECOND)
                R.id.tab_subjects -> fragNavController!!.switchTab(TAB_THIRD)
            }
        }

        bottomBar.setOnTabReselectListener { fragNavController!!.clearStack() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, resources.getString(R.string.press_back), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this@MainActivity, PreferencesActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
