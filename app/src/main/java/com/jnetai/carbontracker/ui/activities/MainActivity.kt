package com.jnetai.carbontracker.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.jnetai.carbontracker.CarbonTrackerApp
import com.jnetai.carbontracker.R
import com.jnetai.carbontracker.calculator.DateUtils
import com.jnetai.carbontracker.calculator.EmissionFactors
import com.jnetai.carbontracker.data.entity.CarbonActivity
import com.jnetai.carbontracker.databinding.ActivityMainBinding
import com.jnetai.carbontracker.ui.adapters.ActivityAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ActivityAdapter
    private val app by lazy { application as CarbonTrackerApp }
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    enum class Period { TODAY, WEEK, MONTH }
    private var currentPeriod = Period.TODAY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        adapter = ActivityAdapter { activity ->
            deleteActivity(activity)
        }
        binding.rvRecentActivities.layoutManager = LinearLayoutManager(this)
        binding.rvRecentActivities.adapter = adapter

        setupPeriodChips()
        setupFab()
        setupPieChart()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_goals -> { startActivity(Intent(this, GoalsActivity::class.java)); true }
            R.id.action_tips -> { startActivity(Intent(this, TipsActivity::class.java)); true }
            R.id.action_export -> { startActivity(Intent(this, ExportActivity::class.java)); true }
            R.id.action_about -> { startActivity(Intent(this, AboutActivity::class.java)); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupPeriodChips() {
        binding.periodChips.setOnCheckedStateChangeListener { _, _ ->
            currentPeriod = when {
                binding.chipToday.isChecked -> Period.TODAY
                binding.chipWeek.isChecked -> Period.WEEK
                binding.chipMonth.isChecked -> Period.MONTH
                else -> Period.TODAY
            }
            loadData()
        }
    }

    private fun setupFab() {
        binding.fabLog.setOnClickListener {
            startActivity(Intent(this, LogActivityActivity::class.java))
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 50f
            transparentCircleRadius = 55f
            setUsePercentValues(true)
            setEntryLabelColor(android.graphics.Color.WHITE)
            setEntryLabelTextSize(12f)
            legend.isEnabled = true
            legend.textColor = android.graphics.Color.WHITE
        }
    }

    private fun getTimeRange(): Pair<Long, Long> {
        return when (currentPeriod) {
            Period.TODAY -> DateUtils.getTodayStart() to DateUtils.getTodayEnd()
            Period.WEEK -> DateUtils.getThisWeekStart() to DateUtils.getThisWeekEnd()
            Period.MONTH -> DateUtils.getThisMonthStart() to DateUtils.getThisMonthEnd()
        }
    }

    private fun loadData() {
        val (start, end) = getTimeRange()
        lifecycleScope.launch {
            val totalEmission = app.activityRepository.getTotalEmissionInRange(start, end)
            val transportEmission = app.activityRepository.getCategoryEmissionInRange("transport", start, end)
            val foodEmission = app.activityRepository.getCategoryEmissionInRange("food", start, end)
            val energyEmission = app.activityRepository.getCategoryEmissionInRange("energy", start, end)
            val activities = app.activityRepository.getActivitiesInRange(start, end)

            withContext(Dispatchers.Main) {
                binding.tvTotalEmission.text = String.format("%.2f", totalEmission)
                binding.tvTransportEmission.text = String.format("%.2f", transportEmission)
                binding.tvFoodEmission.text = String.format("%.2f", foodEmission)
                binding.tvEnergyEmission.text = String.format("%.2f", energyEmission)

                updatePieChart(transportEmission, foodEmission, energyEmission)

                if (activities.isEmpty()) {
                    binding.rvRecentActivities.visibility = android.view.View.GONE
                    binding.tvNoActivities.visibility = android.view.View.VISIBLE
                } else {
                    binding.rvRecentActivities.visibility = android.view.View.VISIBLE
                    binding.tvNoActivities.visibility = android.view.View.GONE
                    adapter.submitList(activities.take(10))
                }
            }
        }
    }

    private fun updatePieChart(transport: Double, food: Double, energy: Double) {
        val total = transport + food + energy
        if (total == 0.0) {
            binding.pieChart.visibility = android.view.View.GONE
            binding.tvNoChartData.visibility = android.view.View.VISIBLE
            return
        }
        binding.pieChart.visibility = android.view.View.VISIBLE
        binding.tvNoChartData.visibility = android.view.View.GONE

        val entries = mutableListOf<PieEntry>()
        if (transport > 0) entries.add(PieEntry(transport.toFloat(), "Transport"))
        if (food > 0) entries.add(PieEntry(food.toFloat(), "Food"))
        if (energy > 0) entries.add(PieEntry(energy.toFloat(), "Energy"))

        val dataSet = PieDataSet(entries, "").apply {
            colors = mutableListOf<Int>().apply {
                if (transport > 0) add(getColor(R.color.chart_transport))
                if (food > 0) add(getColor(R.color.chart_food))
                if (energy > 0) add(getColor(R.color.chart_energy))
            }
            valueTextSize = 14f
            valueTextColor = android.graphics.Color.WHITE
        }

        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.invalidate()
    }

    private fun deleteActivity(activity: CarbonActivity) {
        lifecycleScope.launch {
            app.activityRepository.delete(activity)
            loadData()
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}