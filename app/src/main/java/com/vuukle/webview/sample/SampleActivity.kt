package com.vuukle.webview.sample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.vuukle.sdk.VuukleManager
import com.vuukle.sdk.constants.logger.LoggerConstants
import com.vuukle.sdk.exeptions.VuukleException
import com.vuukle.sdk.listeners.SSOEventListener
import com.vuukle.sdk.listeners.VuukleErrorListener
import com.vuukle.sdk.listeners.VuukleEventListener
import com.vuukle.sdk.model.VuukleEvent
import com.vuukle.sdk.widget.VuukleView
import com.vuukle.webview.MainActivity.Companion.URL
import com.vuukle.webview.R
import com.vuukle.webview.constants.VuukleConstants

class SampleActivity : AppCompatActivity() {

    private lateinit var vuukleManager: VuukleManager
    private lateinit var vuukleView: VuukleView
    private lateinit var loginSSOButton: Button
    private lateinit var logoutSSOButton: Button
    private var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_sample)
        getDataFromBundle()
        initViews()
        VuukleManager.init(this)
        createVuukleManager()
        if (url == VuukleConstants.COMMENTS_URL) {
            initSSOFunctionality()
        }
    }

    private fun getDataFromBundle() {
        url = intent.getStringExtra(URL) ?: VuukleConstants.QUIZLY_URL
    }

    private fun initViews() {
        vuukleView = findViewById(R.id.vuukleView)
        if (url == VuukleConstants.COMMENTS_URL) {
            createActionBar()
        }
    }

    private fun createActionBar() {
        val actionBar: ActionBar = supportActionBar ?: return
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setCustomView(R.layout.custom_action_bar_layout)
        loginSSOButton = actionBar.customView.findViewById(R.id.login_by_sso)
        logoutSSOButton = actionBar.customView.findViewById(R.id.logout_by_sso)
    }

    private fun createVuukleManager() {
        // Create Vuukle manager instance
        vuukleManager = VuukleManager.Builder(this).build()
        // Error handling
        vuukleManager.addErrorListener(object : VuukleErrorListener {
            override fun onError(exception: VuukleException) {
                Toast.makeText(this@SampleActivity, exception.message.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        })

        // load url in VuukleView
        vuukleManager.load(vuukleView, url)
        // Handle vuukle events
        // If you did not set event listener by default all events will be handled by VuukleManager
        vuukleManager.setEventListener(object : VuukleEventListener {
            override fun onNewEvent(event: VuukleEvent) {
                when (event) {
                    is VuukleEvent.TownTalkClickEvent -> {
                        /* Handle */
                        Log.i(LoggerConstants.VUUKLE_LOGGER, "TownTalkClickEvent")
                    }

                    is VuukleEvent.YouMindLikeClickEvent -> {
                        /* Handle */
                        Log.i(LoggerConstants.VUUKLE_LOGGER, "YouMindLikeClickEvent")
                    }
                }
            }
        })
    }

    private fun initSSOFunctionality() {
        // Add custom SSO listener
        vuukleManager.addCustomSSOEventListener(object : SSOEventListener {
            override fun onSignInClicked() {
                // Overriding SSO Sign in button click
            }
        })
        // Handle on login click
        loginSSOButton.setOnClickListener {
            // Login user by SSO using email and username
            vuukleManager.loginBySSO("test@gamil.com", "testUserName")
        }
        // Handle on logout click
        logoutSSOButton.setOnClickListener {
            // Logout user
            vuukleManager.logout()
        }
    }
}