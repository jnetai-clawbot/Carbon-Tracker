package com.jnetai.carbontracker.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.jnetai.carbontracker.CarbonTrackerApp
import com.jnetai.carbontracker.R
import com.jnetai.carbontracker.databinding.ActivityExportBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ExportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportBinding
    private val app by lazy { application as CarbonTrackerApp }
    private val gson = GsonBuilder().setPrettyPrinting().create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnExportJson.setOnClickListener { exportData() }
        binding.btnShareExport.setOnClickListener { shareData() }
    }

    private fun exportData() {
        lifecycleScope.launch {
            val activities = withContext(Dispatchers.IO) {
                app.activityRepository.getAllActivitiesForExport()
            }

            if (activities.isEmpty()) {
                Toast.makeText(this@ExportActivity, "No data to export", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val json = gson.toJson(mapOf(
                "exportDate" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).format(Date()),
                "totalActivities" to activities.size,
                "activities" to activities
            ))

            val file = File(filesDir, "carbon_tracker_export.json")
            withContext(Dispatchers.IO) { file.writeText(json) }

            Toast.makeText(this@ExportActivity, R.string.export_success, Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareData() {
        lifecycleScope.launch {
            val activities = withContext(Dispatchers.IO) {
                app.activityRepository.getAllActivitiesForExport()
            }

            if (activities.isEmpty()) {
                Toast.makeText(this@ExportActivity, "No data to export", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val json = gson.toJson(mapOf(
                "exportDate" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).format(Date()),
                "totalActivities" to activities.size,
                "activities" to activities
            ))

            val file = File(cacheDir, "carbon_tracker_share.json")
            withContext(Dispatchers.IO) { file.writeText(json) }

            val uri = FileProvider.getUriForFile(
                this@ExportActivity,
                "${packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Carbon Data"))
        }
    }
}