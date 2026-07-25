# WeatherApp 🌤️

A modern, offline-capable Android Weather application built with Jetpack Compose, Kotlin, and the OpenWeatherMap API.

## 🏗️ Architecture

This project follows **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** pattern. It is structured into three main layers:

*   **Data Layer**: 
    *   **Room Database**: Acts as the single source of truth for offline support.
    *   **Retrofit**: Handles network requests to the Weather API.
    *   **Repository Pattern**: `WeatherRepositoryImpl` manages the logic for fetching from the network and caching to the local database.
*   **Domain Layer**: Contains the core business logic and models (`FullWeatherData`, `Resource`) that are independent of any framework.
*   **UI Layer (Presentation)**: 
    *   **Jetpack Compose**: Used for building a fully declarative UI.
    *   **StateFlow**: Employs reactive streams to update the UI from the ViewModel.

## 🚀 Setup Instructions

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/your-username/WeatherApp.git
    ```
2.  **API Key**:
    *   The project uses a bundled API key in `networkModule`. For production, it is recommended to move this to `local.properties`.
3.  **Build and Run**:
    *   Open the project in **Android Studio (Ladybug or newer)**.
    *   Sync Gradle and run the `app` module on an emulator or physical device.

## 🛠️ Key Features & Implementation Details

*   **Offline First**: The app checks for internet connectivity. If available, it fetches fresh data and updates the DB. If offline, it immediately falls back to the last cached data.
*   **Permissions**: Uses a modern `ActivityResultLauncher` flow to request location permissions directly on the Home Screen.
*   **Dynamic Background Sync**: Utilizes `WorkManager` to periodically refresh weather data in the background every 2 hours.
*   **Unit Conversion**: Supports toggling between Celsius and Fahrenheit with immediate UI updates.

## 📝 Assumptions & Fallbacks

*   **Location**: If the user denies location permissions and hasn't searched for a city, the app defaults to **Lucknow, India** as a fallback coordinates (26.85, 80.95).
*   **API Limits**: The implementation assumes a standard OpenWeatherMap API subscription. If the API returns an error or rate limit, the app serves cached data.
*   **Geocoder**: The app relies on the system `Geocoder` service for name-to-coordinate resolution. If the service is unavailable (e.g., on some emulators), a "Weather Data Not Available" snackbar is shown.

## 📦 Tech Stack

*   **UI**: Jetpack Compose, Material 3
*   **DI**: Koin
*   **Database**: Room
*   **Networking**: Retrofit, OkHttp, GSON
*   **Async**: Kotlin Coroutines & Flow
*   **Background Tasks**: WorkManager
