package com.fkc.seduhkopi

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fkc.seduhkopi.databinding.ActivityComicReadBinding

class ComicReadActivity : AppCompatActivity() {
    private val sharedPreference : SharedPreference = SharedPreference(this)
    private lateinit var mHomeWatcher : HomeWatcher
    private lateinit var binding: ActivityComicReadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comic_read)

        doBindService()
        val music = Intent()
        music.setClass(this, MusicService::class.java)
        startService(music)

        mHomeWatcher = HomeWatcher(this)
        mHomeWatcher.setOnHomePressedListener(object: HomeWatcher.OnHomePressedListener {
            override fun onHomePressed() {
                if (mServ != null)
                {
                    mServ!!.pauseMusic()
                }
            }
            override fun onHomeLongPressed() {
                if (mServ != null)
                {
                    mServ!!.pauseMusic()
                }
            }
        })
        mHomeWatcher.startWatch()

        val navArray = resources.getStringArray(R.array.comic_nav_array)
        val addressArray = resources.getStringArray(R.array.comic_address_array)
        var positionEpi = intent.getIntExtra("position", 0)
        val mWebView: WebView = binding.comicView
        mWebView.settings.builtInZoomControls = true
        mWebView.settings.displayZoomControls = false
        mWebView.settings.useWideViewPort = true
        mWebView.settings.loadWithOverviewMode = true
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        mWebView.loadUrl(addressArray[positionEpi])

        val buttonNext = binding.buttonNextEp
        when (positionEpi) {
            2 -> {
                buttonNext.visibility = View.INVISIBLE
            }
            else -> {
                buttonNext.text = navArray[positionEpi+1]
            }
        }

        buttonNext.setOnClickListener {
            when (sharedPreference.getValueInt("UNLOCKED_EPISODES")) {
                2 -> {
                    when (positionEpi) {
                        2 -> {
                            //
                        }
                        1 -> {
                            positionEpi++
                            mWebView.loadUrl(addressArray[positionEpi])
                            buttonNext.visibility = View.INVISIBLE
                        }
                        0 -> {
                            positionEpi++
                            mWebView.loadUrl(addressArray[positionEpi])
                            buttonNext.text = navArray[positionEpi + 1]
                        }
                    }
                }
                1 -> {
                    when (positionEpi) {
                        1 -> {
                            val intent = Intent(this, ComicQuestionActivity::class.java)
                            intent.putExtra("prev_epi", 1)
                            startActivity(intent)
                            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
                        }
                        0 -> {
                            positionEpi++
                            mWebView.loadUrl(addressArray[positionEpi])
                            buttonNext.text = navArray[positionEpi + 1]
                        }
                    }
                }
                else -> {
                    val intent = Intent(this, ComicQuestionActivity::class.java)
                    intent.putExtra("prev_epi", 0)
                    startActivity(intent)
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
                }
            }
        }

        val buttonMenu = binding.buttonMenu
        buttonMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        }
    }

    private var mIsBound = false
    private var mServ: MusicService? = null
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            mServ = (binder as MusicService.ServiceBinder).service
        }
        override fun onServiceDisconnected(name: ComponentName) {
            mServ = null
        }
    }

    private fun doBindService() {
        bindService(
            Intent(this, MusicService::class.java),
            serviceConnection, Context.BIND_AUTO_CREATE
        )
        mIsBound = true
    }

    private fun doUnbindService() {
        if (mIsBound) {
            unbindService(serviceConnection)
            mIsBound = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (mServ != null) {
            mServ!!.resumeMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        val pm =
            getSystemService(Context.POWER_SERVICE) as PowerManager
        val isScreenOn: Boolean
        isScreenOn = pm.isInteractive
        if (!isScreenOn) {
            if (mServ != null) {
                mServ!!.pauseMusic()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
        mHomeWatcher.stopWatch()
        val music = Intent()
        music.setClass(this, MusicService::class.java)
        stopService(music)
    }
}