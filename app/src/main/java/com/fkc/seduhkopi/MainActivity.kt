package com.fkc.seduhkopi

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fkc.seduhkopi.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val sharedPreference : SharedPreference = SharedPreference(this)
    private lateinit var mHomeWatcher : HomeWatcher
    private lateinit var binding : ActivityMainBinding
    private var musicState = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        musicState = sharedPreference.getValueBoolean("TOGGLE_MUSIC", true)

        if (musicState) {
            doBindService()
            val music = Intent()
            music.setClass(this, MusicService::class.java)
            startService(music)
        }

        mHomeWatcher = HomeWatcher(this)
        mHomeWatcher.setOnHomePressedListener(object : HomeWatcher.OnHomePressedListener {
            override fun onHomePressed() {
                if (mServ != null) {
                    mServ!!.pauseMusic()
                }
            }

            override fun onHomeLongPressed() {
                if (mServ != null) {
                    mServ!!.pauseMusic()
                }
            }
        })
        mHomeWatcher.startWatch()

        val buttonKomik = binding.buttonKomik
        buttonKomik.setOnClickListener {
            val intent = Intent(this, ComicOptActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        }

        val buttonDeteksi = binding.buttonDeteksi
        buttonDeteksi.setOnClickListener {
            val intent = Intent(this, DetectionIntroActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        }

        val buttonBantuan = binding.buttonBantuan
        buttonBantuan.setOnClickListener {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        }

        val buttonTentang = binding.aboutButton
        buttonTentang.setOnClickListener {
            showCustomAlert()
        }

        val toggleMusic = binding.musicToggle
        toggleMusic.isChecked = musicState
        toggleMusic.setOnClickListener {
            if (toggleMusic.isChecked) {
                doBindService()
                val music = Intent()
                music.setClass(this, MusicService::class.java)
                startService(music)
                musicState = true
                if (mServ != null) {
                    mServ!!.startMusic()
                }
            } else {
                musicState = false
                if (mServ != null) {
                    mServ!!.stopMusic()
                }
            }
            sharedPreference.save("TOGGLE_MUSIC", musicState)
        }
    }

    private fun showCustomAlert() {
        val dialogView = layoutInflater.inflate(R.layout.about_dialog, null)
        val customDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .show()
        val btDismiss = dialogView.findViewById<Button>(R.id.button_ok)
        btDismiss.setOnClickListener {
            customDialog.dismiss()
        }
        val link1 = dialogView.findViewById<TextView>(R.id.text_about_1)
        val link2 = dialogView.findViewById<TextView>(R.id.text_about_3)
        val link3 = dialogView.findViewById<TextView>(R.id.text_about_4)
        val link4 = dialogView.findViewById<TextView>(R.id.text_about_5)
        link1.movementMethod = LinkMovementMethod.getInstance()
        link2.movementMethod = LinkMovementMethod.getInstance()
        link3.movementMethod = LinkMovementMethod.getInstance()
        link4.movementMethod = LinkMovementMethod.getInstance()
        link1.setLinkTextColor(Color.parseColor("#3A130C"))
        link2.setLinkTextColor(Color.parseColor("#3A130C"))
        link3.setLinkTextColor(Color.parseColor("#3A130C"))
        link4.setLinkTextColor(Color.parseColor("#3A130C"))
    }

    override fun onBackPressed() {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("Apakah anda ingin keluar dari aplikasi?")
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setPositiveButton("Ya") { _, _ -> finish()
            }
            // negative button text and action
            .setNegativeButton("Tidak") { dialog, _ -> dialog.cancel()
            }

        // create dialog box
        val alert = dialogBuilder.create()
        // set title for alert dialog box
        alert.setTitle("KONFIRMASI KELUAR")
        // show alert dialog
        alert.show()
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