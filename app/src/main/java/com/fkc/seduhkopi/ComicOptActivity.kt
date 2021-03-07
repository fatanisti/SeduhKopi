package com.fkc.seduhkopi

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fkc.seduhkopi.databinding.ActivityComicOptBinding

class ComicOptActivity : AppCompatActivity() {
    private val sharedPreference : SharedPreference = SharedPreference(this)
    private lateinit var mHomeWatcher : HomeWatcher
    private lateinit var binding: ActivityComicOptBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comic_opt)

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

        val group1 = binding.ep1Group
        val group2 = binding.ep2Group
        val group3 = binding.ep3Group

        val buttonEp1 = binding.buttonKomikEp1
        val buttonEp2 = binding.buttonKomikEp2
        val buttonEp3 = binding.buttonKomikEp3

        when (sharedPreference.getValueInt("UNLOCKED_EPISODES")) {
             2 -> {
                 group1.visibility = View.VISIBLE
                 group2.visibility = View.VISIBLE
                 group3.visibility = View.VISIBLE
            }
            1 -> {
                group1.visibility = View.VISIBLE
                group2.visibility = View.VISIBLE
                group3.visibility = View.INVISIBLE
            }
            else -> {
                group1.visibility = View.VISIBLE
                group2.visibility = View.INVISIBLE
                group3.visibility = View.INVISIBLE
            }
        }

        buttonEp1.setOnClickListener {
            val intent = Intent(this, ComicReadActivity::class.java)
            intent.putExtra("position", 0)
            startActivity(intent)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        }

        buttonEp2.setOnClickListener {
            val intent = Intent(this, ComicReadActivity::class.java)
            intent.putExtra("position", 1)
            startActivity(intent)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        }

        buttonEp3.setOnClickListener {
            val intent = Intent(this, ComicReadActivity::class.java)
            intent.putExtra("position", 2)
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