package com.dpito.githubuser.view.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dpito.githubuser.view.FollowersFragment
import com.dpito.githubuser.view.FollowingFragment

class FollowTabAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity)  {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = FollowersFragment()
            1 -> fragment = FollowingFragment()
        }
        return fragment as Fragment
    }
}