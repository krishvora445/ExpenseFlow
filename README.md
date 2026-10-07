# 💳 ExpenseFlow - Android Expense & Budget Tracker

A modern, fast, and intuitive Expense & Budget Tracking application built natively for Android using Kotlin, Material 3 Design, and custom numeric keypad calculation.

![ExpenseFlow Screenshot](phone_screen.png)

---

## 🌟 Key Features

- **📊 Comprehensive Financial Dashboard**:
  - Live Net Balance computation.
  - Real-time Income vs. Expense breakdowns with high-contrast color indicators.
  - **Privacy Mode Shield (`👁 / 🔒`)**: Tap once to mask financial figures (`••••••`) in public spaces.

- **⏱️ Monthly Budget Pacing Widget**:
  - Automatically calculates your daily spending allowance (`₹/day left`) based on remaining budget and days left in the current month.
  - Category-wise spending visual progress bars with dynamic status indicators:
    - 🟢 Normal (< 75%)
    - 🟡 Attention (75% - 99%)
    - 🔴 Exceeded (≥ 100%)

- **⚡ Frictionless Quick-Log Bottom Sheet**:
  - Fast transaction logging in seconds.
  - Category selector chips with custom emoji glyphs (`🍔 Food`, `🚗 Transit`, `💡 Bills`, `🛒 Groceries`, `💼 Salary`, `💻 Freelance`, etc.).
  - **Built-in Numpad Calculator**: Perform on-the-fly arithmetic (`+`) right inside the numpad without leaving the screen.

- **💾 Local Storage & Management**:
  - Fast, offline-first local persistence powered by `SharedPreferences` and JSON serialization.
  - Long-press any transaction to delete with confirmation.

---

## 🛠️ Tech Stack & Architecture

- **Language**: Kotlin 2.2
- **UI Framework**: Modern Android Views, Material Design 3 (M3 Dark Theme), ViewBinding
- **Persistence**: SharedPreferences + Google Gson
- **Target SDK**: Android 35 (Vanilla Ice Cream) / Min SDK: Android 26
- **Build System**: Gradle 8.14.3 with Kotlin DSL (`build.gradle.kts`)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Koala / Meerkat (or JDK 17 / 21)
- Android SDK 35 installed

### Clone & Build
```bash
git clone https://github.com/krishvora445/ExpenseFlow.git
cd ExpenseFlow
./gradlew assembleDebug
```

The APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Install directly via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
