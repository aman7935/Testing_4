package com.example.testing4.views.maps

import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Bundle
import android.widget.Toast
import com.example.testing4.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.testing4.R
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.database.Database
import com.example.testing4.factory.Factory
import com.example.testing4.models.entities.UserAddress
import com.example.testing4.repo.Repo
import com.example.testing4.viewmodels.ViewModel
import com.example.testing4.views.auth.userId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DataBaseProvider.getInstance(this)
        val repo = Repo(RetrofitInstance.retroFitApi, db.dbDao)
        viewModel = ViewModelProvider(this, Factory(repo))[ViewModel::class.java]

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveButton.setOnClickListener {
            val center = googleMap.cameraPosition.target
            val address = binding.addressText.text.toString()

            val userAddress = UserAddress(
                userId = userId,
                latitude = center.latitude,
                longitude = center.longitude,
                address = address
            )

            viewModel.saveAddress(userAddress) { saved ->
                if (saved) Toast.makeText(this, "Address saved successfully", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "Address already exists", Toast.LENGTH_SHORT).show()
                finish()
            }
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
            val address = withContext(Dispatchers.IO) {
                try {
                    val geocoder = Geocoder(this@MapActivity, Locale.getDefault())
                    val addressList = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )
                    addressList?.firstOrNull()?.getAddressLine(0) ?: "Address not found"
                } catch (e: Exception) {
                    "Unable to find the address"
                }
            }
            binding.addressText.text = address
        }
    }
}
