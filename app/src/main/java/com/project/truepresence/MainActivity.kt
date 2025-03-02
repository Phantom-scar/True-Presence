package com.project.truepresence

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Play startup sound
        val mediaPlayer = MediaPlayer.create(this, R.raw.startup_sound)
        mediaPlayer.start()

        // Apply fade-in animation to logo
        val logo = findViewById<ImageView>(R.id.logo)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        logo.startAnimation(fadeIn)

        // Delay and navigate to LoginActivity
        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer.release() // Release media player
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 2-second delay
    }
}
