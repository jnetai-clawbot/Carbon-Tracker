package com.jnetai.carbontracker.calculator

/**
 * Carbon emission factors (kg CO2 per unit)
 * Sources: UK DEFRA 2023, IPCC guidelines
 */
object EmissionFactors {

    // Transport (kg CO2 per km)
    const val CAR = 0.170       // average petrol car
    const val CAR_ELECTRIC = 0.053
    const val BUS = 0.089       // bus per passenger km
    const val TRAIN = 0.041     // national rail per passenger km
    const val FLIGHT_SHORT = 0.255  // short haul (< 1500 km) per passenger km
    const val FLIGHT_LONG = 0.195   // long haul per passenger km
    const val BIKE = 0.0        // cycling
    const val WALKING = 0.0     // walking
    const val MOTORCYCLE = 0.113

    // Food (kg CO2 per meal)
    const val MEAT_RED = 6.63       // red meat meal
    const val MEAT_WHITE = 2.52     // white meat meal
    const val FISH = 2.10           // fish meal
    const val VEGGIE = 1.14         // vegetarian meal
    const val VEGAN = 0.70          // vegan meal

    // Energy (kg CO2 per kWh)
    const val ELECTRICITY = 0.233   // UK grid average
    const val NATURAL_GAS = 0.184   // per kWh
    const val HEATING_OIL = 0.266   // per kWh
    const val LPG = 0.215           // per kWh

    // Map subcategories to factors
    fun getFactor(category: String, subcategory: String): Double {
        return when (category) {
            "transport" -> when (subcategory) {
                "car" -> CAR
                "car_electric" -> CAR_ELECTRIC
                "bus" -> BUS
                "train" -> TRAIN
                "flight_short" -> FLIGHT_SHORT
                "flight_long" -> FLIGHT_LONG
                "bike" -> BIKE
                "walking" -> WALKING
                "motorcycle" -> MOTORCYCLE
                else -> 0.0
            }
            "food" -> when (subcategory) {
                "red_meat" -> MEAT_RED
                "white_meat" -> MEAT_WHITE
                "fish" -> FISH
                "veggie" -> VEGGIE
                "vegan" -> VEGAN
                else -> 0.0
            }
            "energy" -> when (subcategory) {
                "electricity" -> ELECTRICITY
                "gas" -> NATURAL_GAS
                "heating_oil" -> HEATING_OIL
                "lpg" -> LPG
                else -> 0.0
            }
            else -> 0.0
        }
    }

    fun calculateEmission(category: String, subcategory: String, value: Double): Double {
        val factor = getFactor(category, subcategory)
        return ((factor * value * 100.0).roundToLong()).toDouble() / 100.0  // round to 2 dp
    }

    fun getUnitForCategory(category: String): String {
        return when (category) {
            "transport" -> "km"
            "food" -> "meals"
            "energy" -> "kWh"
            else -> ""
        }
    }

    fun getSubcategoriesForCategory(category: String): List<Pair<String, String>> {
        return when (category) {
            "transport" -> listOf(
                "car" to "Car (Petrol)",
                "car_electric" to "Car (Electric)",
                "bus" to "Bus",
                "train" to "Train",
                "flight_short" to "Flight (Short Haul)",
                "flight_long" to "Flight (Long Haul)",
                "motorcycle" to "Motorcycle",
                "bike" to "Bicycle",
                "walking" to "Walking"
            )
            "food" -> listOf(
                "red_meat" to "Red Meat",
                "white_meat" to "White Meat",
                "fish" to "Fish",
                "veggie" to "Vegetarian",
                "vegan" to "Vegan"
            )
            "energy" -> listOf(
                "electricity" to "Electricity",
                "gas" to "Natural Gas",
                "heating_oil" to "Heating Oil",
                "lpg" to "LPG"
            )
            else -> emptyList()
        }
    }

    private fun Double.roundToLong(): Long = kotlin.math.round(this).toLong()
}