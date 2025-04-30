package com.example.news_wirehub

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoImageView: ImageView = findViewById(R.id.logoImageView)

        // Scale animation for the logo
        val scaleAnimation = ObjectAnimator.ofPropertyValuesHolder(
            logoImageView,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        scaleAnimation.duration = 1500
        scaleAnimation.repeatCount = ObjectAnimator.INFINITE
        scaleAnimation.repeatMode = ObjectAnimator.REVERSE

        // Fade in animation for the entire splash screen
        val fadeInAnimation = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f)
        fadeInAnimation.duration = 1500

        // Animator set for running both animations concurrently
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleAnimation, fadeInAnimation)
        animatorSet.start()

        Handler().postDelayed({
            // This code will be executed after the splash screen delay
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close the splash activity to prevent the user from going back to it
        }, SPLASH_TIME_OUT.toLong())
    }
}
