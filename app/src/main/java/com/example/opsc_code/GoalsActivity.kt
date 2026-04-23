package com.example.opsc_code

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GoalsActivity : AppCompatActivity() {

    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var btnSaveGoals: Button
    private lateinit var tvSavedGoals: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goals)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
        val userId = auth.currentUser?.uid ?: return

        val minText = etMinGoal.text.toString().trim()
        val maxText = etMaxGoal.text.toString().trim()

        if (minText.isEmpty() || maxText.isEmpty()) {
            Toast.makeText(this, "Enter both values", Toast.LENGTH_SHORT).show()
            return
        }

        val minGoal = minText.toDoubleOrNull()
        val maxGoal = maxText.toDoubleOrNull()

        if (minGoal == null || maxGoal == null) {
            Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show()
            return
        }

        val goalMap = hashMapOf(
            "userId" to userId,
            "minGoal" to minGoal,
            "maxGoal" to maxGoal
        )

        firestore.collection("goals")
            .document(userId)
            .set(goalMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show()


                tvSavedGoals.text = "Min: R$minGoal | Max: R$maxGoal"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save goals", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadGoals() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("goals")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val min = doc.getDouble("minGoal") ?: 0.0
                    val max = doc.getDouble("maxGoal") ?: 0.0

                    etMinGoal.setText(min.toString())
                    etMaxGoal.setText(max.toString())

                    tvSavedGoals.text = "Min: R$min | Max: R$max"
                }
            }
    }
}