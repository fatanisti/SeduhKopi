package com.fkc.seduhkopi

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.fkc.seduhkopi.databinding.ActivityPreludeBinding

class PreludeActivity : AppCompatActivity() {
    private lateinit var cdt: CountDownTimer
    private lateinit var binding : ActivityPreludeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prelude)

        val prelude1 = binding.first
        val prelude2 = binding.second
        val buttonSelesai = binding.buttonSelesai
        val spinner = binding.progressBar

        buttonSelesai.setOnClickListener {
            prelude1.visibility = View.INVISIBLE
            prelude2.visibility = View.VISIBLE
            buttonSelesai.visibility = View.INVISIBLE
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            cdt = object : CountDownTimer(6000, 1000) {
                override fun onTick(p0: Long) {
                    spinner.visibility = View.VISIBLE
                }
                override fun onFinish() {
                    spinner.visibility = View.INVISIBLE
                }
            }
            Handler().postDelayed({
                cdt.start()
            }, 1000)
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit)
                this.finish()
            }, 7000)
        }
    }
}