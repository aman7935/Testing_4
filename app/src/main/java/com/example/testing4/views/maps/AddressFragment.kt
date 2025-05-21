package com.example.testing4.views.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testing4.R
import com.example.testing4.adapters.recyclerviewadapters.AddressAdapter
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.clicklisteners.OnClickListenerForAddress
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.databinding.FragmentAddressBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.factory.Factory
import com.example.testing4.models.entities.UserAddress
import com.example.testing4.models.resource.Resource
import com.example.testing4.models.resource.Result
import com.example.testing4.repo.Repo
import com.example.testing4.utils.Loader
import com.example.testing4.viewmodels.ViewModel
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var geoCoder: Geocoder
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var viewModel: ViewModel
    private lateinit var address: String
    private lateinit var dataStore: DataStoreManager
    private lateinit var userID: String

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)

        val dbDao = DataBaseProvider.getInstance(requireContext()).dbDao
        val repo = Repo(RetrofitInstance.retroFitApi, dbDao)
        dataStore = DataStoreManager(requireContext())
        viewModel = ViewModelProvider(this, Factory(repo, dataStore))[ViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        geoCoder = Geocoder(requireContext(), Locale.getDefault())

        lifecycleScope.launch {
            userID = dataStore.getUserId.first()
            setUpRecyclerView()
            viewModel.getAddressById(userID)
            observeAddresses()
        }

        binding.addbtn.setOnClickListener {
            checkLocationAndPermission()
        }

        binding.saveBtn.setOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_to_cartFragment)
        }
    }

    private fun setUpRecyclerView() {
        addressAdapter = AddressAdapter(emptyList(), object : OnClickListenerForAddress {
            override fun onClickForAddress(list: UserAddress) {
                if (list.defaultAddress == 1) {
                    // Toggle off default address
                    list.defaultAddress = 0
                } else if (list.defaultAddress == 0) {
                    // Reset other default addresses and set new one
                    /*viewModel.resetDefaultAddress(userID)
                    viewModel.setDefaultAddressById(userID, list.id)*/
                    viewModel.makeDefaultAddress(list.id, list.userId)
                    viewModel.getAddressById(userID)

                }
            }
        })
        binding.addressRV.layoutManager = LinearLayoutManager(requireContext())
        binding.addressRV.adapter = addressAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeAddresses() {
        viewModel.address.observe(viewLifecycleOwner) { addressList ->
            Log.d("TAG", "observeAddresses: $addressList")
            addressAdapter.updateList(addressList)
        }

        viewModel.isSelectesAaddress.observe(viewLifecycleOwner)
        {
            if (it) {
                viewModel.getAddressById(userID)
            } else {
                viewModel.isSelectesAaddress.postValue(false)
            }
        }
    }

    private fun checkLocationAndPermission() {
        if (!isLocationEnabled(requireContext())) {
            showLocationSettingsDialog()
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getCurrentLocation()
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showLocationSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Enable Location")
            .setMessage("Location is required to proceed. Please enable location in settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getCurrentLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
            setMinUpdateIntervalMillis(0)
            setMaxUpdateDelayMillis(0)
            setWaitForAccurateLocation(true)
            setGranularity(Granularity.GRANULARITY_FINE)
            setMaxUpdates(1)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val intent = Intent(requireContext(), MapActivity::class.java).apply {
                        putExtra("latitude", location.latitude)
                        putExtra("longitude", location.longitude)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Unable to get location", Toast.LENGTH_SHORT)
                        .show()
                }
                fusedLocationProviderClient.removeLocationUpdates(this)
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                requireActivity().mainLooper
            )
        } else {
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::fusedLocationProviderClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }
}
