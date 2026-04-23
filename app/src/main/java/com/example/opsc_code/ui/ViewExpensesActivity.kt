package com.example.opsc_code.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_code.R
import com.example.opsc_code.data.model.Category
import com.example.opsc_code.data.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Activity for viewing all expenses for the logged-in user.
 */
class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmptyState: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvGoalStatus: TextView
    private lateinit var btnBackToDashboard: Button
    private lateinit var btnAddExpense: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: ExpenseAdapter

    private val expenses = mutableListOf<Expense>()
    private val categories = mutableMapOf<String, Category>()
    private var minGoal = 0.0
    private var maxGoal = 0.0
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    companion object {
        private const val COLLECTION_EXPENSES = "expenses"
        private const val COLLECTION_CATEGORIES = "categories"
        private const val COLLECTION_GOALS = "goals"
        private const val FIELD_USER_ID = "userId"
        private const val FIELD_DATE = "date"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)

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
        setupButtons()
        loadCategoriesAndExpenses()
        loadGoals()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewExpenses)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        progressBar = findViewById(R.id.progressBar)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvGoalStatus = findViewById(R.id.tvGoalStatus)
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard)
        btnAddExpense = findViewById(R.id.btnAddExpense)
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter(expenses, categories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupButtons() {
        btnBackToDashboard.setOnClickListener {
            finish()
        }

        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }
    }
    private fun loadGoals() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection(COLLECTION_GOALS)
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    minGoal = doc.getDouble("minGoal") ?: 0.0
                    maxGoal = doc.getDouble("maxGoal") ?: 0.0
                }
                loadCategoriesAndExpenses()
            }
            .addOnFailureListener {
                loadCategoriesAndExpenses()
            }
    }
    private fun loadCategoriesAndExpenses() {
        showLoading(true)
        val currentUser = auth.currentUser ?: return

        // First load categories
        firestore.collection(COLLECTION_CATEGORIES)
            .whereEqualTo(FIELD_USER_ID, currentUser.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                categories.clear()
                querySnapshot.documents.forEach { document ->
                    val category = Category.fromMap(document.id, document.data)
                    categories[category.id] = category
                }
                // Then load expenses
                loadExpenses()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load categories: ${e.message}", Toast.LENGTH_LONG).show()
                showLoading(false)
            }
    }

    private fun loadExpenses() {
        val currentUser = auth.currentUser ?: return

        firestore.collection(COLLECTION_EXPENSES)
            .whereEqualTo(FIELD_USER_ID, currentUser.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                expenses.clear()
                querySnapshot.documents.forEach { document ->
                    val expense = Expense.fromMap(document.id, document.data)
                    expenses.add(expense)
                }
                // Sort by date descending (newest first) locally
                expenses.sortByDescending { it.date }
                adapter.notifyDataSetChanged()
                updateTotalAndGoals()
                showEmptyState(expenses.isEmpty())
                showLoading(false)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load expenses: ${e.message}", Toast.LENGTH_LONG).show()
                showLoading(false)
            }
    }

    private fun updateTotalAmount() {
        val total = expenses.sumOf { it.amount }
        tvTotalAmount.text = String.format("Total: R%.2f", total)
    }
    private fun updateTotalAndGoals() {
        val total = expenses.sumOf { it.amount }
        tvTotalAmount.text = String.format("Total: R%.2f", total)

        if (minGoal == 0.0 && maxGoal == 0.0) {
            tvGoalStatus.text = "No goals set"
            return
        }

        tvGoalStatus.text = when {
            total < minGoal -> "Below minimum goal"
            total > maxGoal -> "Exceeded maximum goal"
            else -> "Within budget"
        }
    }

    private fun showEmptyState(show: Boolean) {
        tvEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        recyclerView.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this activity
        if (auth.currentUser != null) {
            loadCategoriesAndExpenses()
        }
    }

    /**
     * RecyclerView Adapter for expenses.
     */
    inner class ExpenseAdapter(
        private val expenses: List<Expense>,
        private val categories: Map<String, Category>
    ) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

        inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
            val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
            val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
            val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
            val tvHasPhoto: TextView = itemView.findViewById(R.id.tvHasPhoto)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_expense, parent, false)
            return ExpenseViewHolder(view)
        }

        override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
            val expense = expenses[position]
            
            holder.tvDescription.text = expense.description
            holder.tvAmount.text = String.format("R%.2f", expense.amount)
            
            // Get category name
            val categoryName = categories[expense.categoryId]?.name ?: "Unknown Category"
            holder.tvCategory.text = "Category: $categoryName"
            
            // Format date and time
            val dateStr = expense.date?.let { dateFormat.format(it) } ?: "No date"
            val timeStr = if (expense.startTime.isNotEmpty() && expense.endTime.isNotEmpty()) {
                "${expense.startTime} - ${expense.endTime}"
            } else {
                ""
            }
            holder.tvDateTime.text = if (timeStr.isNotEmpty()) {
                "$dateStr | $timeStr"
            } else {
                dateStr
            }
            
            // Show photo indicator
            holder.tvHasPhoto.visibility = if (expense.photoUrl.isNotEmpty()) View.VISIBLE else View.GONE
        }

        override fun getItemCount(): Int = expenses.size
    }
}
