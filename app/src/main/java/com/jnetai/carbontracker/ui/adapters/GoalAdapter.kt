package com.jnetai.carbontracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.carbontracker.R
import com.jnetai.carbontracker.data.entity.Goal
import com.jnetai.carbontracker.databinding.ItemGoalBinding

class GoalAdapter(
    private val onDelete: (Goal) -> Unit
) : RecyclerView.Adapter<GoalAdapter.ViewHolder>() {

    private var items = listOf<Pair<Goal, Double>>()

    inner class ViewHolder(val binding: ItemGoalBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (goal, currentEmission) = items[position]
        with(holder.binding) {
            tvGoalName.text = goal.name

            val periodLabel = when (goal.period) {
                "daily" -> "Daily"
                "weekly" -> "Weekly"
                "monthly" -> "Monthly"
                else -> goal.period
            }
            val categoryLabel = when (goal.category) {
                "overall" -> "Overall"
                "transport" -> "Transport"
                "food" -> "Food"
                "energy" -> "Energy"
                else -> goal.category
            }

            tvGoalTarget.text = "Target: ${String.format("%.1f", goal.targetEmission)} kg CO₂ $periodLabel ($categoryLabel)"

            val progress = if (goal.targetEmission > 0.0) {
                (currentEmission / goal.targetEmission * 100.0).coerceIn(0.0, 100.0)
            } else 0.0
            progressGoal.progress = progress.toInt()

            if (currentEmission <= goal.targetEmission) {
                tvGoalStatus.text = "${String.format("%.1f", currentEmission)} / ${String.format("%.1f", goal.targetEmission)} kg — On track! 🎉"
                tvGoalStatus.setTextColor(holder.itemView.context.getColor(R.color.md_theme_dark_primary))
            } else {
                tvGoalStatus.text = "${String.format("%.1f", currentEmission)} / ${String.format("%.1f", goal.targetEmission)} kg — Over target ⚠️"
                tvGoalStatus.setTextColor(holder.itemView.context.getColor(R.color.md_theme_dark_error))
            }

            btnDeleteGoal.setOnClickListener { onDelete(goal) }
        }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<Pair<Goal, Double>>) {
        items = newItems
        notifyDataSetChanged()
    }
}