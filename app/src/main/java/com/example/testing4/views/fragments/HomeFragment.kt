package com.example.testing4.views.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.testing4.R
import com.example.testing4.adapters.recyclerviewadapters.CategoryRVAdapter
import com.example.testing4.adapters.recyclerviewadapters.ProductAdapter
import com.example.testing4.adapters.viewpageradapters.HomeScreenViewPagerAdapter
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.clicklisteners.OnItemClickListener
import com.example.testing4.clicklisteners.OnItemClickListenerDetails
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.database.Database
import com.example.testing4.databinding.FragmentHomeBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.factory.Factory
import com.example.testing4.models.category.CategoryItem
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.models.resource.Result
import com.example.testing4.models.viewpagermodels.HomeScreenViewPagerModel
import com.example.testing4.repo.Repo
import com.example.testing4.utils.ConstValues.BROADCAST_ACTION
import com.example.testing4.utils.ConstValues.EXTRA_MESSAGE
import com.example.testing4.utils.Loader
import com.example.testing4.viewmodels.ViewModel
import com.example.testing4.views.activity.MainActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeFragment : Fragment(){
    private lateinit var binding: FragmentHomeBinding
    private lateinit var categoryRVAdapter: CategoryRVAdapter
    private lateinit var viewModel: ViewModel
    private lateinit var productAdapter: ProductAdapter
    private lateinit var db: Database
    private val dataStore by lazy { DataStoreManager(requireContext()) }


    private val categoryItem = ArrayList<CategoryItem>()
    private val productItem = ArrayList<ProductsItem>()

    private var current = 0
    private var scrollJob: Job? = null


    private var isCategoryLoaded = false
    private var isProductLoaded = false


    private val viewPagerItems = listOf(
        HomeScreenViewPagerModel(R.drawable.banner_2),
        HomeScreenViewPagerModel(R.drawable.banner_3),
        HomeScreenViewPagerModel(R.drawable.banner_5),
        HomeScreenViewPagerModel(R.drawable.banner_4),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        db = DataBaseProvider.getInstance(requireContext())
        observeData()
        setUpRecyclerViewsAndViewPager()


        binding.cart.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }
        binding.ivNotification.setOnClickListener {  }
    }

    private fun setUpRecyclerViewsAndViewPager() {

        categoryRVAdapter = CategoryRVAdapter(categoryItem, onItemClickListener = object : OnItemClickListener {
            override fun onclick(name: String) {
                val bundle = Bundle().apply { putString("name", name) }
                findNavController().navigate(R.id.categoryFragment, bundle)
            }
        })
        binding.categoryRv.adapter = categoryRVAdapter

        binding.homeScreenViewPager.adapter = HomeScreenViewPagerAdapter(viewPagerItems)
        binding.dotsIndicatorHomeViewpager.attachTo(binding.homeScreenViewPager)
        
        productAdapter = ProductAdapter(productItem, onItemClickListenerDetails = object : OnItemClickListenerDetails {
            override fun onClickForDetails(id: Int) {
                val bundle = Bundle().apply { putInt("id", id) }
                findNavController().navigate(R.id.detailsFragment, bundle)
            }
        })
        binding.HomeScreenRecyclerViewCloth.adapter = productAdapter

        binding.homeScreenViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                current = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> scrollJob?.cancel()
                    ViewPager2.SCROLL_STATE_IDLE -> startAutoScrolling()
                }
            }
        })
    }

    private fun startAutoScrolling() {
        scrollJob?.cancel()
        scrollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(2000)
                current = (current + 1) % viewPagerItems.size
                binding.homeScreenViewPager.setCurrentItem(current, true)
            }
        }
    }

    private fun checkIfAllDataLoaded() {
        if (isCategoryLoaded && isProductLoaded) {
            Loader.hideDialog()
            startAutoScrolling()
            binding.linear3.visibility = View.VISIBLE
            binding.linearLayout5.visibility = View.VISIBLE
            binding.const2.visibility = View.VISIBLE
        }
    }

    private fun observeData() {
        val repo = Repo(RetrofitInstance.retroFitApi, db.dbDao)
        viewModel = ViewModelProvider(this, Factory(repo, dataStore))[ViewModel::class.java]

        isCategoryLoaded = false
        isProductLoaded = false


        viewModel.category.observe(viewLifecycleOwner) { resource ->
            when (resource.result) {
                Result.LOADING -> {
                    Loader.showDialog(requireContext())
                    binding.linear3.visibility = View.GONE
                }
                Result.SUCCESS -> {
                    categoryItem.clear()
                    resource.data?.let { categoryItem.addAll(it) }
                    categoryRVAdapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Category data loaded", Toast.LENGTH_SHORT).show()
                    isCategoryLoaded = true
                    checkIfAllDataLoaded()
                }
                Result.FAILURE -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    isCategoryLoaded = true
                    checkIfAllDataLoaded()
                }
            }
        }

        viewModel.products.observe(viewLifecycleOwner) { resource ->
            when (resource.result) {
                Result.LOADING -> {
                    binding.linear3.visibility = View.GONE
                    binding.linearLayout5.visibility = View.GONE
                }
                Result.SUCCESS -> {
                    productItem.clear()
                    val filteredProducts = resource.data?.filter { it.category.name == "Electronics" }
                    filteredProducts?.size
                    filteredProducts?.let { productItem.addAll(it) }
                    productAdapter.notifyDataSetChanged()
                    isProductLoaded = true
                    checkIfAllDataLoaded()
                }
                Result.FAILURE -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                    isProductLoaded = true
                    checkIfAllDataLoaded()
                }
            }
        }
        viewModel.getCategories()
        viewModel.getProducts()
    }


   /* private val messageReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == BROADCAST_ACTION){
                val message = intent.getStringExtra(EXTRA_MESSAGE)
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(requireContext(), "badfasfas", Toast.LENGTH_LONG).show()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(BROADCAST_ACTION)
        requireContext().registerReceiver(messageReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "onStop: ")
    }*/


    override fun onPause() {
        super.onPause()
        Loader.hideDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        scrollJob?.cancel()

    }



}