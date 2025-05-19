package com.example.testing4.views.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.*
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
import com.example.testing4.repo.Repo
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
    private lateinit var  dataStore : DataStoreManager
    private lateinit var  userID : String

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

        binding.addbtn.setOnClickListener {
            checkLocationAndPermission()
        }

        addressAdapter = AddressAdapter(emptyList(), object : OnClickListenerForAddress {
            override fun onClickForAddress(list: UserAddress) {
                address = "${list.apartmentOrHouseNo}, ${list.streetDetails}"
            }
        })

        binding.addressRV.layoutManager = LinearLayoutManager(requireContext())
        binding.addressRV.adapter = addressAdapter


        lifecycleScope.launch {
            userID = dataStore.getUserId.first()
        }
        getAllAddresses()

        binding.saveBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString("address", address)
            }
            findNavController().navigate(R.id.action_addressFragment_to_cartFragment, bundle)
        }
    }

    private fun getAllAddresses() {
        viewModel.getAllAddressesByUserId(userID) { addressList ->
            // Update adapter data and refresh RecyclerView when new data arrives
            addressAdapter.updateList(addressList)
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
                // Request location permission from user
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Check if either GPS or Network location provider is enabled
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

    // Using GPS + NETWORK for the most accurate location
    private fun getCurrentLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
            setMinUpdateIntervalMillis(0) /* minimum interval for updates means if the updates get faster than the
            constructor interval then the update will have to wait for this much interval */

            setMaxUpdateDelayMillis(0) /* want location updates but Android is allowed to send a batch of updates
            sending multiple updates at once */

            setWaitForAccurateLocation(true)  // telling Android to wait until it can get a more accurate location before delivering the update to your app.
            setGranularity(Granularity.GRANULARITY_FINE) // return precise latitude/longitude using GPS..
            setMaxUpdates(1)    // sets the maximum number of location updates your app wants to receive before the request automatically stops
        }.build()

        // This is an abstract class used to receive location updates asynchronously (in the background).
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
                // Used to manually stop location updates once you’ve received the needed result. (DEFENSIVE PROGRAMMING PRACTICE)
                fusedLocationProviderClient.removeLocationUpdates(this)
            }
        }

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, // tells the system how to get the location (e.g., accuracy, interval).
                locationCallback, // what happens when a new location is received (usually where location.latitude and location.longitude).
                requireActivity().mainLooper /* callback runs on the main (UI) thread, which is necessary if we plan to update the UI
                    (e.g., start a new activity or show a Toast).
                    If you omit this parameter, the callback might run on a background thread,
                    which could cause issues if you try to update the UI directly from there.
                    UI must be updated from the main thread */
            )
        } else {
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }

    /*override fun onStop() {
        super.onStop()
        if (::fusedLocationProviderClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }*/
}
