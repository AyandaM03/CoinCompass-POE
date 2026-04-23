package com.example.coincompass

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.*



class ExpenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_expense)

        findViewById<Button>(R.id.btn_save_expense).setOnClickListener {
            val amount = findViewById<EditText>(R.id.et_amount).text.toString()
            val desc = findViewById<EditText>(R.id.et_description).text.toString()
            val category = findViewById<EditText>(R.id.et_category).text.toString()
            val date = findViewById<EditText>(R.id.et_date).text.toString()

            if (amount.isNotEmpty() && desc.isNotEmpty() && category.isNotEmpty() && date.isNotEmpty()) {
                val transaction = "- R$amount\n$desc\nCategory: $category\nDate: $date"
                TransactionsActivity.transactionsList.add(0, transaction)
                Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
                finish() // Go back to Transactions page
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}