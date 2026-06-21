# SkyBrief - CP3406 Assessment 1 Utility App

SkyBrief is a focused Android utility app for quick daily outdoor decisions. Instead of presenting a full weather dashboard, it answers one practical question: **should the user go outside, prepare first, or stay indoors?**

The app uses live weather data from Open-Meteo, converts it into a compact comfort score, and displays only the information that is useful at a glance.

## Core features

- **Utility screen:** shows location, decision label, temperature, weather condition, comfort score, concise advice, and optional detailed metrics.
- **Settings screen:** lets the user change city, switch between Celsius and Fahrenheit, and show or hide detailed metrics.
- **Live API data:** uses Retrofit to fetch geocoding and weather forecast data from Open-Meteo.
- **Outdoor comfort calculation:** combines temperature, humidity, wind speed, rain probability, and UV index into one score.
- **Modern Android architecture:** uses ViewModel, StateFlow, Repository pattern, dependency injection with Hilt, and a separate domain logic layer.
- **Jetpack Compose UI:** uses Material Design 3 components including Scaffold, NavigationBar, Cards, Buttons, Switches, RadioButtons, and OutlinedTextField.
- **Testing support:** includes local unit tests for the outdoor comfort scoring and classification logic.

## Assessment requirement mapping

| Requirement | SkyBrief implementation |
|---|---|
| Kotlin and Android Studio | Kotlin Android project using Gradle Kotlin DSL. |
| Jetpack Compose layouts | Main utility screen and settings screen are implemented as modular Composables. |
| Material Design 3 | Uses Material 3 layout, navigation, cards, input controls, and typography. |
| App architecture | Uses ViewModel, Repository, Hilt dependency injection, and separated domain logic. |
| Web APIs using Retrofit | Uses Retrofit and Moshi to call Open-Meteo geocoding and forecast APIs. |
| Utility app purpose | Provides rapid at-a-glance guidance for a daily outdoor activity decision. |
| Settings screen | Controls city, temperature unit, and detailed metric visibility. |
| README | Documents purpose, features, architecture, running steps, and limitations. |

## Architecture

```text
UtilityApp Composables
        ↓
WeatherViewModel
        ↓
WeatherRepository interface
        ↓
WeatherRepositoryImpl
        ↓
WeatherApiService / Retrofit / Open-Meteo APIs
        ↓
ComfortCalculator domain logic
```

## Project structure

```text
app/src/main/java/com/noahjiang/skybrief/
├── MainActivity.kt
├── SkyBriefApplication.kt
├── data/
│   ├── remote/          # Retrofit API definitions and response models
│   └── repository/      # Repository contract, implementation, and dashboard model
├── di/                  # Hilt modules for repository and network dependencies
├── domain/              # Pure comfort score and decision calculation
└── ui/                  # ViewModel, Compose screens, and Material theme
```

## How to run

1. Open this project folder in Android Studio.
2. Wait for Gradle Sync to finish.
3. Select the `app` run configuration.
4. Run the app on an Android emulator or Android device.
5. Confirm the emulator or device has internet access.
6. Use the Settings tab to change the city, temperature unit, and detailed metric visibility.

## Suggested checks before submission

- Build and run the `app` module successfully.
- Test a valid city such as Singapore, Cairns, or Brisbane.
- Test an invalid city name and confirm the error message is readable.
- Switch between Celsius and Fahrenheit.
- Turn detailed metrics on and off.
- Run the `ComfortCalculatorTest` local tests if Android Studio is available.

## Design rationale

The app intentionally avoids overloaded weather-app functionality. The main screen prioritises one decision, one score, one temperature reading, and one advice message. Detailed values are available only when enabled from Settings. This keeps the user experience aligned with a utility app: quick, focused, and useful during everyday planning.

## Limitations

- Settings are not persistent because persistent settings are not required for this assessment.
- The app requires internet access for live weather data.
- The app uses city search rather than device location permission to keep the interaction simple and privacy-friendly.
- Future improvements could include local caching, automatic device location, dark theme refinement, Compose UI tests, and offline fallback data.
