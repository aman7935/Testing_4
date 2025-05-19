package com.example.testing4.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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
import com.example.testing4.factory.Factory
import com.example.testing4.models.entities.ProductCart
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.repo.Repo
import com.example.testing4.viewmodels.ViewModel
import com.example.testing4.views.auth.userId

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var viewModel: ViewModel
    private lateinit var repo: Repo
    private lateinit var db: Database
    private lateinit var cartRvAdapter: CartRvAdapter

    var cartItems= ArrayList<ProductsItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        db = DataBaseProvider.getInstance(requireContext())
        repo = Repo(RetrofitInstance.retroFitApi, db.dbDao)
        viewModel = ViewModelProvider(this, Factory(repo))[ViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        viewModel.getAllCartItems(userId)
        observeData()
        val address = arguments?.getString("address")

        binding.change.setOnClickListener {
            findNavController().navigate(R.id.addressFragment)
        }
        binding.address.text = address
    }

    private fun calculateBillDetails(){
        var itemTotal = 0.0
        var gstRate = 0.05
        val delivery = .50

        cartItems.forEach {
            var price = it.price?.toDouble() ?: 0.0
            var quantity = it.quantity ?: 1
            itemTotal += price * quantity
        }
        val gstAmount = (itemTotal * gstRate).toFloat()

        val deliveryCharges  = if (itemTotal >= 300) 0.0 else delivery

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
        cartRvAdapter =
            CartRvAdapter(
                cartItems,
                onItemClickDeleteCart = object : OnItemClickDeleteCart {
                    override fun onclickDelete(item: ProductsItem) {
                        viewModel.deleteFromCart(item.id, userId)
                    }
                }, onClickIncrement = object : OnClickIncrement {
                    override fun onClickIncrement(itemID: ProductsItem) {
                        viewModel.incrementQuantity(itemID.id, userId)
                    }
                }, onClickDecrement = object : OnClickDecrement {
                    override fun onClickDecrement(itemID: ProductsItem) {
                        viewModel.decrementQuantity(itemID.id, userId)
                    }
                },
                calculateBillDetails = { calculateBillDetails() }
            )


        binding.cartRV.adapter = cartRvAdapter
        binding.cartRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun observeData() {



        viewModel.cartItems.observe(viewLifecycleOwner) { productCartList ->
            val cartProducts = productCartList.data ?: emptyList()
            cartItems = ArrayList(cartProducts.map {
                it.products.apply { quantity = it.quantity }
            })
            cartRvAdapter.updateList(cartItems)
        }
    }
}
