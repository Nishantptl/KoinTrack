---
trigger: always_on
---

# KoinTrack Architecture & Coding Guidelines

You are an expert Android Developer acting as an AI assistant on the KoinTrack project.
Adhere strictly to the following architectural constraints, stack selections, and coding standards.

---

### 1. Tech Stack & Libraries
* **Language:** 100% Kotlin (Strictly avoid Java unless required for backward compatibility).
* **UI Engine:** Jetpack Compose with Material 3 components.
* **Architecture:** Clean Architecture (Domain -> Data -> Presentation) + MVVM / MVI.
* **Dependency Injection:** Hilt (`@HiltAndroidApp`, `@HiltViewModel`, `@Inject`).
* **Asynchronous Programming:** Kotlin Coroutines & Reactive `StateFlow` / `SharedFlow`.
* **Local Persistence:** Room Database with Kotlin Flow returns for offline-first reactive UI.
* **Networking:** Retrofit + Kotlinx Serialization for exchange rate APIs.

---

### 2. Architecture & Layer Boundaries

* **Domain Layer (`:domain` or `package domain`)**
  * Contains pure Kotlin logic (No Android dependencies, no UI components, no Room/Retrofit imports).
  * Houses **Models**, **Repository Interfaces**, and **Use Cases**.
  * Use Cases must do *one thing* well (e.g., `ConvertCurrencyUseCase`, `GetMonthlyExpensesUseCase`).

* **Data Layer (`:data` or `package data`)**
  * Implements Domain repository interfaces.
  * Handles local storage (Room DAOs, Entities) and remote network endpoints (Retrofit interfaces).
  * Responsible for mapping internal Data Entities (`TransactionEntity`) to Domain Models (`Transaction`).

* **Presentation Layer (`:ui` or `package ui`)**
  * Powered by Jetpack Compose.
  * **ViewModels:** Inject Use Cases via Hilt. Expose UI state via `StateFlow<HomeScreenState>` using unidirectional data flow (UDF).
  * **UI State:** Model screen state as sealed interfaces/classes (`Loading`, `Success(data)`, `Error(message)`).
  * **Composables:** Stateless UI composables separated from stateful screen wrappers.

---

### 3. Code Style & Clean Code Standard
* **Explicit Naming:** Use clear, self-describing names (`CalculateMonthlyBudgetUseCase` rather than `CalcBudget`).
* **Immutability:** Default to `val` and immutable collections (`List`, `Map`).
* **Error Handling:** Wrap repository operations in `Result<T>` or custom Domain Error types. Avoid silent crash scenarios.
* **Single Source of Truth:** Room database acts as the single source of truth for app state. Network calls update Room, and Room updates the UI flow.

---

### 4. Code Generation Instructions
When generating or modifying code for KoinTrack:
1. Always present changes broken down by architectural layer (Domain -> Data -> UI).
2. Write unit-testable components with dependency injection.
3. Provide explicit Jetpack Compose previews (`@Preview`) for custom UI components.
4. Keep functions concise and single-purpose.