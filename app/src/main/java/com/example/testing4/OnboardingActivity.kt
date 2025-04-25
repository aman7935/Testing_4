package com.example.testing4

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.testing4.adapters.ViewPagerAdapter
import com.example.testing4.databinding.ActivityOnboardingBinding
import com.example.testing4.models.OnboardingItemModel

class OnboardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    val viewPagerList = listOf(
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