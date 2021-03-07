package com.fkc.seduhkopi

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fkc.seduhkopi.databinding.ActivityDetectionFormBinding

class DetectionFormActivity : AppCompatActivity() {

    private var counter = 0
    private var numOfYes = 0
    private lateinit var mHomeWatcher : HomeWatcher
    private lateinit var binding : ActivityDetectionFormBinding

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detection_form)

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

        val questions = resources.getStringArray(R.array.detection_questions_array)
        val closing = resources.getString(R.string.detection_item_4)
        val ket = resources.getString(R.string.detection_item_5)
        val interpretGood = resources.getString(R.string.detection_item_6)
        val interpretBad = resources.getString(R.string.detection_item_7)
        binding.questions.text = questions[counter]
        val numOfQuestion = questions.count()
        val buttonYes = binding.buttonYa
        val buttonNo = binding.buttonTidak
        val buttonMenu = binding.buttonMenu
        val buttonBantuan = binding.buttonBantuan
        val group1 = binding.groupPilihan1
        val group2 = binding.groupPilihan2
        val determinateBar = binding.determinateBar
        determinateBar.max = 24

        buttonYes.setOnClickListener {
            val tx = binding.questions
            val tx2 = binding.keterangan
            counter++
            numOfYes++
            if (counter >= numOfQuestion) {
                tx.text = "Skor kamu adalah $numOfYes dari total skor $numOfQuestion.\n\n$closing"
                if (numOfYes > 13) {
                    tx2.text = "$ket\n\n$interpretBad"
                }
                else {
                    tx2.text = "$ket\n\n$interpretGood"
                }
                group1.visibility = View.GONE
                group2.visibility = View.VISIBLE
            }
            else {
                tx.text = questions[counter]
                determinateBar.progress = counter
            }
        }

        buttonNo.setOnClickListener {
            val tx = binding.questions
            val tx2 = binding.keterangan
            counter++
            if (counter >= numOfQuestion) {
                tx.text = "Skor kamu adalah $numOfYes dari total skor $numOfQuestion.\n\n$closing"
                if (numOfYes > 13) {
                    tx2.text = "$ket\n\n$interpretBad"
                }
                else {
                    tx2.text = "$ket\n\n$interpretGood"
                }
                group1.visibility = View.GONE
                group2.visibility = View.VISIBLE
            }
            else {
                tx.text = questions[counter]
                determinateBar.progress = counter
            }
        }

        buttonMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
        }

        buttonBantuan.setOnClickListener {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
            this.finish()
        }
    }

    override fun onBackPressed() {
        // build alert dialog
        val dialogBuilder = AlertDialog.Builder(this)

        // set message of alert dialog
        dialogBuilder.setMessage("Apakah anda ingin berhenti mengisi form deteksi?")
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
        alert.setTitle("KONFIRMASI BERHENTI")
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