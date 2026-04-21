package com.jnetai.carbontracker.ui.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.carbontracker.CarbonTrackerApp
import com.jnetai.carbontracker.R
import com.jnetai.carbontracker.calculator.DateUtils
import com.jnetai.carbontracker.calculator.EmissionFactors
import com.jnetai.carbontracker.data.entity.CarbonActivity
import com.jnetai.carbontracker.databinding.ActivityLogActivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class LogActivityActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogActivityBinding
    private val app by lazy { application as CarbonTrackerApp }
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private var selectedDate = System.currentTimeMillis()
    private var currentCategory = ""
    private var currentSubcategory = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupCategoryDropdown()
        setupDatePicker()
        setupLiveCalculation()
        setupSaveButton()

        // Set default date
        binding.etDate.setText(dateFormat.format(Date(selectedDate)))
    }

    private fun setupCategoryDropdown() {
        val categories = listOf("Transport", "Food", "Energy")
        val categoryKeys = listOf("transport", "food", "energy")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        (binding.actvCategory as AutoCompleteTextView).setAdapter(adapter)

        binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
            currentCategory = categoryKeys[position]
            currentSubcategory = ""
            binding.actvSubcategory.setText("")
            updateSubcategoryDropdown()
            updateUnit()
            updateCalculation()
        }
    }

    private fun updateSubcategoryDropdown() {
        val subcategories = EmissionFactors.getSubcategoriesForCategory(currentCategory)
        val names = subcategories.map { it.second }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, names)
        (binding.actvSubcategory as AutoCompleteTextView).setAdapter(adapter)

        binding.actvSubcategory.setOnItemClickListener { _, _, position, _ ->
            currentSubcategory = subcategories[position].first
            updateCalculation()
        }
    }

    private fun updateUnit() {
        val unit = EmissionFactors.getUnitForCategory(currentCategory)
        binding.tilValue.hint = "${binding.root.context.getString(R.string.enter_value)} ($unit)"
        binding.tvUnit.text = "Unit: $unit"
    }

    private fun setupLiveCalculation() {
        binding.etValue.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { updateCalculation() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateCalculation() {
        val value = binding.etValue.text.toString().toDoubleOrNull()
        if (value != null && currentCategory.isNotEmpty() && currentSubcategory.isNotEmpty()) {
            val emission = EmissionFactors.calculateEmission(currentCategory, currentSubcategory, value)
            binding.tvCalculatedEmission.text = String.format("%.2f kg CO₂", emission)
        } else {
            binding.tvCalculatedEmission.text = "0.00 kg CO₂"
        }
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDate }
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val newCal = Calendar.getInstance().apply {
                        set(year, month, day)
                        set(Calendar.HOUR_OF_DAY, 12)
                    }
                    selectedDate = newCal.timeInMillis
                    binding.etDate.setText(dateFormat.format(Date(selectedDate)))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val value = binding.etValue.text.toString().toDoubleOrNull()
            if (currentCategory.isEmpty()) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (currentSubcategory.isEmpty()) {
                Toast.makeText(this, "Please select a type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (value == null || value <= 0) {
                Toast.makeText(this, "Please enter a valid value", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emission = EmissionFactors.calculateEmission(currentCategory, currentSubcategory, value)
            val activity = CarbonActivity(
                category = currentCategory,
                subcategory = currentSubcategory,
                value = value,
                emission = emission,
                date = selectedDate,
                notes = binding.etNotes.text.toString()
            )

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    app.activityRepository.insert(activity)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LogActivityActivity, R.string.saved, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}