package com.example.testing4.views.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.example.testing4.R
import com.example.testing4.databinding.ActivityMainBinding
import com.example.testing4.utils.ConstValues.BROADCAST_ACTION
import com.example.testing4.utils.ConstValues.EXTRA_MESSAGE
import com.example.testing4.utils.ViewUtils
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController


        setUpBottomNav()
        observeDestinationChanges()
    }



    private fun setUpBottomNav() {
        val layoutMap = mapOf(
            binding.homeIconLayout to R.id.homeFragment,
            binding.categoryLayout to R.id.categoryFragment,
            binding.favoritesLayout to R.id.favoritesFragment,
            binding.youLayout to R.id.youFragment,
            binding.ordersLayout to R.id.ordersFragment
        )

        layoutMap.forEach { (layout, destinationId) ->
            layout.setOnClickListener {
                if (navController.currentDestination?.id != destinationId) {
                    navController.navigate(destinationId, null,
                        navOptions {
                            launchSingleTop = true
                            popUpTo(destinationId) {
                                inclusive = true
                            }
                        }
                    )
                }
            }
        }
    }

    private fun observeDestinationChanges() {
        val selectedColor = R.color.selected_icon_color
        val defaultColor = R.color.default_icon_color

        val viewMap = mapOf(
            R.id.homeFragment to Pair(binding.homeIcon, binding.homeTv),
            R.id.categoryFragment to Pair(binding.categoryIcon, binding.categoryTv),
            R.id.favoritesFragment to Pair(binding.favoritesIcon, binding.favoritesTv),
            R.id.youFragment to Pair(binding.youIcon, binding.youTv),
            R.id.ordersFragment to Pair(binding.ordersIcon, binding.ordersTv)
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewMap.forEach { (id, pair) ->
                val colorRes = if (id == destination.id) selectedColor else defaultColor
                ViewUtils.setIconColor(pair.first, colorRes, this)
                ViewUtils.setTextColor(pair.second, colorRes, this)
            }

            ViewUtils.setBackground(viewMap,this, destination.id)
        }
    }
}
