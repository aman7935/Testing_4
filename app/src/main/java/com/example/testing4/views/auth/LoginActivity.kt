package com.example.testing4.views.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.testing4.databinding.ActivityLoginBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.views.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val dataStore by lazy { DataStoreManager(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.singUpButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.loginButton.setOnClickListener {
            val userName = binding.userNameAndEmail.text.toString().trim()
            val password = binding.LoginPassword.text.toString().trim()

            if (userName.isNotEmpty() && password.isNotEmpty()) {
                loginUser(userName, password)
            } else {
                Toast.makeText(this, "Please enter the username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lifecycleScope.launch {
                        val firebaseUser = auth.currentUser
                        if (firebaseUser != null) {
                            val userId = firebaseUser.uid
                            Log.d("LoginActivity", "User UID: $userId") // Debug UID
                            dataStore.saveUserId(userId)
                            dataStore.hasLoggedIn(true)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "User authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
