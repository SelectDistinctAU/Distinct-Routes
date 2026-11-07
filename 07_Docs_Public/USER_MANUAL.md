---
title: "User Manual & Complete How-To Guide"
date: 2026-10-11
description: "Comprehensive guide to all features, safety toggles, favourites, and navigation handoffs."
lead: "Master every feature of Distinct Routes."
weight: 20
---

# User Manual — Distinct Routes

This manual provides complete documentation for all features, safety preferences, favourites, ride history, and navigation handoffs in **Distinct Routes**.

---

## 🗺️ 1. Full-Screen Map & Canvas Controls

The OpenStreetMap canvas fills the full screen background:
- **Pan & Zoom:** Single-finger drag to pan; pinch to zoom.
- **Collapse / Expand Overlay Card (`▲` / `✎`):** Tap the **Chevron (`▲`)** at top right to collapse search inputs into a 1-line top banner (`"Departure → Destination"`), clearing **over 90% of the screen** for the map.
- **Clear All (`🗑`):** Tap the **Red Trash Icon** to reset all pins, addresses, detours, and route lines with 1 tap.

---

## 📍 2. Map Long-Press Contextual Menu

Long-pressing any location on the map canvas opens a 1-tap menu:
* **`▶ Set Departure`:** Sets the starting point and green flag pin.
* **`● Add Detour (n)`:** Adds a mid-point detour stop (Detour 1, Detour 2...).
* **`■ Set Destination`:** Sets the destination point and red marker pin.

---

## ⚙️ 3. Safety & Riding Preferences

Tap **Safety & Riding Preferences** to customize pathfinding:

| Setting | Purpose |
| :--- | :--- |
| **Prefer Residential Roads** | Routes through quiet backstreets (`pref_cat=N1`) avoiding multi-lane traffic arterials. |
| **Prefer Green & Scenic Routes** | Prioritizes parks, river foreshores, and greenways. |
| **Avoid Unlit Streets** | Avoids unlit roads for night riding safety. |
| **Avoid Cobblestones / Unpaved** | Forces smooth paved surfaces for road bikes, wheelchairs, and scooters. |
| **Avoid Traffic Lights** | Bypasses signalized intersections. |
| **Avoid Highways** | Mandatory avoidance of dual-carriageway motorways. |
| **Avoid Steep Hills** | Avoids steep climbs (mandatory for wheelchairs & scooters). |
| **Dog Trailer Mode** | Applies a climbing weight penalty for heavy cargo/trailers. |

---

## ⭐ 4. Favourites & Ride Journal History

- **Save Favourite:** Tap **`[♥ Save Favourite]`** on the route result card to store frequent routes on-device in Jetpack Room ORM SQLite storage.
- **Ride Journal:** Every calculated route is automatically logged into **Ride Journal History** on-device with date, distance, duration, and profile.
- **1-Tap Re-Route:** Tap **`[Load into Route Planner]`** on any favourite or journal entry to populate inputs and calculate the route trail.

---

## 📤 5. GPX Export & Navigation Handoff

Tap **`[Share GPX / Route]`** on the route result card:
- Generates a standard GPX 1.1 XML track (`distinct-routes-{timestamp}.gpx`).
- Shares the route track with OsmAnd, Organic Maps, Garmin Connect, Email, or Drive.

---

## ❓ 6. Frequently Asked Questions & Troubleshooting

**Q: Why does the route line start slightly away from my exact driveway pin?**  
*A:* BBBike's routing engine snaps your coordinates to the nearest safe street node. Look for the *"📍 Snapped to nearest safe route node"* disclosure badge on the result card.

**Q: Does Distinct Routes track my location data?**  
*A:* No. Distinct Routes is 100% open-source (GPLv3), ad-free, with zero user accounts and zero telemetry. All saved routes remain strictly on your device.
