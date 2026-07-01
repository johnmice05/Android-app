# Android App

A modern Android application built with Kotlin, Jetpack Compose, and MVVM architecture.

## Project Structure

```
app/src/main/java/com/example/androidapp/
├── MyApplication.kt                 # Hilt Application entry point
├── ui/
│   ├── MainActivity.kt              # Main activity
│   ├── AppNavigation.kt             # Navigation setup
│   ├── navigation/
│   │   ├── NavGraph.kt              # Navigation graph definition
│   │   └── Screen.kt                # Screen routes
│   ├── screens/
│   │   └── home/
│   │       ├── HomeScreen.kt        # Home screen composable
│   │       └── HomeViewModel.kt     # Home screen view model
│   └── theme/
│       ├── Theme.kt                 # Material 3 theme configuration
│       ├── Color.kt                 # Color definitions
│       └── Type.kt                  # Typography definitions
├── data/
│   └── db/
│       └── AppDatabase.kt           # Room database configuration
└── di/
    └── AppModule.kt                 # Dependency injection module
```

## Technologies Used

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Navigation Compose
- **Database**: Room Database
- **Dependency Injection**: Hilt
- **Minimum SDK**: 24
- **Target SDK**: 34

## Getting Started

### Prerequisites
- Android Studio 2023.1 or higher
- Kotlin 1.9.20 or higher
- Android SDK 34

### Setup

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run on an emulator or physical device

## Adding New Features

### Adding a New Screen

1. Create a new screen package under `ui/screens/`
2. Create a composable screen file (e.g., `NewScreen.kt`)
3. Create a corresponding ViewModel (e.g., `NewViewModel.kt`)
4. Add the screen route to `navigation/Screen.kt`
5. Add the navigation route to `navigation/NavGraph.kt`

### Adding Database Entities

1. Create entity classes in `data/db/entities/`
2. Create DAOs (Data Access Objects) in `data/db/dao/`
3. Add the entity and DAO to `AppDatabase.kt`

## Project Initialization

This project was initialized with:
- MVVM architecture pattern
- Clean folder structure
- Material 3 design system
- Navigation Compose for screen routing
- Room Database for local persistence
- Hilt for dependency injection
- Coroutines for async operations

More features will be added as the project develops.

## License

MIT License - see LICENSE file for details
