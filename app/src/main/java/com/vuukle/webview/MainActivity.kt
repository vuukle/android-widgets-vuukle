package com.vuukle.webview

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vuukle.webview.constants.VuukleConstants
import com.vuukle.webview.sample.SampleActivity
import com.vuukle.webview.viewpager.ViewPagerActivity
import com.vuukle.webview.viewpager_and_nestedscrollview.ViewPagerAndNestedScrollView

class MainActivity : AppCompatActivity() {

    companion object {
        const val URL = "url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        findViewById<Button>(R.id.share_bar_button).setOnClickListener {
            val intent = Intent(this, SampleActivity::class.java)
            intent.putExtra(URL,VuukleConstants.SHARE_BAR_URL)
            startActivity(intent)
        }
        findViewById<Button>(R.id.comments_button).setOnClickListener {
            val intent = Intent(this, SampleActivity::class.java)
            intent.putExtra(URL,VuukleConstants.COMMENTS_URL)
            startActivity(intent)
        }
        findViewById<Button>(R.id.quizly_button).setOnClickListener {
            val intent = Intent(this, SampleActivity::class.java)
            intent.putExtra(URL,VuukleConstants.QUIZLY_URL)
            startActivity(intent)
        }
        findViewById<Button>(R.id.view_pager_button).setOnClickListener {
            startActivity(Intent(this, ViewPagerActivity::class.java))
        }
        findViewById<Button>(R.id.view_pager_plus_nested_scroll_view_button).setOnClickListener {
            startActivity(Intent(this, ViewPagerAndNestedScrollView::class.java))
        }
    }
}