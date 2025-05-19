package com.example.testing4.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testing4.R
import com.example.testing4.adapters.recyclerviewadapters.CartRvAdapter
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.clicklisteners.OnClickDecrement
import com.example.testing4.clicklisteners.OnClickIncrement
import com.example.testing4.clicklisteners.OnItemClickDeleteCart
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.database.Database
import com.example.testing4.databinding.FragmentCartBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.factory.Factory
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.models.resource.Resource
import com.example.testing4.models.resource.Result
import com.example.testing4.repo.Repo
import com.example.testing4.utils.Loader
import com.example.testing4.viewmodels.ViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var viewModel: ViewModel
    private lateinit var repo: Repo
    private lateinit var db: Database
    private lateinit var cartRvAdapter: CartRvAdapter
    private val dataStore by lazy { DataStoreManager(requireContext()) }
    private var userID: String = ""

    private var cartItems = ArrayList<ProductsItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        db = DataBaseProvider.getInstance(requireContext())
        repo = Repo(RetrofitInstance.retroFitApi, db.dbDao)
        viewModel = ViewModelProvider(this, Factory(repo, dataStore))[ViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            userID = dataStore.getUserId.first()
            viewModel.getAllCartItems(userID)

            viewModel.getDefaultAddressByUserId(userID) {
                it?.let {
                    binding.address.text = it.address
                }
            }
        }

        setUpRecyclerView()
        observeData()

        val address = arguments?.getString("address")
        binding.change.setOnClickListener {
            findNavController().navigate(R.id.addressFragment)
        }
    }

    private fun calculateBillDetails(items: List<ProductsItem>) {
        var itemTotal = 0.0
        val gstRate = 0.05
        val delivery = 0.50

        items.forEach {
            val price = it.price?.toDouble() ?: 0.0
            val quantity = it.quantity ?: 1
            itemTotal += price * quantity
        }

        val gstAmount = (itemTotal * gstRate).toFloat()
        val deliveryCharges = if (itemTotal >= 300) 0.0 else delivery
        val totalAmount = (itemTotal + gstAmount + deliveryCharges).toFloat()

        binding.apply {
            amount.text = itemTotal.toString()
            gstTv.text = gstAmount.toString()
            totalPriceTv.text = totalAmount.toString()
            deliveyTv.text = if (deliveryCharges == 0.0) "Free" else "Free above shopping of 300"
            billAmount.text = totalAmount.toString()
        }
    }

    private fun setUpRecyclerView() {
        cartRvAdapter = CartRvAdapter(
            cartItems,
            onItemClickDeleteCart = object : OnItemClickDeleteCart {
                override fun onclickDelete(item: ProductsItem) {
                    Loader.showDialog(requireContext())
                    viewModel.deleteFromCart(item.id, userID)
                }
            },
            onClickIncrement = object : OnClickIncrement {
                override fun onClickIncrement(itemID: ProductsItem) {
                    viewModel.incrementQuantity(itemID.id, userID)
                }
            },
            onClickDecrement = object : OnClickDecrement {
                override fun onClickDecrement(itemID: ProductsItem) {
                    viewModel.decrementQuantity(itemID.id, userID)
                }
            },
            calculateBillDetails = { calculateBillDetails(cartItems) }
        )

        binding.cartRV.adapter = cartRvAdapter
        binding.cartRV.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() {
        viewModel.cartItems.observe(viewLifecycleOwner) { resource ->
            when (resource.result) {
                Result.LOADING -> {
                    Loader.showDialog(requireContext())
                }
                Result.SUCCESS -> {
                    Loader.hideDialog()
                    val cartProducts = resource.data?.map {
                        it.products.apply { quantity = it.quantity }
                    } ?: emptyList()
                    cartItems = ArrayList(cartProducts)
                    cartRvAdapter.updateList(cartItems)
                    calculateBillDetails(cartItems)
                    resource.message?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
                Result.FAILURE -> {
                    resource.message?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
