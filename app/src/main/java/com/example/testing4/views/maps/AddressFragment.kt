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
import com.example.testing4.databinding.FragmentAddressBinding
import com.google.android.gms.location.*
import java.util.Locale

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var geoCoder: Geocoder

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
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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


    //using GPS+NETWORK for the most accurate location
    private fun getCurrentLocation() {                                          //constructor interval(0) -> how often you want to get location updates
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0).apply {
            setMinUpdateIntervalMillis(0) /*minimum interval for updates means if the updates gets faster than the
            constructor interval then the update will have to wait for this much interval*/

            setMaxUpdateDelayMillis(0) /*want location updates but android is allowed to send a batch of updates
            sending multiple updates at once*/

            setWaitForAccurateLocation(true)  //  telling Android to wait until it can get a more accurate location before delivering the update to your app.
            setGranularity(Granularity.GRANULARITY_FINE) //  return precise latitude/longitude using GPS..
            //setDurationMillis(Long.MAX_VALUE)  // Requests location updates indefinitely until you explicitly stop them  (not usefull here).
            setMaxUpdates(1)    // sets the maximum number of location updates your app wants to receive before the request automatically stops
        }.build()


        //This is an abstract class used to receive location updates asynchronously (in the background).
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
                //used to manually stop location updates once you’ve received the needed result.(DEFENSIVE PROGRAMMING PRACTICE)
                fusedLocationProviderClient.removeLocationUpdates(this)
            }
        }

        if (ContextCompat.checkSelfPermission( // checks if your app has already been granted a specific permission.
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, //tells the system how to get the location (e.g., accuracy, interval).
                locationCallback, //what happens when a new location is received (usually where location.latitude and location.longitude).
                requireActivity().mainLooper
                /*callback runs on the main (UI) thread, which is necessary if we plan to update the UI
                    (e.g., start a new activity or show a Toast).
                    If you omit this parameter, the callback might run on a background thread,
                    which could cause issues if you try to update the UI directly from there.
                    UI must be updated from the mainthread*/
            )
        } else {
            Toast.makeText(requireContext(), "Location permission not granted", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
