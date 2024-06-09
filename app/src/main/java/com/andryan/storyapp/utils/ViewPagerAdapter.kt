package com.andryan.storyapp.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.andryan.storyapp.ui.fragment.login.LoginFragment
import com.andryan.storyapp.ui.fragment.register.RegisterFragment

@ExperimentalPagingApi
class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = RegisterFragment()
            1 -> fragment = LoginFragment()
        }
        return fragment as Fragment
    }
}