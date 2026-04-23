package com.example.opsc_code.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.opsc_code.R
import com.example.opsc_code.data.model.Category
import com.example.opsc_code.data.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Activity for creating and saving expense entries.
 * Allows users to select category, enter description, amount, date, and time range.
 */
class ExpenseActivity : AppCompatActivity() {

    // UI Components
    private lateinit var spinnerCategory: Spinner
    private lateinit var etDescription: EditText
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var btnSaveExpense: Button
    private lateinit var btnSelectPhoto: Button
    private lateinit var ivReceiptPhoto: ImageView
    private lateinit var progressBar: ProgressBar

    // Firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    // Data
    private val categories = mutableListOf<Category>()
    private var selectedCategoryId: String = ""
    private var selectedPhotoUri: Uri? = null
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    companion object {
        private const val COLLECTION_EXPENSES = "expenses"
        private const val COLLECTION_CATEGORIES = "categories"
        private const val FIELD_USER_ID = "userId"
        private const val PICK_IMAGE_REQUEST = 1
        private const val CAPTURE_IMAGE_REQUEST = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        // Check if user is logged in
        if (auth.currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        initializeViews()
        setupDateTimePickers()
        setupSaveButton()
        loadCategories()
    }

    /**
     * Initializes all view references.
     */
    private fun initializeViews() {
        spinnerCategory = findViewById(R.id.spinnerCategory)
        etDescription = findViewById(R.id.etDescription)
        etAmount = findViewById(R.id.etAmount)
        etDate = findViewById(R.id.etDate)
        etStartTime = findViewById(R.id.etStartTime)
        etEndTime = findViewById(R.id.etEndTime)
        btnSaveExpense = findViewById(R.id.btnSaveExpense)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        ivReceiptPhoto = findViewById(R.id.ivReceiptPhoto)
        progressBar = findViewById(R.id.progressBar)
    }

    /**
     * Sets up date and time picker dialogs.
     */
    private fun setupDateTimePickers() {
        // Date picker
        etDate.setOnClickListener {
            showDatePicker()
        }

        // Start time picker
        etStartTime.setOnClickListener {
            showTimePicker(etStartTime)
        }

        // End time picker
        etEndTime.setOnClickListener {
            showTimePicker(etEndTime)
        }
    }

    /**
     * Shows a date picker dialog.
     */
    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            etDate.setText(dateFormat.format(calendar.time))
        }, year, month, day).show()
    }

    /**
     * Shows a time picker dialog.
     *
     * @param editText The EditText to populate with selected time
     */
    private fun showTimePicker(editText: EditText) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val timeCalendar = Calendar.getInstance()
            timeCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            timeCalendar.set(Calendar.MINUTE, selectedMinute)
            editText.setText(timeFormat.format(timeCalendar.time))
        }, hour, minute, true).show()
    }

    /**
     * Sets up the save button click listener.
     */
    private fun setupSaveButton() {
        btnSaveExpense.setOnClickListener {
            saveExpense()
        }

        btnSelectPhoto.setOnClickListener {
            showPhotoOptions()
        }
    }

    /**
     * Shows dialog to choose between camera and gallery.
     */
    private fun showPhotoOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Add Receipt Photo")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera()
                1 -> openImagePicker()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    /**
     * Opens the camera to capture a photo.
     */
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_REQUEST)
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the image picker to select a photo.
     */
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    selectedPhotoUri = data.data
                    ivReceiptPhoto.setImageURI(selectedPhotoUri)
                    ivReceiptPhoto.visibility = View.VISIBLE
                }
                CAPTURE_IMAGE_REQUEST -> {
                    val imageBitmap = data.extras?.get("data") as? android.graphics.Bitmap
                    imageBitmap?.let {
                        ivReceiptPhoto.setImageBitmap(it)
                        ivReceiptPhoto.visibility = View.VISIBLE
                        // Convert bitmap to URI for upload
                        selectedPhotoUri = getImageUriFromBitmap(it)
                    }
                }
            }
        }
    }

    /**
     * Converts a Bitmap to a URI for uploading.
     */
    private fun getImageUriFromBitmap(bitmap: android.graphics.Bitmap): Uri {
        val bytes = java.io.ByteArrayOutputStream()
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "Receipt_${System.currentTimeMillis()}", null)
        return Uri.parse(path)
    }

    /**
     * Loads categories from Firestore and populates the spinner.
     */
    private fun loadCategories() {
        showLoading(true)
        val currentUser = auth.currentUser ?: return

        firestore.collection(COLLECTION_CATEGORIES)
            .whereEqualTo(FIELD_USER_ID, currentUser.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                categories.clear()
                categories.addAll(
                    querySnapshot.documents.map { document ->
                        Category.fromMap(document.id, document.data)
                    }
                )
                setupCategorySpinner()
                showLoading(false)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to load categories: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                showLoading(false)
            }
    }

    /**
     * Sets up the category spinner with loaded categories.
     */
    private fun setupCategorySpinner() {
        if (categories.isEmpty()) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                listOf("No categories available")
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
            return
        }

        val categoryNames = categories.map { it.name }
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategoryId = categories[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategoryId = ""
            }
        }
    }

    /**
     * Validates input and saves the expense to Firestore.
     */
    private fun saveExpense() {
        // Get input values
        val description = etDescription.text.toString().trim()
        val amountText = etAmount.text.toString().trim()
        val dateText = etDate.text.toString().trim()
        val startTime = etStartTime.text.toString().trim()
        val endTime = etEndTime.text.toString().trim()

        // Validate required fields
        if (selectedCategoryId.isEmpty()) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        if (description.isEmpty()) {
            etDescription.error = "Description is required"
            return
        }

        if (dateText.isEmpty()) {
            etDate.error = "Date is required"
            return
        }

        // Parse amount safely
        val amount = try {
            if (amountText.isEmpty()) 0.0 else amountText.toDouble()
        } catch (e: NumberFormatException) {
            etAmount.error = "Invalid amount"
            return
        }

        // Parse date
        val date = try {
            dateFormat.parse(dateText)
        } catch (e: Exception) {
            etDate.error = "Invalid date format"
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Upload photo if selected
        showLoading(true)
        btnSaveExpense.isEnabled = false

        if (selectedPhotoUri != null) {
            uploadPhotoAndSaveExpense(currentUser.uid, selectedCategoryId, description, date, startTime, endTime, amount)
        } else {
            saveExpenseToFirestore(currentUser.uid, selectedCategoryId, description, date, startTime, endTime, amount, "")
        }
    }

    /**
     * Uploads the selected photo to Firebase Storage and then saves the expense.
     */
    private fun uploadPhotoAndSaveExpense(
        userId: String,
        categoryId: String,
        description: String,
        date: Date?,
        startTime: String,
        endTime: String,
        amount: Double
    ) {
        selectedPhotoUri?.let { uri ->
            val filename = "expenses/${userId}/${UUID.randomUUID()}.jpg"
            val photoRef = storage.reference.child(filename)
            
            photoRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    // Get the download URL
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                        saveExpenseToFirestore(userId, categoryId, description, date, startTime, endTime, amount, downloadUrl.toString())
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to get download URL: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        showLoading(false)
                        btnSaveExpense.isEnabled = true
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to upload photo: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    showLoading(false)
                    btnSaveExpense.isEnabled = true
                }
        } ?: run {
            // No photo selected, save without photo
            saveExpenseToFirestore(userId, categoryId, description, date, startTime, endTime, amount, "")
        }
    }

    /**
     * Saves the expense to Firestore.
     */
    private fun saveExpenseToFirestore(
        userId: String,
        categoryId: String,
        description: String,
        date: Date?,
        startTime: String,
        endTime: String,
        amount: Double,
        photoUrl: String
    ) {
        val expense = Expense(
            userId = userId,
            categoryId = categoryId,
            description = description,
            date = date,
            startTime = startTime,
            endTime = endTime,
            amount = amount,
            photoUrl = photoUrl
        )

        firestore.collection(COLLECTION_EXPENSES)
            .add(expense.toMap())
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Expense saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
                clearInputs()
                showLoading(false)
                btnSaveExpense.isEnabled = true
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Failed to save expense: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                showLoading(false)
                btnSaveExpense.isEnabled = true
            }
    }

    /**
     * Clears all input fields after successful save.
     */
    private fun clearInputs() {
        etDescription.text.clear()
        etAmount.text.clear()
        etDate.text.clear()
        etStartTime.text.clear()
        etEndTime.text.clear()
        spinnerCategory.setSelection(0)
        selectedCategoryId = if (categories.isNotEmpty()) categories[0].id else ""
        selectedPhotoUri = null
        ivReceiptPhoto.visibility = View.GONE
    }

    /**
     * Shows or hides the loading indicator.
     *
     * @param show True to show loading, false to hide
     */
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
