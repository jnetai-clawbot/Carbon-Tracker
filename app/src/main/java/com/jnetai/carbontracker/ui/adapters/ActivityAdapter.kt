package com.jnetai.carbontracker.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.carbontracker.R
import com.jnetai.carbontracker.calculator.EmissionFactors
import com.jnetai.carbontracker.data.entity.CarbonActivity
import com.jnetai.carbontracker.databinding.ItemActivityBinding
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter(
    private val onDelete: (CarbonActivity) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    private var items = listOf<CarbonActivity>()
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    inner class ViewHolder(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = items[position]
        with(holder.binding) {
            val subcategories = EmissionFactors.getSubcategoriesForCategory(activity.category)
            val displayName = subcategories.find { it.first == activity.subcategory }?.second
                ?: activity.subcategory.replaceFirstChar { it.uppercase() }

            tvIcon.text = when (activity.category) {
                "transport" -> "🚗"
                "food" -> "🍽️"
                "energy" -> "⚡"
                else -> "📋"
            }

            tvSubcategory.text = displayName
            tvDetails.text = "${String.format("%.1f", activity.value)} ${EmissionFactors.getUnitForCategory(activity.category)} • ${dateFormat.format(Date(activity.date))}"
            if (activity.notes.isNotBlank()) {
                tvDetails.append(" • ${activity.notes}")
            }
            tvEmission.text = String.format("%.2f kg", activity.emission)

            root.setOnLongClickListener {
                onDelete(activity)
                true
            }
        }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<CarbonActivity>) {
        items = newItems
        notifyDataSetChanged()
    }
}