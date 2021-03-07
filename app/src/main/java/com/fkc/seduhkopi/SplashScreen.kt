package com.fkc.seduhkopi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fkc.seduhkopi.databinding.SplashScreenBinding
import kotlinx.android.synthetic.main.splash_screen.*

class SplashScreen : AppCompatActivity() {
    private val splashdelay: Long = 5000
    private var mDelayHandler: Handler? = null
    private var progressBarStatus = 0
    private var dummy:Int = 0
    private lateinit var binding : SplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.splash_screen)
        mDelayHandler = Handler()
        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, splashdelay)
    }
    private fun launchNextActivity() {
        val intent = Intent(this, PreludeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        mDelayHandler!!.removeCallbacks(mRunnable)
        this.finish()
    }
    private val mRunnable: Runnable = Runnable {
        Thread {
            while (progressBarStatus < 100) {
                // performing some dummy operation
                try {
                    dummy += 25
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                // tracking progress
                progressBarStatus = dummy
                // Updating the progress bar
                splash_screen_progress_bar.progress = progressBarStatus
            }
            //splash_screen_progress_bar.setProgress(10)
            launchNextActivity()
        }.start()
    }
    override fun onDestroy() {
        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }
        super.onDestroy()
    }
}