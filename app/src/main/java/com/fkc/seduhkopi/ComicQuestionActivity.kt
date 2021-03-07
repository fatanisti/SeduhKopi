package com.fkc.seduhkopi

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fkc.seduhkopi.databinding.ActivityComicQuestionBinding

class ComicQuestionActivity : AppCompatActivity() {
    private val sharedPreference : SharedPreference = SharedPreference(this)
    private lateinit var mHomeWatcher : HomeWatcher
    private lateinit var binding: ActivityComicQuestionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_comic_question)

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

        val questArray = resources.getStringArray(R.array.comic_questions_array)
        val answerArray = resources.getStringArray(R.array.comic_answers_array)
        val wrongAns = resources.getString(R.string.comic_wrong_ans)
        val rightAns = resources.getString(R.string.comic_right_ans)
        val prevEpi = intent.getIntExtra("prev_epi", 0)
        val comQuest = binding.comicQuestion
        val opt1 = binding.buttonOpt1
        val opt2 = binding.buttonOpt2
        val opt3 = binding.buttonOpt3

        when (prevEpi) {
            0 -> {
                comQuest.text = questArray[0]
                opt1.text = answerArray[0]
                opt2.text = answerArray[1]
                opt3.text = answerArray[2]

                opt1.setOnClickListener {
                    Toast.makeText(this@ComicQuestionActivity, wrongAns, Toast.LENGTH_SHORT).show()
                }

                opt2.setOnClickListener {
                    sharedPreference.save("UNLOCKED_EPISODES", 1)
                    Toast.makeText(this@ComicQuestionActivity, rightAns, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ComicReadActivity::class.java)
                    intent.putExtra("position", 1)
                    startActivity(intent)
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
                    this.finish()
                }

                opt3.setOnClickListener {
                    Toast.makeText(this@ComicQuestionActivity, wrongAns, Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                comQuest.text = questArray[1]
                opt1.text = answerArray[3]
                opt2.text = answerArray[4]
                opt3.text = answerArray[5]

                opt1.setOnClickListener {
                    Toast.makeText(this@ComicQuestionActivity, wrongAns, Toast.LENGTH_SHORT).show()
                }

                opt2.setOnClickListener {
                    Toast.makeText(this@ComicQuestionActivity, wrongAns, Toast.LENGTH_SHORT).show()
                }

                opt3.setOnClickListener {
                    sharedPreference.save("UNLOCKED_EPISODES", 2)
                    Toast.makeText(this@ComicQuestionActivity, rightAns, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ComicReadActivity::class.java)
                    intent.putExtra("position", 2)
                    startActivity(intent)
                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
                    this.finish()
                }
            }
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