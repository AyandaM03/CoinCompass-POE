package com.example.opsc_code.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc_code.GoalsActivity
import com.example.opsc_code.R
import com.example.opsc_code.ui.theme.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.jvm.java

/**
 * DashboardActivity serves as the main landing page after login.
 * Displays a welcome message with the user's name and provides navigation
 * to Create Category, Add Expense, and View Expenses features.
 */
class DashboardActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var btnCreateCategory: Button
    private lateinit var btnSetGoals: Button
    private lateinit var btnAddExpense: Button
    private lateinit var btnViewExpenses: Button
    private lateinit var btnLogout: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if user is logged in
        if (auth.currentUser == null) {
            navigateToLogin()
            return
        }

        initializeViews()
        loadUserName()
        setupClickListeners()
    }

    /**
     * Initializes all view references.
     */
    private fun initializeViews() {
        tvUserName = findViewById(R.id.tvUserName)
        btnCreateCategory = findViewById(R.id.btnCreateCategory)
        btnSetGoals = findViewById(R.id.btnSetGoals)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        btnViewExpenses = findViewById(R.id.btnViewExpenses)
        btnLogout = findViewById(R.id.btnLogout)

    }

    /**
     * Loads the user's name from Firebase Auth or Firestore.
     */
    private fun loadUserName() {
        val currentUser = auth.currentUser

        // Try to get name from Firebase Auth display name first
        val displayName = currentUser?.displayName
        if (!displayName.isNullOrEmpty()) {
            tvUserName.text = displayName
            return
        }

        // Fallback: Load from Firestore
        currentUser?.uid?.let { userId ->
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name") ?: "User"
                        tvUserName.text = name
                    } else {
                        tvUserName.text = "User"
                    }
                }
                .addOnFailureListener {
                    tvUserName.text = "User"
                }
        }
    }

    /**
     * Sets up click listeners for all buttons.
     */
    private fun setupClickListeners() {
        btnCreateCategory.setOnClickListener {
            navigateToCategoryActivity()
        }

        btnAddExpense.setOnClickListener {
            navigateToExpenseActivity()
        }

        btnViewExpenses.setOnClickListener {
            navigateToViewExpenses()
        }

        btnSetGoals.setOnClickListener {
            navigateToGoalsActivity()
        }

        btnLogout.setOnClickListener {
            logout()
        }

    }

    /**
     * Navigates to CategoryActivity to create/manage categories.
     */
    private fun navigateToCategoryActivity() {
        val intent = Intent(this, CategoryActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToGoalsActivity() {
        val intent = Intent(this, GoalsActivity::class.java )
        startActivity(intent)
    }

    /**
     * Navigates to ExpenseActivity to add a new expense.
     */
    private fun navigateToExpenseActivity() {
        val intent = Intent(this, ExpenseActivity::class.java)
        startActivity(intent)
    }

    /**
     * Navigates to ViewExpensesActivity to view all expenses.
     */
    private fun navigateToViewExpenses() {
        val intent = Intent(this, ViewExpensesActivity::class.java)
        startActivity(intent)
    }

    /**
     * Navigates to LoginActivity.
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Logs out the current user and navigates to log in screen.
     */
    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        navigateToLogin()
    }
}
