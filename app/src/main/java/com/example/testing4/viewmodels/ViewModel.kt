package com.example.testing4.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testing4.models.category.Category
import com.example.testing4.repo.Repo
import androidx.lifecycle.viewModelScope
import com.example.testing4.models.product.Products
import com.example.testing4.models.product.ProductsItem
import com.example.testing4.models.resource.Resource
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
    val productById : LiveData<Resource<ProductsItem>> get() = _productById

    fun getProductByID(id : Int ){
        viewModelScope.launch(Dispatchers.IO) {
            _productById.postValue(Resource.loading(null, null))
            try {
                val response = repo.getProductByID(id)
                if (response.isSuccessful && response.body() != null){
                    delay(700)
                    _productById.postValue(
                        Resource.success(response.body(), "Product fetched by id successfully")
                    )
                }else _productById.postValue(Resource.failure(null, "Failed to fetch the data"))
            }catch (e : Exception) {
                _productById.postValue(Resource.failure(null, e.message ?: "unknown error occurred"))
            }
        }
    }
}