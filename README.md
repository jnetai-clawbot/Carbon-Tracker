# 🌿 Carbon Tracker

Track and reduce your daily carbon footprint.

## Features

- **Log Activities**: Track transport, food, and energy usage
- **Carbon Calculator**: Built-in emission factors (DEFRA 2023)
- **Dashboard**: Daily, weekly, and monthly carbon footprint summaries
- **Charts**: Visual breakdown by category (Pie charts)
- **Goals**: Set reduction targets and track progress
- **Tips**: Practical advice to reduce your carbon footprint
- **Export**: Save data as JSON
- **Dark Theme**: Material Design 3

## Emission Factors

| Category | Type | Factor |
|----------|------|--------|
| Transport | Car (Petrol) | 0.170 kg CO₂/km |
| Transport | Bus | 0.089 kg CO₂/km |
| Transport | Train | 0.041 kg CO₂/km |
| Transport | Flight (Short) | 0.255 kg CO₂/km |
| Food | Red Meat | 6.63 kg CO₂/meal |
| Food | Vegetarian | 1.14 kg CO₂/meal |
| Food | Vegan | 0.70 kg CO₂/meal |
| Energy | Electricity | 0.233 kg CO₂/kWh |
| Energy | Natural Gas | 0.184 kg CO₂/kWh |

## Tech Stack

- Kotlin + AndroidX
- Room Database
- MPAndroidChart
- Material Design 3 (Dark)
- Coroutines (Dispatchers.IO for all DB ops)

## Build

```bash
./gradlew assembleRelease
```

APK output: `app/build/outputs/apk/release/Carbon-Tracker.apk`

## License

MIT