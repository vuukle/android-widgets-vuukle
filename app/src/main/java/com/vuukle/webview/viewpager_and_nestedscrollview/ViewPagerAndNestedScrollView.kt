package com.vuukle.webview.viewpager_and_nestedscrollview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.vuukle.sdk.VuukleManager
import com.vuukle.webview.R

class ViewPagerAndNestedScrollView : AppCompatActivity() {

    var mPager: ViewPager2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager_and_nested_scroll_view)
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