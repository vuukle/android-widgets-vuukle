package com.vuukle.webview.viewpager

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.vuukle.sdk.VuukleManager
import com.vuukle.webview.R

class ViewPagerActivity : FragmentActivity() {

    private var mPager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        VuukleManager.init(this)
        initViewPager()
    }

    private fun initViewPager() {
        mPager = findViewById(R.id.view_pager)
        // The pager adapter, which provides the pages to the view pager widget.
        val pagerAdapter = PagerAdapter(this)
        pagerAdapter.setFragments(PageOneFragment(), PageTwoFragment())
        mPager?.adapter = pagerAdapter
    }
}