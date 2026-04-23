package com.example.coincompass

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        //link UI components
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val registerLink = findViewById<TextView>(R.id.registerLink)

        // Set listener for login button click
        loginBtn.setOnClickListener{
            val userUsername = username.text.toString()
            val userPassword = password.text.toString()
            
            // Validation: Check if any field is empty
            if(userUsername.isEmpty()|| userPassword.isEmpty()){
                Toast.makeText(this , "Please Fill all fields" , Toast.LENGTH_SHORT).show()

            }else {
            //go to dashborad/home page
                val intent = Intent(this , DashboardActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
        //go to register screen
       registerLink.setOnClickListener{
           val intent = Intent(this , RegisterActivity :: class.java)
           startActivity(intent)
       }
        }


}
