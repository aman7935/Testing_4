package com.example.testing4.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.database.Database
import com.example.testing4.databinding.FragmentBottomSheetBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.factory.Factory
import com.example.testing4.models.entities.UserAddress
import com.example.testing4.repo.Repo
import com.example.testing4.viewmodels.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetBinding
    private lateinit var address: String
    private lateinit var city: String
    private lateinit var state: String
    private lateinit var latLng: LatLng
    private lateinit var viewModel: ViewModel
    private lateinit var repo: Repo
    private lateinit var dbDao: Database
    private var selectedText: String? = null
    private lateinit var dataStore: DataStoreManager
    private lateinit var userID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        address = arguments?.getString("address") ?: ""
        city = arguments?.getString("city") ?: ""
        state = arguments?.getString("state") ?: ""
        latLng = arguments?.getParcelable("latLng") ?: LatLng(0.0, 0.0)

        dbDao = DataBaseProvider.getInstance(requireContext())
        dataStore = DataStoreManager(requireContext())
        repo = Repo(RetrofitInstance.retroFitApi, dbDao.dbDao)
        viewModel = ViewModelProvider(this, Factory(repo, dataStore))[ViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.location.text = "$city, $state"

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedButton = group.findViewById<RadioButton>(checkedId)
            selectedText = selectedButton.text.toString()
        }

        lifecycleScope.launch {
            userID = dataStore.getUserId.first()

            viewModel.getAllAddressesByUserId(userID) { addresses -> // No addresses found, check the checkbox
                if (addresses.isEmpty()) {
                    binding.checkBox.isChecked = true
                }
            }
        }

        binding.saveAddressButton.setOnClickListener {
            val apartmentOrHouseNo = binding.home.text.toString()
            val streetDetails = binding.streetDetails.text.toString()
            val landmark = binding.landmark.text.toString()
            val pincode = binding.pincodeDetails.text.toString()
            val name = binding.nameDetails.text.toString()

            if (selectedText == null) {
                Toast.makeText(requireContext(), "Please select address type", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (apartmentOrHouseNo.isNotEmpty() && pincode.isNotEmpty()) {
                val isDefault = if (binding.checkBox.isChecked) 1 else 0

                lifecycleScope.launch {
                    if (isDefault == 1) {
                        viewModel.resetDefaultAddress(userID)
                    }

                    val userAddress = UserAddress(
                        userId = userID,
                        name = name,
                        address = address,
                        pinCode = pincode.toInt(),
                        addressType = selectedText!!,
                        apartmentOrHouseNo = apartmentOrHouseNo,
                        streetDetails = streetDetails,
                        landmark = landmark,
                        defaultAddress = isDefault
                    )

                    viewModel.saveAddress(userAddress)

                    Toast.makeText(requireContext(), "Address saved", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in required fields", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
