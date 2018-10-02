package com.zeeshanhussain.timetable.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window

import com.zeeshanhussain.timetable.R

class SplashActivity : Activity() {
    private var runnable: Runnable? = null
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = window
        window.decorView.overScrollMode = View.OVER_SCROLL_NEVER
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        initialize()
    }

    private fun initialize() {
        handler = Handler()
        runnable = Runnable {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
        }
        handler!!.postDelayed(runnable, 1000)
    }

    override fun onBackPressed() {
        finish()
        handler!!.removeCallbacks(runnable)
    }

}