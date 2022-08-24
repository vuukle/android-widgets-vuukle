package com.vuukle.webview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vuukle.webview.sample.SampleActivity
import com.vuukle.webview.viewpager.ViewPagerActivity
import com.vuukle.webview.viewpager_and_nestedscrollview.ViewPagerAndNestedScrollView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        findViewById<Button>(R.id.sample_button).setOnClickListener {
            startActivity(Intent(this, SampleActivity::class.java))
        }
        findViewById<Button>(R.id.view_pager_button).setOnClickListener {
            startActivity(Intent(this, ViewPagerActivity::class.java))
        }
        findViewById<Button>(R.id.view_pager_plus_nested_scroll_view_button).setOnClickListener {
            startActivity(Intent(this, ViewPagerAndNestedScrollView::class.java))
        }
    }
}