# NatCalc — Setup & Run Guide

## Prerequisites

| Tool | Minimum Version |
|------|----------------|
| Android Studio | Hedgehog 2023.1.1+ |
| JDK | 17 |
| Android SDK | API 26 (min) / API 34 (target) |
| Kotlin | 1.9.22 (handled by Gradle) |

---

## 1. Open the Project

```
File → Open → select E:\Projects\Numi\NatCalc
```

Android Studio will sync Gradle automatically. First sync downloads ~200 MB of dependencies.

---

## 2. Create Launcher Icons (one-time)

The project references `@mipmap/ic_launcher` which Android Studio generates automatically when you create a project. To add it:

```
Right-click res/ → New → Image Asset → choose any icon → Finish
```

Or copy any existing `mipmap-*` folders from a new blank project into `app/src/main/res/`.

---

## 3. Currency API (Optional)

The app uses [open.er-api.com](https://open.er-api.com) free tier — **no API key required**. It falls back to built-in offline rates automatically when offline.

If you want real-time rates in production, sign up at [exchangerate-api.com](https://www.exchangerate-api.com) and update `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "CURRENCY_API_BASE_URL", "\"https://v6.exchangerate-api.com/v6/YOUR_KEY/\"")
```

---

## 4. Run on Emulator

1. **AVD Manager** → Create Virtual Device → Pixel 6 → API 34
2. Press **Run ▶** (or `Shift+F10`)

---

## 5. Run on Physical Device

1. Enable **Developer Options** on device → turn on **USB Debugging**
2. Connect via USB
3. Select device in Android Studio → **Run ▶**

---

## 6. Build Release APK

```
Build → Generate Signed Bundle/APK → APK → create/use keystore → Release
```

Output: `app/release/app-release.apk`

---

## Example Test Inputs

### English — Arithmetic
```
1 + 1
(5 + 3) * 4 / 2
2 ^ 10
sqrt(144)
1,000,000 * 365
```

### English — Percentages
```
What is 20% of 500?
50 percent of 1200
```

### English — Currency
```
Convert 50 USD to EUR
100 dollars in dinars
50 GBP to SAR
```

### English — Fractions
```
Half of 200
Quarter of 1000
A third of 99
Three-quarters of 400
```

### Arabic — All Categories
```
١ + ١
احسب 20 بالمية من 5000
معي 100 دينار كم نصها؟
حول 50 دولار إلى يورو
نصف 300
ربع 1000
```

---

## Project Structure

```
NatCalc/
├── app/src/main/java/com/natcalc/
│   ├── parser/             ← NLP core (language-agnostic)
│   │   ├── NLPParser.kt        orchestrator
│   │   ├── ArithmeticParser.kt  math expressions
│   │   ├── PercentageParser.kt  X% of Y
│   │   ├── CurrencyParser.kt    currency detection
│   │   ├── FractionParser.kt    half/quarter/third
│   │   ├── ExpressionEvaluator.kt  recursive descent math
│   │   ├── CurrencyMapper.kt    ISO code aliases + mock rates
│   │   ├── LanguageDetector.kt  Arabic vs English detection
│   │   ├── NumberExtractor.kt   digit normalization
│   │   ├── ParseResult.kt       sealed result types
│   │   └── ResponseFormatter.kt human-readable output
│   ├── domain/             ← Business logic, no Android deps
│   │   ├── model/              Message, MessageType
│   │   ├── repository/         Repository interfaces
│   │   └── usecase/            ProcessInput, GetHistory, ClearHistory
│   ├── data/               ← Room + Retrofit implementations
│   │   ├── local/              AppDatabase, MessageDao, MessageEntity
│   │   ├── remote/             CurrencyApi, ExchangeRateResponse
│   │   └── repository/         Impl classes
│   ├── di/                 ← Hilt DI modules
│   └── presentation/       ← MVVM UI
│       ├── chat/               ChatFragment + ChatViewModel + MessageAdapter
│       └── history/            HistoryFragment + HistoryViewModel
└── res/
    ├── layout/             ← Chat bubbles, history list, fragments
    ├── navigation/         ← NavGraph (chat ↔ history)
    ├── values/             ← colors, strings, themes
    └── values-ar/          ← Arabic locale strings
```

---

## Architecture Decisions

| Decision | Reason |
|----------|--------|
| Rule-based NLP (no ML) | Zero latency, offline, predictable, extensible |
| Clean Architecture | Parser/domain/data/UI are fully decoupled; easy to swap NLP engine |
| Hilt DI | Standard Android DI; enables testability |
| Room | Typed SQLite; offline history; reactive with Flow |
| Retrofit + offline fallback | Live rates when online; mock rates when offline |
| ListAdapter + ChatRow sealed class | Efficient diffing; user/bot bubbles as separate RecyclerView types |
| Recursive descent evaluator | Proper BODMAS precedence, no eval(), supports sqrt/^ |

---

## Future Improvements

- [ ] **Unit conversions** — km↔mi, kg↔lb, °C↔°F
- [ ] **Word numbers** — "one hundred plus fifty", "مئة وخمسين"
- [ ] **Multi-step context** — "what was that times 2?"
- [ ] **LLM fallback** — call Claude/GPT for inputs the rule engine can't parse
- [ ] **Voice language toggle** — Arabic speech recognition (ar-SA)
- [ ] **Dark mode** — Night theme (colors.xml night variant)
- [ ] **Copy result** — long-press bot bubble to copy
- [ ] **Share calculation** — share as image/text
- [ ] **Widget** — home screen quick-calc widget
- [ ] **Notifications** — scheduled reminders (e.g., currency alerts)
- [ ] **More currencies** — crypto (BTC, ETH) via separate API
