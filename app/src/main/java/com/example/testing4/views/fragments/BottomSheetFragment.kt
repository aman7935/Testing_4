package com.example.testing4.views.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.testing4.R
import com.example.testing4.api.RetrofitInstance
import com.example.testing4.database.DataBaseProvider
import com.example.testing4.database.Database
import com.example.testing4.database.DbDao
import com.example.testing4.databinding.FragmentBottomSheetBinding
import com.example.testing4.factory.Factory
import com.example.testing4.models.entities.UserAddress
import com.example.testing4.repo.Repo
import com.example.testing4.viewmodels.ViewModel
import com.example.testing4.views.auth.userId
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


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
        repo = Repo(RetrofitInstance.retroFitApi, dbDao.dbDao)
        viewModel = ViewModelProvider(this, Factory(repo))[ViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.localtion.text = "$city, $state"

        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedButton = group.findViewById<RadioButton>(checkedId)
            selectedText = selectedButton.text.toString()
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
                val userAddress = UserAddress(
                    userId = userId,
                    name = name,
                    address = address,
                    pinCode = pincode.toInt(),
                    addressType = selectedText!!,
                    apartmentOrHouseNo = apartmentOrHouseNo,
                    streetDetails = streetDetails,
                    landmark = landmark,
                )
                viewModel.saveAddress(userAddress)
            }
        }
    }
}
