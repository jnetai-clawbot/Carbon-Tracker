package com.jnetai.carbontracker.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jnetai.carbontracker.R
import com.jnetai.carbontracker.databinding.ActivityTipsBinding
import com.google.android.material.card.MaterialCardView
import android.widget.TextView
import android.view.Gravity

class TipsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTipsBinding

    data class Tip(val emoji: String, val text: String)

    private val tips = listOf(
        Tip("🚗", "Combine errands into one trip to reduce driving distance. Carpooling can cut emissions by 50%."),
        Tip("🚌", "Take public transport instead of driving. A bus carries 30+ people for the same road space as 2 cars."),
        Tip("🚂", "Train travel produces 75% less CO₂ than flying for the same distance."),
        Tip("🥩", "Reducing red meat consumption by one meal per week saves ~345 kg CO₂ per year."),
        Tip("🥦", "Going plant-based for 2 days a week can reduce your food carbon footprint by 25%."),
        Tip("🇻🇬", "A fully plant-based diet produces about 50% less CO₂ than a meat-heavy diet."),
        Tip("💡", "Switch to LED bulbs — they use 75% less energy and last 25x longer than incandescent."),
        Tip("🌡️", "Lower your thermostat by 1°C to save ~300 kg CO₂ per year on heating."),
        Tip("🔌", "Unplug chargers and devices when not in use. Standby power accounts for 5-10% of household energy."),
        Tip("☀️", "Consider solar panels — even partial coverage can offset 1-2 tonnes of CO₂ per year."),
        Tip("🚿", "Shorter showers save both water and the energy needed to heat it."),
        Tip("🛒", "Buy local and seasonal produce to reduce transport emissions in your food supply chain."),
        Tip("✈️", "One transatlantic flight produces ~1.6 tonnes of CO₂ — equivalent to a year of driving for some."),
        Tip("🌳", "Plant trees or support reforestation. A single tree absorbs ~22 kg of CO₂ per year."),
        Tip("🚲", "Cycling for trips under 5 km is faster than driving in urban areas and produces zero emissions."),
        Tip("🏠", "Improve home insulation — proper insulation can reduce heating needs by 30%."),
        Tip("🧺", "Wash clothes at 30°C instead of 40°C — uses 40% less energy per wash."),
        Tip("📱", "Keep devices longer — manufacturing a new phone produces 70-85 kg of CO₂."),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }

        tips.forEach { tip ->
            val card = MaterialCardView(this).apply {
                radius = 12f
                cardElevation = 2f
                useCompatPadding = true
            }

            val layout = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(32, 32, 32, 32)
            }

            val emojiView = TextView(this).apply {
                text = tip.emoji
                textSize = 28f
            }

            val textView = TextView(this).apply {
                text = tip.text
                setPadding(24, 0, 0, 0)
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
            }

            layout.addView(emojiView)
            layout.addView(textView)
            card.addView(layout)
            binding.tipsContainer.addView(card)
        }
    }
}