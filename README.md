### **Implement Vuukle SDK for Android**


   1 Add the JitPack repository to your build file
   ```kotlin 
         allprojects {
            repositories {
               maven { url 'https://jitpack.io' } 
            }
         }
   ```


   2 Include Vuukle Widget SDK dependency in **build.gradle**
   ```kotlin
         implementation 'com.github.vuukle:android-widgets-vuukle:1.0.8'
   ```

   3 Provides **Public & Private** keys for establish connection with vuukle api's in Application **onCreate** function.
   ```kotlin
          override fun onCreate() {
              super.onCreate()
              VuukleKeys.apply {
                  setPublicKey("664e0b85-5b2c-4881-ba64-************")
                  setPrivateKey("bd3a64e4-7e19-46b2-ae4d-************")
              }
          }
   ```


   4 Add permission in **AndroidManifest.xml**
   ```xml
         <uses-permission android:name="android.permission.INTERNET" />
   ```

   5 **Comment Widget**

   Define **VuukleView** in layout XML
   ```xml
        <com.vuukle.webview.widget.VuukleView
            android:id="@+id/commentsView"
            android:layout_width="match_parent"
            android:overScrollMode="never"
            android:layout_height="match_parent"
            android:scrollbars="none" />
   ```

   Initialize **VuukleManager** in activity onCreate() function
   ```kotlin 
        override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_view_pager)
           // Initialize vuukle manager
           VuukleManager.init(this)
        }
   ```


   Create **VuukleManager** instance and load. Add color parameter to the load function to allow for changing the background color if needed.
   ```kotlin
        private fun createVuukleManager() {
           // Create Vuukle manager instance
           vuukleManager = VuukleManager.Builder(LifecycleOwner).build()
   
           // Error handling
           vuukleManager.addErrorListener(object : VuukleErrorListener {
               override fun onError(exception: VuukleException) {
                   Toast.makeText(this@SampleActivity, exception.message.toString(), Toast.LENGTH_LONG).show()
               }
           })
   
           // load comments in VuukleView
           vuukleManager.load(commentsView, VuukleConstants.COMMENTS_URL, "#FFFF00")
   
           // Handle vuukle events
           // If you did not set event listener by default all events will be handled by VuukleManager
           vuukleManager.setEventListener(object : VuukleEventListener {
               override fun onNewEvent(event: VuukleEvent) {
                   when(event){
                       is VuukleEvent.TownTalkClickEvent -> { /* Handle */ }
                       is VuukleEvent.YouMindLikeClickEvent -> { /* Handle */ }
                   }
               }
           })
        }
   ```

   6 **Share Bar Widget**

   Define **VuukleView** in layout XML
   ```xml
        <com.vuukle.webview.widget.VuukleView
            android:id="@+id/shareBarView"
            android:layout_width="match_parent"
            android:overScrollMode="never"
            android:layout_height="match_parent"
            android:scrollbars="none" />
   ```

   Initialize **VuukleManager** in activity onCreate() function
   ```kotlin 
        override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_view_pager)
           // Initialize vuukle manager
           VuukleManager.init(this)
        }
   ```


   Create **VuukleManager** instance and load
   ```kotlin
        private fun createVuukleManager() {
           // Create Vuukle manager instance
           vuukleManager = VuukleManager.Builder(LifecycleOwner).build()
   
           // Error handling
           vuukleManager.addErrorListener(object : VuukleErrorListener {
               override fun onError(exception: VuukleException) {
                   Toast.makeText(this@SampleActivity, exception.message.toString(), Toast.LENGTH_LONG).show()
               }
           })
   
           // load shareBar in VuukleView
           vuukleManager.load(shareBarView, VuukleConstants.SHARE_BAR_URL)
   
           // Handle vuukle events
           // If you did not set event listener by default all events will be handled by VuukleManager
           vuukleManager.setEventListener(object : VuukleEventListener {
               override fun onNewEvent(event: VuukleEvent) {
                   when(event){
                       is VuukleEvent.TownTalkClickEvent -> { /* Handle */ }
                       is VuukleEvent.YouMindLikeClickEvent -> { /* Handle */ }
                   }
               }
           })
        }
   ```

   7 **Quizzly Widget**

   Define **VuukleView** in layout XML
   ```xml
        <com.vuukle.webview.widget.VuukleView
            android:id="@+id/quizzlyView"
            android:layout_width="match_parent"
            android:overScrollMode="never"
            android:layout_height="match_parent"
            android:scrollbars="none" />
   ```

   Initialize **VuukleManager** in activity onCreate() function
   ```kotlin 
        override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           setContentView(R.layout.activity_view_pager)
           // Initialize vuukle manager
           VuukleManager.init(this)
        }
   ```


   Create **VuukleManager** instance and load
   ```kotlin
        private fun createVuukleManager() {
           // Create Vuukle manager instance
           vuukleManager = VuukleManager.Builder(LifecycleOwner).build()
   
           // Error handling
           vuukleManager.addErrorListener(object : VuukleErrorListener {
               override fun onError(exception: VuukleException) {
                   Toast.makeText(this@SampleActivity, exception.message.toString(), Toast.LENGTH_LONG).show()
               }
           })
   
           // load quizzly in VuukleView
           vuukleManager.load(quizzlyView, VuukleConstants.QUIZZLY_URL)
   
           // Handle vuukle events
           // If you did not set event listener by default all events will be handled by VuukleManager
           vuukleManager.setEventListener(object : VuukleEventListener {
               override fun onNewEvent(event: VuukleEvent) {
                   when(event){
                       is VuukleEvent.TownTalkClickEvent -> { /* Handle */ }
                       is VuukleEvent.YouMindLikeClickEvent -> { /* Handle */ }
                   }
               }
           })
        }
   ```


   8 SSO  functionality
   ```kotlin
        private fun initSSOFunctionality() {
           // Add custom SSO listener
           vuukleManager.addCustomSSOEventListener(object : SSOEventListener {
               override fun onSignInClicked() {
                   // Overriding SSO Sign in button click
               }
           })
           // Handle on login click
           loginSSOButton.setOnClickListener {
               // Login user by SSO using token
               vuukleManager.loginBySSO("token here")
           }
           // Handle on logout click
           logoutSSOButton.setOnClickListener {
               // Logout user
               vuukleManager.logout()
           }
        }
   ```


   9 Include Facebook SDK for Facebook functionality

   9.1  implementations to **build.gradle**
   ```kotlin
        implementation 'com.facebook.android:facebook-login:12.0.0'
        implementation 'com.facebook.android:facebook-share:12.0.0'
   ```

   9.2 add **facebook_app_id** and **facebook_client_token** in **strings.xml**
   ```xml
      <string name="facebook_app_id">20036321******</string>
      <string name="facebook_client_token">e9153a0f8194d413**************</string>
   ```

   To get your app_id and client_token check  [Facebook documentation](https://developers.facebook.com/docs/facebook-login/android/)

   9.3 add meta-data in **AndroidManifest.xml**
   ```xml
      <meta-data
       android:name="com.facebook.sdk.ApplicationId"
       android:value="@string/facebook_app_id" />
      
      <meta-data
       android:name="com.facebook.sdk.ClientToken"
       android:value="@string/facebook_client_token"/>
   ```

   9.4 add **FacebookContentProvider** in **AndroidManifest.xml**
   ```xml
      <provider
           android:name="com.facebook.FacebookContentProvider"
           android:authorities="com.facebook.app.FacebookContentProvider[facebook_app_id]"
          android:exported="false" />
   ```