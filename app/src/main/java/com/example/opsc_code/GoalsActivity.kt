package com.example.opsc_code

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity



class GoalsActivity : AppCompatActivity() {

    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var btnSaveGoals: Button
    private lateinit var tvSavedGoals: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)


        etMinGoal = findViewById(R.id.etMinGoal)
        etMaxGoal = findViewById(R.id.etMaxGoal)
        btnSaveGoals = findViewById(R.id.btnSaveGoals)
        tvSavedGoals = findViewById(R.id.tvSavedGoals)

        loadGoals() //

        btnSaveGoals.setOnClickListener {
            saveGoals()
        }
    }

    private fun saveGoals() {
        val minText = etMinGoal.text.toString().trim()
        val maxText = etMaxGoal.text.toString().trim()

        if (minText.isEmpty() || maxText.isEmpty()) {
            Toast.makeText(this, "Enter both values", Toast.LENGTH_SHORT).show()
            return
        }

        val minGoal = minText.toFloatOrNull()
        val maxGoal = maxText.toFloatOrNull()

        if (minGoal == null || maxGoal == null) {
            Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        if (minGoal > maxGoal) {
            Toast.makeText(this, "Min cannot be greater than Max", Toast.LENGTH_SHORT).show()
            return
        }

        // SAVE LOCALLY (SharedPreferences)
        val prefs = getSharedPreferences("GoalsPrefs", MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putFloat("minGoal", minGoal)
        editor.putFloat("maxGoal", maxGoal)
        editor.apply()

        Toast.makeText(this, "Goals saved locally", Toast.LENGTH_SHORT).show()

        tvSavedGoals.text = "Min: R$minGoal | Max: R$maxGoal"
    }

    private fun loadGoals() {
        val prefs = getSharedPreferences("GoalsPrefs", MODE_PRIVATE)

        val min = prefs.getFloat("minGoal", 0f)
        val max = prefs.getFloat("maxGoal", 0f)

        if (min != 0f || max != 0f) {
            etMinGoal.setText(min.toString())
            etMaxGoal.setText(max.toString())

            tvSavedGoals.text = "Min: R$min | Max: R$max"
        }
    }
    }
