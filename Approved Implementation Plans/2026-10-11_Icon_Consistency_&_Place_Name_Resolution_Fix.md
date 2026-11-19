# Approved Implementation Plan — Icon Consistency, Custom Drawables & Place Name Resolution Fix

**Approval Status:** Approved by Project Owner  
**Approval Timestamp:** 2026-10-11  

---

Wire custom vector drawables (`bookmark_heart` and `add_location`), ensure 100% app-wide icon consistency between search cards and the Favourites/Places tab, fix reverse geocoding place name resolution across waypoints and Ride Journal history, and remove the Bike Shop default place option.

## User Review Required

> [!IMPORTANT]
> **Custom Vector Drawables & Icon Consistency:** Wires `R.drawable.bookmark_heart` for bookmarking places across Route Planner and Places tab, and `R.drawable.add_location` for adding detour stops.

> [!NOTE]
> **Complete Place Name Resolution:** Reverse geocoding now updates Departure, Detours, Destination, map long-press popups, and Ride Journal entries with clean street and suburb names (e.g. *"Hill View Terrace, St James"*).

## Proposed Changes

### Documentation & Plan Archiving

#### [NEW] [Approved Implementation Plans/](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/Approved%20Implementation%20Plans)
- Archive a copy of this approved plan as `Approved Implementation Plans/2026-10-11_Icon_Consistency_&_Place_Name_Resolution_Fix.md` upon approval.

---

### UI & View Layer

#### [MODIFY] [RoutePlannerScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/app/src/main/java/com/distinctroutes/app/ui/route_planner/RoutePlannerScreen.kt)
- Use `painterResource(R.drawable.bookmark_heart)` for bookmark buttons on Departure & Destination rows.
- Use `painterResource(R.drawable.add_location)` for the Add Detour Stop button.
- Remove `BIKE_SHOP` option from `SavePlaceDialog`.

#### [MODIFY] [FavoritesScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/app/src/main/java/com/distinctroutes/app/ui/favorites/FavoritesScreen.kt)
- Use `painterResource(R.drawable.bookmark_heart)` for the Places tab filter chip icon.

---

### ViewModel & Geocoding Layer

#### [MODIFY] [RoutePlannerViewModel.kt](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/app/src/main/java/com/distinctroutes/app/ui/route_planner/RoutePlannerViewModel.kt)
- Update reverse geocoding callbacks to ensure street/suburb names are populated in input fields, waypoints, map long-press popups, and Ride Journal entries.

---

## Verification Plan

### Automated Tests
- Run `./gradlew testDebugUnitTest` to verify all unit tests pass.

### Manual Verification
- Test long-pressing on the map on the emulator and verify that the street name is resolved in the popup menu and populated in the input field.
- Verify `bookmark_heart` and `add_location` icons render on device.
