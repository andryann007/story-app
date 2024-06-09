package com.andryan.storyapp.ui.activity.splash

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.databinding.ActivitySplashBinding
import com.andryan.storyapp.ui.activity.main.MainActivity
import com.andryan.storyapp.ui.activity.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.splash) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()
        checkUserLogin()
    }

    private fun checkUserLogin() {
        viewModel.getSession().observe(this@SplashActivity) { session ->
            if (session?.isLogin == false) {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SplashActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }, SPLASH_DURATION)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, SPLASH_DURATION)
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogoItem, View.TRANSLATION_Y, 50f, -50f).apply {
            duration = SPLASH_DURATION
        }.start()
    }

    companion object {
        private const val SPLASH_DURATION = 1000L
    }
}