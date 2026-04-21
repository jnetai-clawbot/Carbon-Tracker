package com.jnetai.carbontracker.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jnetai.carbontracker.CarbonTrackerApp
import com.jnetai.carbontracker.R
import com.jnetai.carbontracker.calculator.DateUtils
import com.jnetai.carbontracker.data.entity.Goal
import com.jnetai.carbontracker.databinding.ActivityGoalsBinding
import com.jnetai.carbontracker.databinding.DialogAddGoalBinding
import com.jnetai.carbontracker.ui.adapters.GoalAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GoalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalsBinding
    private val app by lazy { application as CarbonTrackerApp }
    private lateinit var goalAdapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        goalAdapter = GoalAdapter { goal ->
            deleteGoal(goal)
        }
        binding.rvGoals.layoutManager = LinearLayoutManager(this)
        binding.rvGoals.adapter = goalAdapter

        binding.fabAddGoal.setOnClickListener { showAddGoalDialog() }
        loadGoals()
    }

    private fun showAddGoalDialog() {
        val dialogBinding = DialogAddGoalBinding.inflate(layoutInflater)
        var selectedPeriod = "daily"
        var selectedCategory = "overall"

        dialogBinding.periodChipGroup.setOnCheckedStateChangeListener { group, _ ->
            selectedPeriod = when (group.checkedChipId) {
                R.id.chipWeekly -> "weekly"
                R.id.chipMonthly -> "monthly"
                else -> "daily"
            }
        }

        dialogBinding.categoryChipGroup.setOnCheckedStateChangeListener { group, _ ->
            selectedCategory = when (group.checkedChipId) {
                R.id.chipTransport -> "transport"
                R.id.chipFood -> "food"
                R.id.chipEnergy -> "energy"
                else -> "overall"
            }
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.add_goal)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                val name = dialogBinding.etGoalName.text.toString().ifBlank {
                    "Reduce ${selectedCategory} emissions"
                }
                val target = dialogBinding.etGoalTarget.text.toString().toDoubleOrNull()
                if (target == null || target <= 0) {
                    Toast.makeText(this, "Enter a valid target", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val goal = Goal(
                    name = name,
                    targetEmission = target,
                    period = selectedPeriod,
                    category = selectedCategory
                )

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        app.goalRepository.insert(goal)
                    }
                    loadGoals()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun loadGoals() {
        lifecycleScope.launch {
            val goals = withContext(Dispatchers.IO) { app.goalRepository.getActiveGoalsList() }
            if (goals.isEmpty()) {
                binding.rvGoals.visibility = android.view.View.GONE
                binding.tvNoGoals.visibility = android.view.View.VISIBLE
            } else {
                binding.rvGoals.visibility = android.view.View.VISIBLE
                binding.tvNoGoals.visibility = android.view.View.GONE

                // Calculate progress for each goal
                val goalsWithProgress = goals.map { goal ->
                    val (start, end) = getTimeRangeForPeriod(goal.period)
                    val current = if (goal.category == "overall") {
                        app.activityRepository.getTotalEmissionInRange(start, end)
                    } else {
                        app.activityRepository.getCategoryEmissionInRange(goal.category, start, end)
                    }
                    goal to current
                }

                goalAdapter.submitList(goalsWithProgress)
            }
        }
    }

    private fun getTimeRangeForPeriod(period: String): Pair<Long, Long> {
        return when (period) {
            "daily" -> DateUtils.getTodayStart() to DateUtils.getTodayEnd()
            "weekly" -> DateUtils.getThisWeekStart() to DateUtils.getThisWeekEnd()
            "monthly" -> DateUtils.getThisMonthStart() to DateUtils.getThisMonthEnd()
            else -> DateUtils.getTodayStart() to DateUtils.getTodayEnd()
        }
    }

    private fun deleteGoal(goal: Goal) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                app.goalRepository.deleteById(goal.id)
            }
            loadGoals()
        }
    }
}