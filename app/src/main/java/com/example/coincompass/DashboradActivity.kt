package com.example.coincompass

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvUsername: TextView
    private lateinit var tvTotalBalance: TextView
    private lateinit var tvIncome: TextView
    private lateinit var tvExpenses: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashborad)

        tvUsername = findViewById(R.id.tv_username)
        tvTotalBalance = findViewById(R.id.tv_total_balance)
        tvIncome = findViewById(R.id.tv_income)
        tvExpenses = findViewById(R.id.tv_expenses)

        loadUserData()

        findViewById<Button>(R.id.btn_edit_name).setOnClickListener {
            showInputDialog("Enter Your Name") { name ->
                tvUsername.text = name
                saveData("username", name)
            }
        }

        findViewById<Button>(R.id.btn_set_balance).setOnClickListener {
            showInputDialog("Enter Balance Amount") { balance ->
                tvTotalBalance.text = "R$balance"
                saveData("balance", balance)
            }
        }

        findViewById<Button>(R.id.btn_add_transaction).setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }

        // BOTTOM NAV LOGIC
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home // Highlight Home

        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> true
                R.id.nav_budget -> {
                    startActivity(Intent(this, BudgetActivity::class.java))
                    true
                }
                R.id.nav_analytics -> {
                    startActivity(Intent(this, AnalyticsActivity::class.java)) // POINT 7
                    true
                }
                R.id.nav_savings -> {
                    startActivity(Intent(this, SavingsActivity::class.java))
                    true
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java)) // POINT 6
                    true
                }
                else -> false
            }
        }
    }

    private fun showInputDialog(title: String, onSave: (String) -> Unit) {
        val input = EditText(this)
        input.hint = "Type here"
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val value = input.text.toString()
                if (value.isNotEmpty()) onSave(value)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveData(key: String, value: String) {
        getSharedPreferences("UserData", Context.MODE_PRIVATE)
            .edit().putString(key, value).apply()
    }

    private fun loadUserData() {
        val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        tvUsername.text = prefs.getString("username", "Guest")
        tvTotalBalance.text = "R${prefs.getString("balance", "0.00")}"
        tvIncome.text = "R${prefs.getString("income", "0.00")}"
        tvExpenses.text = "R${prefs.getString("expenses", "0.00")}"
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }
}