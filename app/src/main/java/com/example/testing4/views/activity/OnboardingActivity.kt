package com.example.testing4.views.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.testing4.R
import com.example.testing4.adapters.viewpageradapters.ViewPagerAdapter
import com.example.testing4.databinding.ActivityOnboardingBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.models.OnboardingItemModel
import com.example.testing4.views.auth.LoginActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val dataStore by lazy { DataStoreManager(applicationContext) }

    private val viewPagerList = listOf(
        OnboardingItemModel(R.drawable.component_1),
        OnboardingItemModel(R.drawable.component_2),
        OnboardingItemModel(R.drawable.componen_3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPagerAdapter = ViewPagerAdapter(viewPagerList)
        binding.viewPager.adapter = viewPagerAdapter


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUI(position)
            }
        })

        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < viewPagerList.lastIndex) {
                binding.viewPager.currentItem = current + 1
            }
            else{
                lifecycleScope.launch {
                    dataStore.saveOnboardingComplete(true)

                    dataStore.onBoardingCompleted.first().let {
                        Log.d("onBoarding", "onboarding: $it")
                    }

                    startActivity(Intent(this@OnboardingActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }

        binding.btnPrev.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current > 0) {
                binding.viewPager.currentItem = current - 1
            }
        }

    }
    private fun updateUI(position: Int) {
        binding.btnPrev.visibility = if (position > 0) View.VISIBLE else View.INVISIBLE
        binding.btnNext.text = if (position == viewPagerList.lastIndex) "Get Started" else "Next"

    }
}