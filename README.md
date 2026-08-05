# 🪙 KoinTrack - Modern EUR Personal Finance Manager

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.0-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android%208.0%2B-green.svg)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVVM-orange.svg)](https://developer.android.com/topic/architecture)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20%2B%20Material%203-purple.svg)](https://developer.android.com/jetpack/compose)

**KoinTrack** is an offline-first personal finance and expense-tracking application for Android, built with modern Android development standards. Features include dynamic dark/light theme support, custom Canvas spending curve charts, Jetpack DataStore budget caps, and thread-safe CSV report exports.

---

## 🌟 Key Features

* **Custom Dark/Light Aesthetic:** Dynamic system theme switching (`isSystemInDarkTheme()`) with a Deep Charcoal palette (`#121212`), side-by-side Income/Expense summary cards, and an interactive balance visibility toggle.
* **Canvas-Rendered Spending Curve:** Custom Jetpack Compose Canvas bezier curve chart tracking 7-day spending trends alongside category breakdown metrics.
* **Offline-First Persistence:** Reactive local storage engine built on Room Database and Kotlin `Flow` streams.
* **Jetpack DataStore Budgeting:** Persistent monthly spending limit caps with dynamic color threshold indicators (Green <70%, Yellow 70–90%, Red >90%).
* **Floating Bottom Navigation:** Elevated floating pill navigation bar (`RoundedCornerShape(32.dp)`) providing fluid transitions across `Dashboard`, `Add Expense`, and `History`.
* **Search, Filtering & CSV Export:** Search bar with multi-chip category/date filters, swipe-to-delete, and thread-safe CSV report generation via Android `FileProvider`.

---

## 🏗️ Architecture & Technical Stack

KoinTrack strictly follows **Clean Architecture** principles (Domain -> Data -> Presentation) combined with **MVVM** and **Unidirectional Data Flow (UDF)**:

```text
┌──────────────────────────────────────────────────────────────┐
│                      Presentation Layer                      │
│   Jetpack Compose UI | ViewModels | StateFlow | Canvas API   │
└──────────────────────────────┬───────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────┐
│                         Domain Layer                         │
│   Use Cases | Pure Kotlin Models | Repository Interfaces     │
└──────────────────────────────▲───────────────────────────────┘
                               │
                               │
┌──────────────────────────────────────────────────────────────┐
│                          Data Layer                          │
│     Room DAO | Preferences DataStore | Entity Mappers        │
└──────────────────────────────────────────────────────────────┘
```

* **Language:** 100% Kotlin
* **UI Engine:** Jetpack Compose, Material 3, Custom Canvas API
* **Dependency Injection:** Hilt (`@HiltAndroidApp`, `@HiltViewModel`)
* **Asynchronous Flow:** Kotlin Coroutines & `StateFlow`
* **Local Storage:** Room Database & Jetpack DataStore Preferences
* **Testing:** JUnit, Kotlinx Coroutines Test

---

## 🚀 Getting Started

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/Nishantptl/KoinTrack.git](https://github.com/Nishantptl/KoinTrack.git)
   cd KoinTrack
