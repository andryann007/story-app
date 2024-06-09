package com.andryan.storyapp.ui.activity.main

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.andryan.storyapp.R
import com.andryan.storyapp.databinding.ActivityMainBinding
import com.andryan.storyapp.ui.activity.maps.MapsActivity
import com.andryan.storyapp.ui.activity.upload.UploadActivity
import com.andryan.storyapp.ui.activity.welcome.WelcomeActivity
import com.andryan.storyapp.utils.LoadingStateAdapter
import com.andryan.storyapp.utils.StoryListAdapter
import com.andryan.storyapp.utils.getGreeting
import com.andryan.storyapp.utils.withUpperCase
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalPagingApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private var token: String = ""
    private lateinit var storyAdapter: StoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        greetingUser(binding.toolbarMain)

        token = runBlocking { viewModel.getSession().first().token }
        getAllStory(token)

        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this@MainActivity, UploadActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (storyAdapter.snapshot().isNotEmpty()) {
            storyAdapter.refresh()
        }
    }

    private fun greetingUser(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        lifecycleScope.launch {
            val name = viewModel.getSession().first().name

            supportActionBar?.apply {
                subtitle = getGreeting(this@MainActivity, name.withUpperCase())
            }
        }
    }

    private fun getAllStory(token: String) {
        storyAdapter = StoryListAdapter()
        val linearLayoutManager = LinearLayoutManager(this@MainActivity)
        val gridLayoutManager = GridLayoutManager(this@MainActivity, 2)

        binding.rvStoryList.apply {
            layoutManager =
                if (applicationContext.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    linearLayoutManager
                } else {
                    gridLayoutManager
                }
            adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }

        viewModel.isLoading.observe(this@MainActivity) {
            setLoadingState(it)
        }

        viewModel.getAllStory(token).observe(this@MainActivity) { result ->
            storyAdapter.submitData(lifecycle, result)
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressbarStory.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(this@MainActivity)
            .setTitle(resources.getString(R.string.logout_title))
            .setMessage(resources.getString(R.string.logout_message))
            .setNegativeButton(resources.getString(R.string.negative_button_text)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.positive_button_text)) { _, _ ->
                lifecycleScope.launch {
                    viewModel.logout()

                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.logout_success_message),
                        Toast.LENGTH_SHORT
                    ).show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(
                            Intent(
                                this@MainActivity,
                                WelcomeActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }, DELAY_TIME)
                }
            }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }

            R.id.action_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.action_logout -> {
                showLogoutConfirmationDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val DELAY_TIME = 1000L
    }
}