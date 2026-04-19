package com.example.coincompass

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Link the EditText and Button UI components to Kotlin variables
        val name = findViewById<EditText>(R.id.fullName)
        val username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val loginLink = findViewById<TextView>(R.id.loginLink)

        // Set listener for the Register button
        registerBtn.setOnClickListener {

            // Get the text entered in the fields
            val userName = name.text.toString()
            val userUsername = username.text.toString()
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()
            val confirmPass = confirmPassword.text.toString()

            // Validate if any fields are empty
            if (userName.isEmpty() || userUsername.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            // Validate if passwords match
            else if (userPassword != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            // If everything is valid
            else {
                // Show registration success message
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                // Go back to Login screen after registration
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()  // Close the Register screen so the user can't go back to it
            }
        }
    }
}