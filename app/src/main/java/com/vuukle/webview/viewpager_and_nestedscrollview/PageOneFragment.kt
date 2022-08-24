package com.vuukle.webview.viewpager_and_nestedscrollview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vuukle.sdk.VuukleManager
import com.vuukle.sdk.exeptions.VuukleException
import com.vuukle.sdk.listeners.VuukleErrorListener
import com.vuukle.sdk.listeners.VuukleViewEventListener
import com.vuukle.sdk.widget.VuukleView
import com.vuukle.webview.R
import com.vuukle.webview.constants.VuukleConstants
import com.vuukle.webview.widget.SmartNestedScrollView

class PageOneFragment : Fragment() {

    private lateinit var commentsView: VuukleView
    private lateinit var binding: View
    private lateinit var vuukleManager: VuukleManager

    private lateinit var smartNestedScrollView: SmartNestedScrollView

    private var disableNestedScrollView = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        this.binding = inflater.inflate(R.layout.fragment_page_one_nested_example, container, false)
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initVuukleManager()
    }

    private fun initViews() {

        smartNestedScrollView = binding.findViewById(R.id.smartNestedScrollView)
        commentsView = binding.findViewById(R.id.commentsView)

        commentsView.setVuukleViewEventListener(object : VuukleViewEventListener {
            var disableScroll = false
            override fun onTouch(view: VuukleView, ev: MotionEvent?) {
                when (ev?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        disableScroll = true
                        disableNestedScrollView = true
                        smartNestedScrollView.requestDisallowInterceptTouchEvent(
                            disableNestedScrollView
                        )
                    }
                    MotionEvent.ACTION_MOVE -> {
                        smartNestedScrollView.requestDisallowInterceptTouchEvent(
                            disableNestedScrollView
                        )
                        if (!disableScroll) {
                            smartNestedScrollView.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    else -> {
                        disableScroll = false
                        disableNestedScrollView = false
                        smartNestedScrollView.requestDisallowInterceptTouchEvent(
                            disableNestedScrollView
                        )
                    }
                }
                super.onTouch(view, ev)
            }

            override fun onOverScroll(
                view: VuukleView,
                scrollX: Int,
                scrollY: Int,
                clampedX: Boolean,
                clampedY: Boolean,
            ) {
                super.onOverScroll(view, scrollX, scrollY, clampedX, clampedY)
                disableNestedScrollView = !clampedY
                disableScroll = !clampedX
            }
        })
    }

    private fun initVuukleManager() {

        // Create Vuukle manager instance
        vuukleManager = VuukleManager.Builder(this).build()

        // Error handling
        vuukleManager.addErrorListener(object : VuukleErrorListener {
            override fun onError(exception: VuukleException) {
                Toast.makeText(requireActivity(), exception.message.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        })

        // load comments in VuukleView
        vuukleManager.load(commentsView, VuukleConstants.COMMENTS_URL)
    }
}