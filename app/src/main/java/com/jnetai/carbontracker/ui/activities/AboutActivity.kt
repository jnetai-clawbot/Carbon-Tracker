package com.jnetai.carbontracker.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.jnetai.carbontracker.BuildConfig
import com.jnetai.carbontracker.R
import com.jnetai.carbontracker.databinding.ActivityAboutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        // Version from PackageInfo
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        binding.tvVersion.text = getString(R.string.version, versionName, versionCode)

        binding.btnCheckUpdates.setOnClickListener { checkForUpdates() }

        binding.btnShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
            }
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }

        binding.btnGithub.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jnetai-clawbot/Carbon-Tracker")))
        }
    }

    private fun checkForUpdates() {
        binding.tvUpdateStatus.visibility = View.VISIBLE
        binding.tvUpdateStatus.text = getString(R.string.checking_updates)

        lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url("https://api.github.com/repos/jnetai-clawbot/Carbon-Tracker/releases/latest")
                        .build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val json = JSONObject(response.body?.string() ?: "")
                        json.optString("tag_name", "")
                    } else {
                        null
                    }
                }

                withContext(Dispatchers.Main) {
                    if (result != null) {
                        val latestVersion = result.trimStart('v')
                        if (latestVersion > BuildConfig.VERSION_NAME) {
                            binding.tvUpdateStatus.text = getString(R.string.update_available, result)
                            binding.tvUpdateStatus.setTextColor(getColor(R.color.md_theme_dark_error))
                            // Open GitHub release page
                            binding.tvUpdateStatus.setOnClickListener {
                                startActivity(Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/jnetai-clawbot/Carbon-Tracker/releases/latest")))
                            }
                        } else {
                            binding.tvUpdateStatus.text = getString(R.string.up_to_date)
                            binding.tvUpdateStatus.setTextColor(getColor(R.color.md_theme_dark_primary))
                        }
                    } else {
                        binding.tvUpdateStatus.text = getString(R.string.update_error)
                        binding.tvUpdateStatus.setTextColor(getColor(R.color.md_theme_dark_error))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.tvUpdateStatus.text = getString(R.string.update_error)
                    binding.tvUpdateStatus.setTextColor(getColor(R.color.md_theme_dark_error))
                }
            }
        }
    }
}