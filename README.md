# AuraFit ⚡

AuraFit is a premium, AI-powered native Android fitness and wellness tracker. Drawing inspiration from modern "neural expressive designs" (such as the Gemini app layout), AuraFit features a true AMOLED dark theme, flowing gradients, and translucent glassmorphic cards. It houses a central pulsating **Neural Aura Core** visualizer reflecting your daily progress while connecting to Google Gemini AI to serve customized training suggestions.

---

## ✨ Features

### 🌟 Neural Aura Core
* **Dynamic Visual Feedback**: A central glowing, breathing, and rotating gradient sphere that visually communicates daily completion rates.
* **Goal Responsive**: Shifts colors automatically from cool blues (under 30%) to active purples/pinks (30%-70%) and warm oranges/magentas (70%+) as you log food, water, and sleep.

### 🍎 Nutrition & Hydration
* **Calorie Logging**: Log meals across breakfast, lunch, dinner, and snacks.
* **Macro Wheel**: Real-time breakdown of Protein, Carbs, and Fats to optimize diet distribution.
* **Hydration Tracker**: Easy one-tap logging to reach your 2500 mL water target.

### 🏋️ Gym & Workout Planner
* **Routine Logger**: Tracks specific exercises with sets, repetitions, and weights.
* **Calorie Burn Calculator**: Estimates and updates gym-related calorie burn directly on your dashboard.

### 🤖 AI Aura Coach (Powered by Gemini)
* **Direct Integration**: Connected to Google Gemini (`gemini-1.5-flash`) via the official Google AI Client SDK.
* **Smart Recommendations**: Evaluates logged sleep quality, macros, and workout intensity to propose custom meals, recovery suggestions, and routine changes.
* **Interactive Chat**: Includes quick prompt chips like *"Get Daily Review"*, *"Workout Check"*, or *"Snack Idea"*.

### ⏰ Notifications & Background Reminders
* **WorkManager Schedule**: Runs background tasks to prompt local notifications reminding you to hydrate, log meals, and start scheduled workouts.

---

## 🛠️ Architecture & Tech Stack

* **UI**: 100% Jetpack Compose for fluid rendering of gradients, canvas overlays, and glassmorphic cards.
* **Database**: Room Database (API 2.7.0) for fast, secure, local offline storage of logs.
* **Navigation**: Jetpack Compose Navigation 3.
* **Language**: Kotlin with Coroutines & Flow for reactive state updates.
* **AI Core**: Google AI Client SDK (`com.google.ai.client.generativeai`).
* **Platform Support**: SDK 36 (Android 15+ compatible features).

---

## 🚀 Setup & Installation

### Prerequisites
* **Android Studio** (Koala / Ladybug or newer recommended).
* **JDK 17** configured as your Gradle JVM.
* **API 35+** Virtual Device (Emulator) or physical device running Android 15+.

### Building from Source
1. Clone the repository:
   ```bash
   git clone https://github.com/Ayush3657/Aurafit.git
   cd Aurafit
   ```
2. Build the project using the gradle wrapper:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run the app on your connected device:
   ```bash
   ./gradlew installDebug
   ```

### Configuring Gemini AI
To activate the Coach chat recommendations:
1. Go to [Google AI Studio](https://aistudio.google.com/) and grab a free API Key.
2. In the app, navigate to the **Coach** tab, open the setup box, and input your API key. (Stored securely in local app preferences).

---

## 🎨 Theme Details
AuraFit is optimized for AMOLED screens using HSL-based dark colors to prevent eye strain during late-night workouts:
* **Background**: `#050505` (AMOLED Dark)
* **Surface**: `#0CFFFFFF` (Glass Overlay)
* **Accents**: Indigo Purple, Fuchsia Pink, Sky Blue, and Cyan Green.
