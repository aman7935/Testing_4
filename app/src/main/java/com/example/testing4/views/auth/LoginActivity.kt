package com.example.testing4.views.auth

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.testing4.R
import com.example.testing4.databinding.ActivityLoginBinding
import com.example.testing4.datastore.DataStoreManager
import com.example.testing4.views.activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val dataStore by lazy { DataStoreManager(applicationContext) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.singUpButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val userName = binding.userNameAndEmail.text.toString()
            val password = binding.LoginPassword.text.toString()

            if (userName.isNotEmpty() && password.isNotEmpty()) loginUser(userName, password)
            else Toast.makeText(this, "please enter the username and password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    lifecycleScope.launch {
                        dataStore.hasLoggedIn(true)
                        dataStore.getLoggedIn.first().let {
                            Log.d("logged in", "loginUser: $it")
                        }
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
    }
}