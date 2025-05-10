package com.example.testing4.views.maps

import android.os.Bundle
import com.example.testing4.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.appcompat.app.AppCompatActivity
import com.example.testing4.R

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        val lat = intent.getDoubleExtra("latitude", 0.0)
        val lng = intent.getDoubleExtra("longitude", 0.0)

        val userLocation = LatLng(lat, lng)
        googleMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
    }

}
