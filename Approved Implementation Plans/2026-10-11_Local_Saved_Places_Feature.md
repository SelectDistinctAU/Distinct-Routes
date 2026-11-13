# Approved Implementation Plan — Local On-Device Saved Places Feature (Home, Work, Places)

**Approval Status:** Approved by Project Owner  
**Approval Timestamp:** 2026-10-11  

---

Implement 100% private, on-device **Local Saved Places (`SavedPlaceEntity` & Quick-Access Chips)** in Jetpack Room ORM SQLite database, allowing riders to populate Departure, Detour, or Destination points in 1 tap (`[ 🏠 Home ]`, `[ 💼 Work ]`, `[ ➕ Add Place ]`) with zero cloud accounts or tracking.

## User Review Required

> [!IMPORTANT]
> **100% On-Device Privacy Guaranteed:** Saved Places are stored exclusively in the local Jetpack Room ORM SQLite database. No Google Sign-In, no cloud accounts, and zero location telemetry tracking.

## Proposed Changes

### Documentation & Plan Archiving

#### [NEW] [Approved Implementation Plans/](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/Approved%20Implementation%20Plans)
- Archive a copy of this approved plan as `Approved Implementation Plans/2026-10-11_Local_Saved_Places_Feature.md` upon approval.

---

### Local Persistence Layer (Room ORM)

#### [NEW] [SavedPlaceEntity.kt](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/app/src/main/java/com/distinctroutes/app/data/local/db/SavedPlaceEntity.kt)
- Room ORM entity for saved places (`id`, `label`, `iconType`, `addressText`, `lat`, `lon`, `timestamp`).

#### [NEW] [SavedPlaceDao.kt](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/app/src/main/java/com/distinctroutes/app/data/local/db/SavedPlaceDao.kt)
- Room DAO for inserting, querying, updating, and deleting saved places.

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/app/src/main/java/com/distinctroutes/app/data/local/db/AppDatabase.kt)
- Register `SavedPlaceDao` and bump DB version to `2`.

---

### UI & Route Planner Layer

#### [MODIFY] [RoutePlannerScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/app/src/main/java/com/distinctroutes/app/ui/route_planner/RoutePlannerScreen.kt)
- Render quick-access chips (`[ 🏠 Home ]`, `[ 💼 Work ]`, `[ ➕ Add Place ]`) under address input rows for 1-tap location setting.

#### [MODIFY] [FavoritesScreen.kt](file:///C:/Users/geewy/AndroidStudioProjects/DistinctRoutes/app/src/main/java/com/distinctroutes/app/ui/favorites/FavoritesScreen.kt)
- Add "Saved Places" tab for managing, editing, and deleting saved places.

---

## Verification Plan

### Automated Tests
- Create `SavedPlaceDaoTest.kt` to verify Room ORM database CRUD operations.
- Run `./gradlew testDebugUnitTest` to verify all tests pass.

### Manual Verification
- Save "Home" and "Work" places on the emulator.
- Tap `[ 🏠 Home ]` chip on Departure field and verify 1-tap location setting and pin placement.
