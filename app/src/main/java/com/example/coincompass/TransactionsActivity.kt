package com.example.coincompass

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class TransactionsActivity : AppCompatActivity() {
    companion object {
        var transactionsList = mutableListOf<String>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transactions)


        val tvList = findViewById<TextView>(R.id.tv_transactions_list)

        // Show transactions
        if (transactionsList.isEmpty()) {
            tvList.text = "No transactions yet."
        } else {
            tvList.text = transactionsList.joinToString("\n\n")
        }

        // Open Income page
        findViewById<Button>(R.id.btn_add_income).setOnClickListener {
            startActivity(Intent(this, IncomeActivity::class.java))
        }

        // Open Expense page
        findViewById<Button>(R.id.btn_add_expense).setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }

        // Bottom Nav
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_transactions
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_transactions -> true
                R.id.nav_analytics -> {
                    startActivity(Intent(this, AnalyticsActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh list when coming back from Income/Expense
        val tvList = findViewById<TextView>(R.id.tv_transactions_list)
        if (transactionsList.isEmpty()) {
            tvList.text = "No transactions yet."
        } else {
            tvList.text = transactionsList.joinToString("\n\n")
        }
    }
}