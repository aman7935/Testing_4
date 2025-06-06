package com.example.testing4.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.testing4.R
import com.example.testing4.adapters.viewpageradapters.DetailViewPagerAdapter
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.database.Database
import com.example.testing4.databinding.FragmentDetailsBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.factory.Factory
import com.example.testing4.models.resource.Result
import com.example.testing4.repo.Repo
import com.example.testing4.utils.Loader
import com.example.testing4.utils.ViewUtils
import com.example.testing4.viewmodels.ViewModel

class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var viewModel: ViewModel
    private var id: Int = -1
    private lateinit var myAdapter: DetailViewPagerAdapter
    private lateinit var db: Database
    private lateinit var repo: Repo
    private val d by lazy { DataStoreManager(requireContext()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        db = DataBaseProvider.getInstance(requireContext())
        repo = Repo(RetrofitInstance.retroFitApi, db.dbDao)
        viewModel = ViewModelProvider(this, Factory(repo, d))[ViewModel::class.java]
        observeData()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        id = arguments?.getInt("id") ?: -1

        if (id != -1) {
            viewModel.getProductByID(id)
        } else {
            Toast.makeText(requireContext(), "Product ID not found", Toast.LENGTH_SHORT).show()
        }

        binding.backbutton.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setUpViewPager(images: List<String>) {
        myAdapter = DetailViewPagerAdapter(images)
        binding.DetailViewPager.adapter = myAdapter
        binding.dotsIndicatorHomeViewpager.attachTo(binding.DetailViewPager)
    }

    private fun observeData() {
        viewModel.productById.observe(viewLifecycleOwner) { resource ->
            when (resource.result) {
                Result.LOADING -> {
                    Loader.showDialog(requireContext())
                    binding.mainDetailId.visibility = View.GONE
                }
                Result.SUCCESS -> {
                    Loader.hideDialog()
                    binding.mainDetailId.visibility = View.VISIBLE
                    resource.data?.let { product ->
                        val imageList = product.images
                        setUpViewPager(imageList)
                        binding.title.text = product.title
                        binding.productDesc.text = product.description
                        if (product.isFavourite == 1)ViewUtils.setIconColor(binding.heartIcon, R.color.like_color, requireContext())
                        else ViewUtils.setIconColor(binding.heartIcon, R.color.default_icon_color, requireContext())
                    }
                }
                Result.FAILURE -> {
                    Loader.hideDialog()
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
