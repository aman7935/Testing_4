package com.example.testing4.views.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.testing4.R
import com.example.testing4.databinding.FragmentYouBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.utils.ConstValues.BROADCAST_ACTION
import com.example.testing4.utils.ConstValues.EXTRA_MESSAGE
import com.stripe.android.model.PaymentMethodOptionsParams
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class YouFragment : Fragment() {
    private lateinit var binding : FragmentYouBinding
    private val dataStore by lazy { DataStoreManager(requireContext())}


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentYouBinding.inflate(inflater, container, false)

        lifecycleScope.launch {
            binding.name.text = dataStore.getUserName.first().toString()
            binding.textView.text = dataStore.getUserName.first().toString()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.name.setOnClickListener {
            showDialogBoxForName(requireContext()) {
                val intent = Intent(BROADCAST_ACTION)
                lifecycleScope.launch {
                    dataStore.saveUserName(it)
                    binding.name.text = dataStore.getUserName.first().toString()
                    binding.textView.text = dataStore.getUserName.first().toString()
                }
                intent.putExtra(EXTRA_MESSAGE, it)
                requireContext().sendBroadcast(intent)
            }
        }
    }

    fun showDialogBoxForName(context: Context, onUsernameEntered: (String) -> Unit) : String{
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enter Your Name")

        val input = EditText(context).apply {
            hint = "Username"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, _ ->
            val username = input.text.toString()
            if (username.isNotEmpty()){
                onUsernameEntered(username)
            }
            else{
                Toast.makeText(requireContext(), "Please enter a username", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()

        return input.text.toString()
    }
}