package com.example.opsc_code.ui.theme

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc_code.R
import com.example.opsc_code.ui.DashboardActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
        private lateinit var auth: FirebaseAuth
        private lateinit var email: EditText
        private lateinit var password: EditText
        private lateinit var loginBtn: Button
        private lateinit var goToRegister: TextView
        private lateinit var tvForgotPassword: TextView
        private lateinit var btnTogglePassword: ImageButton
        private var isPasswordVisible = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)

            auth = FirebaseAuth.getInstance()

            email = findViewById(R.id.email)
            password = findViewById(R.id.password)
            loginBtn = findViewById(R.id.loginBtn)
            goToRegister = findViewById(R.id.goToRegister)
            tvForgotPassword = findViewById(R.id.tvForgotPassword)
            btnTogglePassword = findViewById(R.id.btnTogglePassword)

            loginBtn.setOnClickListener {
                loginUser()
            }

            goToRegister.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }

            tvForgotPassword.setOnClickListener {
                resetPassword()
            }

            btnTogglePassword.setOnClickListener {
                togglePasswordVisibility()
            }
        }

        private fun togglePasswordVisibility() {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            password.setSelection(password.text.length)
        }

        private fun resetPassword() {
            val userEmail = email.text.toString().trim()
            if (userEmail.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
                return
            }

            auth.sendPasswordResetEmail(userEmail)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        private fun loginUser() {
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return
            }

            auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                        // Go to main screen
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Error: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
}