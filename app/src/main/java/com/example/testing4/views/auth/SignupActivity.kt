package com.example.testing4.views.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.testing4.databinding.ActivitySignupBinding
import com.example.testing4.datastore.DataStoreManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth : FirebaseAuth
    private val dataStore by lazy { DataStoreManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.signupButton.setOnClickListener { checkDetailsAndSignUp() }
    }

    private fun checkDetailsAndSignUp() {
        auth = FirebaseAuth.getInstance()
        val username = binding.SignUpName.text.toString().trim()
        val email = binding.email.text.toString().trim()
        val contactNumber = binding.ContactNumber.text.toString().trim()
        val password = binding.signUpPassword.text.toString().trim()
        val confirmPass = binding.confirmPass.text.toString().trim()

        binding.validationUsername.visibility = View.VISIBLE
        binding.passwordValidation.visibility = View.VISIBLE
        binding.confirmPassValidation.visibility = View.VISIBLE

        var isValid = true

        if (username.isEmpty()) {
            binding.validationUsername.text = "Username is required"
            isValid = false
        } else if (!username.matches(Regex("^[a-zA-Z0-9]{3,20}$"))) {
            binding.validationUsername.text =
                "Username must be 3–20 characters (letters and numbers only)"
            isValid = false
        } else {
            binding.validationUsername.text = ""
        }

        val passwordRegex =
            Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$")
        if (password.isEmpty()) {
            binding.passwordValidation.text = "Password is required"
            isValid = false
        } else if (!password.matches(passwordRegex)) {
            binding.passwordValidation.text =
                "Password must be 8+ chars, include upper/lower case, digit & special char"
            isValid = false
        } else {
            binding.passwordValidation.text = ""
        }

        if (confirmPass.isEmpty()) {
            binding.confirmPassValidation.text = "Confirm password is required"
            isValid = false
        } else if (password != confirmPass) {
            binding.confirmPassValidation.text = "Passwords do not match"
            isValid = false
        } else {
            binding.confirmPassValidation.text = ""
        }

        if (isValid) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch { dataStore.saveUserName(username) }
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                else{
                    Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
