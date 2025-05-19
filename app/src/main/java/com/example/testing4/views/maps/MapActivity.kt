package com.example.testing4.views.maps

import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import com.example.testing4.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.testing4.R
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.datastore.dataStore
import com.example.testing4.factory.Factory
import com.example.testing4.repo.Repo
import com.example.testing4.viewmodels.ViewModel
import com.example.testing4.views.fragments.BottomSheetFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: ViewModel
    private lateinit var  address : String
    private lateinit var city : String
    private lateinit var state : String
    private lateinit var latLng : LatLng
    private val dataStore by lazy { DataStoreManager(this) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DataBaseProvider.getInstance(this)
        val repo = Repo(RetrofitInstance.retroFitApi, db.dbDao)
        viewModel = ViewModelProvider(this, Factory(repo, dataStore))[ViewModel::class.java]

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveButton.setOnClickListener {
            val bundle = Bundle().apply {
                putString("city", city,)
                putString("state", state)
                putString("address", address)
                putParcelable("latLng", latLng)
            }
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap

        val lat = intent.getDoubleExtra("latitude", 0.0)
        val lng = intent.getDoubleExtra("longitude", 0.0)
        val userLocation = LatLng(lat, lng)

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))

        googleMap.setOnCameraIdleListener {
            val center = googleMap.cameraPosition.target
            fetchAddress(center)
        }
    }

    private fun fetchAddress(location: LatLng) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(this@MapActivity, Locale.getDefault())
                    val addressList = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )
                    val addressObj = addressList?.firstOrNull()
                    city = addressObj?.locality ?: "City not found"
                    state = addressObj?.adminArea ?: "State not found"
                    address = addressObj?.getAddressLine(0) ?: "Address not found"
                    latLng = location

                } catch (e: Exception) {
                    Toast.makeText(this@MapActivity, "Error fetching address: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            binding.addressText.text = address
        }
    }

}
