package com.example.testing4.views.fragments

import android.os.Bundle
import android.util.Log
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
import com.stripe.Stripe
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var viewModel: ViewModel
    private lateinit var repo: Repo
    private lateinit var db: Database
    private lateinit var cartRvAdapter: CartRvAdapter
    private val dataStore by lazy { DataStoreManager(requireContext()) }
    private var userID: String = ""
    private lateinit var stripe: Stripe
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var paymentSheetCustomerConfig: PaymentSheet.CustomerConfiguration
    private lateinit var clientSecret: String

    private var cartItems = ArrayList<ProductsItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        db = DataBaseProvider.getInstance(requireContext())
        repo = Repo(RetrofitInstance.retroFitApi, db.dbDao)
        viewModel = ViewModelProvider(this, Factory(repo, dataStore))[ViewModel::class.java]
        Stripe.apiKey = "sk_test_51RR8KeH8H0Wz0viAgeT61oQACK9ue1Skzk2rq2yCLVy63VUy15Uvld5uZDQr3IF6X1EPBTAPRFFLE2EMz5bpepCJ00NAd8Gahq"
        PaymentConfiguration.init(
            requireActivity().applicationContext,
            "pk_test_51RR8KeH8H0Wz0viAb67HBIuEzN607G8ATcrCssZw5zyFp07g2biEURE3EATeDKdSLTdONDA281HtsUEzxZ5lDC6h00CZSXjQEK"
        )

        binding.topay.setOnClickListener {
//            createPaymentIntent()
        }

        binding.payBtn.setOnClickListener {
           /* PaymentConfiguration.init(
                requireActivity().applicationContext,
                "sk_test_51RR8KeH8H0Wz0viAb67HBIuEzN607G8ATcrCssZw5zyFp07g2biEURE3EATeDKdSLTdONDA281HtsUEzxZ5lDC6h00CZSXjQEK" // Replace with your publishable key
            )

            // Assume clientSecret is passed securely from your backend
            paymentSheet.presentWithPaymentIntent(
                "sk_test_51RR8KeH8H0Wz0viAgeT61oQACK9ue1Skzk2rq2yCLVy63VUy15Uvld5uZDQr3IF6X1EPBTAPRFFLE2EMz5bpepCJ00NAd8Gahq",
                PaymentSheet.Configuration(
                    merchantDisplayName = "Vybeshop",
                    allowsDelayedPaymentMethods = true
                )
            )*/

            createPaymentIntent()

        }
        return binding.root


    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
        when (paymentSheetResult) {
            is PaymentSheetResult.Completed -> {
                Toast.makeText(requireContext(), "Payment successful!", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(requireContext(), "Payment canceled!", Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(requireContext(), "Payment failed: ${paymentSheetResult.error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /*fun createPaymentIntent() {
        Stripe.apiKey = "sk_test_51RR8KeH8H0Wz0viAgeT61oQACK9ue1Skzk2rq2yCLVy63VUy15Uvld5uZDQr3IF6X1EPBTAPRFFLE2EMz5bpepCJ00NAd8Gahq"

        lifecycleScope.launch {
            try {
                val params = PaymentIntentCreateParams.builder()
                    .setAmount(2000L)
                    .setCurrency("usd")
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .build()
                    )
                    .build()

                val paymentIntent = PaymentIntent.create(params)
                Log.d("TAG", "createPaymentIntent: $paymentIntent")
            }
            catch (e: Exception){
                e.printStackTrace()
                Log.d("TAG", "createPaymentIntent: ${e.message}")
            }
        }




    }*/

    private fun createPaymentIntent() {
        lifecycleScope.launch {
            try {
                val paymentIntent = withContext(Dispatchers.IO) {
                    val params = PaymentIntentCreateParams.builder()
                        .setAmount(8000L)
                        .setCurrency("usd")
                        .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                        )
                        .build()

                    PaymentIntent.create(params)
                }

                clientSecret = paymentIntent.clientSecret ?: throw Exception("Client secret is null")
                Log.d("PaymentIntent", "Created: $clientSecret")

                // Launch PaymentSheet now that clientSecret is ready
                paymentSheet.presentWithPaymentIntent(
                    clientSecret,
                    PaymentSheet.Configuration("Vybeshop")
                )

            } catch (e: Exception) {
                Log.e("PaymentIntent", "Error: ${e.message}", e)
            }
        }
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
        var delivery = 0.50

        items.forEach {
            val price = it.price?.toDouble() ?: 0.0
            val quantity = it.quantity ?: 1
            itemTotal += (price*85) * quantity
        }

        val gstAmount = (itemTotal * gstRate).toFloat()
        if (itemTotal >= 300){
            delivery == 0.0
            binding.textView2.visibility = View.GONE
        }
        else{
            delivery = itemTotal*0.3
            binding.textView2.visibility = View.VISIBLE
            binding.textView2.text = "Add items worth more then ₹ ${300-itemTotal}"
        }

        val totalAmount = (itemTotal + gstAmount + delivery).toFloat()

        binding.apply {
            amount.text = itemTotal.toString()
            gstTv.text = gstAmount.toString()
            totalPriceTv.text = "₹ ${totalAmount.toString()}"
            deliveyTv.text = if (delivery == 0.0) "Free" else "$delivery"
            billAmount.text = "₹ ${totalAmount.toString()}"
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
