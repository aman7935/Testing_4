package com.example.testing4.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.testing4.adapters.recyclerviewadapters.FavouritesAdapter
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.clicklisteners.OnItemClickDelete
import com.example.testing4.clicklisteners.OnItemClickListenerDetails
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.database.Database
import com.example.testing4.databinding.FragmentFavoritesBinding
import com.example.testing4.factory.Factory
import com.example.testing4.models.entities.ProductItemsEntity
import com.example.testing4.models.resource.Result
import com.example.testing4.repo.Repo
import com.example.testing4.utils.Loader
import com.example.testing4.viewmodels.ViewModel

class FavoritesFragment : Fragment() {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: ViewModel
    private lateinit var repo: Repo
    private lateinit var db: Database
    private lateinit var myAdapter: FavouritesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DataBaseProvider.getInstance(requireContext())
        repo = Repo(RetrofitInstance.retroFitApi, db.dbDao)

        // Initialize the ViewModel
        viewModel = ViewModelProvider(this, Factory(repo)).get(ViewModel::class.java)

        observeData()
        setUpRecyclerView()
        viewModel.getAllFavorites()
    }

    private fun setUpRecyclerView() {
        myAdapter = FavouritesAdapter(
            emptyList(),
            onItemClickListenerDetails = object : OnItemClickListenerDetails {
                override fun onClickForDetails(id: Int) {
                    val bundle = Bundle().apply {
                        putInt("id", id)
                    }
                }
            },
            onItemClickDelete = object : OnItemClickDelete{
                override fun onClickDelete(item: ProductItemsEntity) {
                    viewModel.deleteFromFavorites(item.id)
                }

            }
        )
        binding.favouriteRV.adapter = myAdapter
    }
    private fun observeData() {
        viewModel.favorites.observe(viewLifecycleOwner) { it ->
            when (it.result) {
                Result.LOADING -> Loader.showDialog(requireContext())
                Result.SUCCESS -> {
                    Loader.hideDialog()
                    it.data?.let { products ->
                        myAdapter.updateList(products)
                    }
                }
                Result.FAILURE -> {
                    Loader.hideDialog()
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

