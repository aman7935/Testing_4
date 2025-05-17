package com.example.testing4.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testing4.models.category.Category
import com.example.testing4.repo.Repo
import androidx.lifecycle.viewModelScope
import com.example.testing4.models.entities.ProductCart
import com.example.testing4.models.entities.ProductItemsEntity
import com.example.testing4.models.entities.UserAddress
import com.example.testing4.models.product.Products
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.models.resource.Resource
import com.example.testing4.views.auth.userId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ViewModel(private val repo: Repo) : ViewModel() {
    private val _category = MutableLiveData<Resource<Category>>()
    val category: LiveData<Resource<Category>> get() = _category

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            _category.postValue(Resource.loading(null, null))
            try {
                val response = repo.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    _category.postValue(
                        Resource.success(
                            response.body(),
                            "Products fetched successfully"
                        )
                    )
                } else _category.postValue(Resource.failure(null, "Failed to fetch products"))
            } catch (e: Exception) {
                _category.postValue(Resource.failure(null, e.message ?: "Unknown error occurred"))
            }
        }
    }

    private val _products = MutableLiveData<Resource<Products>>()
    val products: LiveData<Resource<Products>> get() = _products

    fun getProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            _products.postValue(Resource.loading(null, null))
            try {
                val response = repo.getProducts()
                if (response.isSuccessful && response.body() != null) {
                    delay(700)
                    _products.postValue(
                        Resource.success(
                            response.body(),
                            "Products fetched successfully"
                        )
                    )
                } else _products.postValue(Resource.failure(null, "Failed to fetch products"))
            } catch (e: Exception) {
                _products.postValue(Resource.failure(null, e.message ?: "Unknown error occurred"))
            }
        }
    }

    private val _productById = MutableLiveData<Resource<ProductsItem>>()
    val productById: LiveData<Resource<ProductsItem>> get() = _productById

    fun getProductByID(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _productById.postValue(Resource.loading(null, null))
            try {
                val response = repo.getProductByID(id)
                if (response.isSuccessful && response.body() != null) {
                    delay(700)
                    _productById.postValue(
                        Resource.success(response.body(), "Product fetched by id successfully")
                    )
                } else _productById.postValue(Resource.failure(null, "Failed to fetch the data"))
            } catch (e: Exception) {
                _productById.postValue(
                    Resource.failure(
                        null,
                        e.message ?: "unknown error occurred"
                    )
                )
            }
        }
    }

    fun saveToFavorites(product: ProductsItem) {
        viewModelScope.launch {
            repo.insertProduct(product)
        }
    }


    private val _favorites = MutableLiveData<Resource<List<ProductItemsEntity>>>()
    val favorites: LiveData<Resource<List<ProductItemsEntity>>> get() = _favorites

    fun getAllFavorites(userId: String) {
        _favorites.postValue(Resource.loading(null, null))

        val source = repo.getAllProducts(userId)

        source.observeForever { data ->
            _favorites.postValue(Resource.success(data, "Favorites fetched successfully"))
        }
    }

    fun deleteFromFavorites(productID: Int, userId: String) {
        viewModelScope.launch {
            repo.deleteProduct(productID, userId)
        }
    }

    fun saveInCart(id: Int, productCart: ProductCart) {
        viewModelScope.launch(Dispatchers.IO) {
            val products = ProductCart(
                userId = userId,
                pID = id,
                products = productCart.products,
                quantity = productCart.quantity
            )
            repo.saveInCart(products)
        }
    }


    //cart
    private val _cartItems = MutableLiveData<Resource<List<ProductCart>>>()
    val cartItems: LiveData<Resource<List<ProductCart>>> get() = _cartItems

    fun getAllCartItems(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _cartItems.postValue(Resource.loading(null, null))
            val cartItemsList = repo.getAllCartItems(userId)
            _cartItems.postValue(Resource.success(cartItemsList, "Cart items fetched successfully"))
        }
    }

    fun deleteFromCart(pID: Int, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFromCart(pID, userId)
        }
    }

    fun incrementQuantity(pID: Int, userId: String) {
        viewModelScope.launch {
            repo.incrementQuantity(pID, userId)
        }
    }

    fun decrementQuantity(pID: Int, userId: String) {
        viewModelScope.launch {
            repo.decrementQuantity(pID, userId)
        }
    }

    //userAddresses
                                              //(callbacks)app stays smooth and knows when the job is done without wasting time.
    fun saveAddress(userAddress: UserAddress, onResult : (Boolean) -> Unit) {
        viewModelScope.launch {
            val saved = repo.saveLocationIfNotExists(userAddress)
            onResult(saved)
        }   

    }
}