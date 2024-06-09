package com.andryan.storyapp.ui.activity.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.databinding.ActivityWelcomeBinding
import com.andryan.storyapp.ui.activity.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.welcome) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()

        binding.buttonToAuth.setOnClickListener {
            val intent = Intent(this@WelcomeActivity, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun playAnimation() {
        val tvWelcomeTitle =
            ObjectAnimator.ofFloat(binding.tvWelcomeTitle, View.ALPHA, 1f).setDuration(100)
        val tvWelcomeDesc =
            ObjectAnimator.ofFloat(binding.tvWelcomeDesc, View.ALPHA, 1f).setDuration(100)
        val buttonToAuth =
            ObjectAnimator.ofFloat(binding.buttonToAuth, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                tvWelcomeTitle,
                tvWelcomeDesc,
                buttonToAuth
            )
            startDelay = 100
        }.start()
    }
}