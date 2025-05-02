package com.example.testing4.views.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testing4.databinding.ActivitySplashBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.views.auth.LoginActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val dataStore by lazy { DataStoreManager(applicationContext) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(2000)

            val onboardingCompleted = dataStore.onBoardingCompleted.first()
            val loggedIn = dataStore.getLoggedIn.first()

            Log.d(TAG, "onCreate: ${onboardingCompleted} ${loggedIn}")

            val destination = if (!onboardingCompleted) { OnboardingActivity::class.java }
            else if(!loggedIn) { LoginActivity::class.java }
            else MainActivity::class.java

            startActivity(Intent(this@SplashActivity, destination))
            finish()
        }
    }
}