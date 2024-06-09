package com.andryan.storyapp.ui.activity.detail

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.andryan.storyapp.BuildConfig
import com.andryan.storyapp.R
import com.andryan.storyapp.utils.Result
import com.andryan.storyapp.databinding.ActivityDetailBinding
import com.andryan.storyapp.utils.withDateFormat
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalPagingApi
@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()

    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra(resources.getString(R.string.extra_id)).toString()

        setActionBar(binding.toolbarDetail)
        getStoryDetail(id)
    }

    private fun getStoryDetail(id: String) {
        lifecycleScope.launch {
            val token = viewModel.getSession().first().token

            viewModel.getStoryDetail(token, id).observe(this@DetailActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.containerDetailStory.visibility = View.GONE
                        binding.lottieError.visibility = View.GONE
                    }

                    is Result.Success -> {
                        setLoadingState(isError = false)

                        val name = result.data.story.name
                        val date = result.data.story.createdAt.withDateFormat()
                        val description = result.data.story.description
                        val photoUrl = result.data.story.photoUrl

                        binding.tvDetailName.text = name
                        binding.tvDetailDate.text = date
                        binding.tvDetailDescription.text = description
                        Picasso.get().load(photoUrl).noFade().into(binding.ivDetailPhoto)
                    }

                    is Result.Error -> {
                        setLoadingState(isError = true)

                        binding.root.let {
                            Snackbar.make(
                                it,
                                result.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }

                        Timber.tag(TAG).d("getStoryDetail: %s", result.error)
                    }
                }
            }
        }
    }

    private fun setActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun setLoadingState(isError: Boolean) {
        if (!isError) {
            binding.containerDetailStory.visibility = View.VISIBLE
        } else {
            binding.lottieError.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)

                intent.apply {
                    type = resources.getString(R.string.text_format)
                    putExtra(Intent.EXTRA_TEXT, "${BuildConfig.BASE_URL}/${id}")
                }

                val chooser =
                    Intent.createChooser(intent, resources.getString(R.string.share_message))
                startActivity(chooser)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}