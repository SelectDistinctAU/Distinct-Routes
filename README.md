---
title: "Distinct Routes — Open Source Safety Cycling Navigator"
date: 2026-10-11
description: "Safety-aware route planning for cyclists, cargo bikes, wheelchairs, and mobility device riders."
---

# DISTINCT ROUTES — Safety Cycling & Mobility Navigator

**Safety-aware route planning for cyclists, cargo bikes, wheelchairs, and mobility device operators powered by BBBike and OpenStreetMap.**

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Platform: Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![Target SDK: 36](https://img.shields.io/badge/TargetSDK-36-orange.svg)](https://developer.android.com/)

---

## 📌 Documentation Hub (5W Framework)

We maintain clean, Hugo and GitHub Pages-ready documentation structured for four primary audiences:

| Target Audience | Document Link | Core Content & 5W Focus |
| :--- | :--- | :--- |
| **New Users** | [Quick Start Guide](07_Docs_Public/QUICK_START.md) | **3-Step Setup:** Vehicle, Departure, Destination. Learning curve progression. |
| **Riders & Users** | [User Manual & How-To's](07_Docs_Public/USER_MANUAL.md) | **Complete Manual:** Map Long-Press, Detours, Favourites, Navigation Handoff, FAQ. |
| **Beta Testers** | [Beta Testing Guide](BETA_TESTING_GUIDE.md) | **Testing Scope:** Boundaries (feature freeze), Bug Report Protocol, Steering Panel. |
| **Content Creators** | [Press & Media Kit](PRESS_KIT.md) | **Media Kit:** Fast facts, core quotes, key differentiators, high-res assets. |

---

## ABOUT

Distinct Routes is a free, open-source Android application that plans safety-focused routes prioritized for **calm backstreets, quiet cycleways, flat terrain, and greenways** rather than high-speed motorways.

**Key Features:**
- **Native BBBike API v0.2 Protocol Engine:** Direct integration with `https://api.bbbike.org/api/0.2/{city}/` (`pref_cat=N1`).
- **100% Full-Screen Floating Canvas UI:** Interactive map canvas with 1-tap overlay collapse (`▲`) and 90%+ clear map view.
- **Visual Map Long-Press Navigation:** 1-tap menu (`▶ Departure`, `● Detour (n)`, `■ Destination`).
- **Specialized Vehicle Profiles:** Standard Bicycles, E-Bikes, Cargo Bikes, Tandems, **Wheelchairs / Handbikes**, and **Mobility Scooters**.
- **Jetpack Room ORM SQLite Storage:** On-device persistent storage for Favourites and Ride Journal (zero cloud tracking).
- **City-Scoped Address Search:** Nominatim searches bounded to active city (`viewbox` + `bounded=1`) with 100% house number preservation.

---

## DISCLAIMER

**This app does NOT guarantee route safety.** We provide routing data, not safety guarantees. Cyclists and mobility device operators remain responsible for assessing local conditions before riding.

---

## LICENSE

Licensed under the GNU General Public License v3.0 (GPLv3). See [LICENSE](LICENSE) for full text.

**Contact:** `distinctroutes@pm.me` | **GitHub:** [https://github.com/SelectDistinctAU/Distinct_Routes](https://github.com/SelectDistinctAU/Distinct_Routes)