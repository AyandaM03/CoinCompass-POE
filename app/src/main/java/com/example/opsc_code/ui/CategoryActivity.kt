package com.example.opsc_code.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_code.R
import com.example.opsc_code.data.model.Category
import com.example.opsc_code.ui.adapter.CategoryAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Activity for managing expense categories.
 * Allows users to create, view, and delete categories linked to their Firebase account.
 */
class CategoryActivity : AppCompatActivity() {

    // UI Components
    private lateinit var etCategoryName: EditText
    private lateinit var btnSaveCategory: Button
    private lateinit var btnAddExpense: Button
    private lateinit var rvCategories: RecyclerView
    private lateinit var tvEmptyState: TextView

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    // Adapter
    private lateinit var categoryAdapter: CategoryAdapter

    companion object {
        private const val COLLECTION_CATEGORIES = "categories"
        private const val FIELD_USER_ID = "userId"
        private const val FIELD_NAME = "name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Check if user is logged in
        if (auth.currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        initializeViews()
        setupRecyclerView()
        setupClickListeners()
        loadCategories()
    }

    /**
     * Initializes all view references.
     */
    private fun initializeViews() {
        etCategoryName = findViewById(R.id.etCategoryName)
        btnSaveCategory = findViewById(R.id.btnSaveCategory)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        rvCategories = findViewById(R.id.rvCategories)
        tvEmptyState = findViewById(R.id.tvEmptyState)
    }

    /**
     * Sets up the RecyclerView with the adapter.
     */
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onDeleteClick = { category ->
                deleteCategory(category)
            }
        )
        rvCategories.apply {
            layoutManager = LinearLayoutManager(this@CategoryActivity)
            adapter = categoryAdapter
        }
    }

    /**
     * Sets up click listeners for buttons.
     */
    private fun setupClickListeners() {
        btnSaveCategory.setOnClickListener {
            saveCategory()
        }

        btnAddExpense.setOnClickListener {
            navigateToExpenseActivity()
        }
    }

    /**
     * Navigates to ExpenseActivity to add a new expense.
     */
    private fun navigateToExpenseActivity() {
        val intent = Intent(this, ExpenseActivity::class.java)
        startActivity(intent)
    }

    /**
     * Validates input and saves a new category to Firestore.
     * The category is linked to the currently logged-in user.
     */
    private fun saveCategory() {
        val categoryName = etCategoryName.text.toString().trim()

        // Validate input
        if (categoryName.isEmpty()) {
            etCategoryName.error = "Category name cannot be empty"
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Create category object
        val category = Category(
            name = categoryName,
            userId = currentUser.uid
        )

        // Disable button to prevent double submission
        btnSaveCategory.isEnabled = false

        // Save to Firestore
        firestore.collection(COLLECTION_CATEGORIES)
            .add(category.toMap())
            .addOnSuccessListener { documentReference ->
                // Success
                Toast.makeText(
                    this,
                    "Category saved successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // Clear input
                etCategoryName.text.clear()

                // Refresh the list
                loadCategories()
            }
            .addOnFailureListener { e ->
                // Failure
                Toast.makeText(
                    this,
                    "Failed to save category: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnCompleteListener {
                // Re-enable button
                btnSaveCategory.isEnabled = true
            }
    }

    /**
     * Retrieves and displays all categories for the current user from Firestore.
     * Categories are ordered alphabetically by name.
     */
    private fun loadCategories() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            showEmptyState(true)
            return
        }

        firestore.collection(COLLECTION_CATEGORIES)
            .whereEqualTo(FIELD_USER_ID, currentUser.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val categories = querySnapshot.documents.map { document ->
                    Category.fromMap(document.id, document.data)
                }.sortedBy { it.name } // Sort locally instead of in query

                categoryAdapter.updateCategories(categories)
                showEmptyState(categories.isEmpty())
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to load categories: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                showEmptyState(true)
            }
    }

    /**
     * Deletes a category from Firestore.
     * 
     * @param category The category to delete
     */
    private fun deleteCategory(category: Category) {
        firestore.collection(COLLECTION_CATEGORIES)
            .document(category.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Category deleted",
                    Toast.LENGTH_SHORT
                ).show()
                loadCategories()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to delete category: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    /**
     * Shows or hides the empty state message.
     * 
     * @param show True to show empty state, false to hide
     */
    private fun showEmptyState(show: Boolean) {
        tvEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        rvCategories.visibility = if (show) View.GONE else View.VISIBLE
    }
}
