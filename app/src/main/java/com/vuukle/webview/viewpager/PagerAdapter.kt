package com.vuukle.webview.viewpager

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private var fragments = ArrayList<Fragment>()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFragments(vararg mFragments: Fragment) {
        fragments.addAll(mFragments)
        notifyDataSetChanged()
    }
}