package com.example.testing4.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.testing4.R
import com.example.testing4.adapters.recyclerviewadapters.CategoryProductRV_Adapter
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.clicklisteners.OnItemClickListenerDetails
import com.example.testing4.databinding.FragmentCategoryBinding
import com.example.testing4.factory.Factory
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.models.resource.Result
import com.example.testing4.repo.Repo
import com.example.testing4.utils.Loader
import com.example.testing4.viewmodels.ViewModel


class CategoryFragment : Fragment(), OnItemClickListenerDetails {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var myAdapter: CategoryProductRV_Adapter
    private val productItems = ArrayList<ProductsItem>()
    private lateinit var item: String
    private var filteredItems = ArrayList<ProductsItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item = arguments?.getString("name") ?: ""
        setUpRecyclerView()
        observeData()

        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int, after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?, start: Int, before: Int, count: Int
            ) {}

            override fun afterTextChanged(s: Editable?) {
                val query = binding.searchEditText.text.toString().lowercase().trim()
                filterProducts(query)
            }

        })

    }

    private fun filterProducts(query: String) {
        filteredItems.clear()
        filteredItems.addAll(
            if (query.isEmpty()) {
            productItems.filter { it.category.name == item }
        } else {
            productItems.filter { it.title.lowercase().trim().contains(query) }
        })
        myAdapter.updateList(filteredItems.toList())
    }

    private fun setUpRecyclerView() {
        myAdapter = CategoryProductRV_Adapter(productItems, this)
        binding.categoryRV2.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.categoryRV2.adapter = myAdapter
    }

    private fun observeData() {
        val repo = Repo(RetrofitInstance.retroFitApi)
        val viewModel = ViewModelProvider(this, Factory(repo))[ViewModel::class.java]

        viewModel.products.observe(viewLifecycleOwner) { resource ->
            when (resource.result) {
                Result.LOADING -> Loader.showDialog(requireContext())
                Result.SUCCESS -> {
                    Loader.hideDialog()
                    productItems.clear()
                    resource.data?.let { products ->
                        productItems.addAll(products)
                        filterProducts(binding.searchEditText.text.toString())
                    }
                }

                Result.FAILURE -> {
                    Loader.hideDialog()
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.getProducts()
    }

    override fun onClickForDetails(id: Int) {
        val bundle = Bundle().apply {
            putInt("id", id)
        }
        findNavController().navigate(R.id.detailsFragment, bundle)
    }
}
