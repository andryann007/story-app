package com.andryan.storyapp.ui.activity.auth

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.widget.ViewPager2
import com.andryan.storyapp.R
import com.andryan.storyapp.databinding.ActivityAuthBinding
import com.andryan.storyapp.utils.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewPager(this@AuthActivity)
    }

    private fun setViewPager(activity: AppCompatActivity) {
        val viewPagerAdapter = ViewPagerAdapter(activity)
        val viewPager: ViewPager2 = binding.viewPager
        val tabs: TabLayout = binding.tabLayout

        viewPager.adapter = viewPagerAdapter

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_1,
            R.string.tab_2
        )
    }
}