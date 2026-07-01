# World Countries App

An Android application that displays information about countries around the world, built as a two-part technical challenge.

## Screenshots

| Home Screen | Search | Detail Screen |
|---|---|---|
| ![Home](screenshots/home.png) | ![Search](screenshots/search.png) | ![Detail](screenshots/detail.png) |

## Tests

![Tests](screenshots/tests.png)

---

## Part 1 — Foundation

The first iteration establishes the core functionality:

- Display a list of countries with flag, name, region and population
- Search countries by name (local filtering)
- Country detail screen with capital, languages, currencies and timezones
- Loading, success and error states
- Dark mode support

### Architecture (Part 1)

Single-module project following **Clean Architecture + MVVM** with three layers:

```
app/
└── src/main/java/com/example/atomchallenge/
    ├── data/          # DTOs, Retrofit, Repository implementation
    ├── domain/        # Business models, Repository interface, Use Cases
    ├── presentation/  # ViewModels, Compose screens, UiState
    └── di/            # Hilt dependency injection modules
```

---

## Part 2 — Modularization, Caching and Offline Support

The second iteration evolves the architecture to support scalability, local caching and offline access.

### What changed and why

#### Feature-based multi-module architecture

The project was split into 7 independent Gradle modules:

```
app/                                    # APK entry point, DI assembly, Navigation
├── core/
│   ├── network/                        # OkHttp + Retrofit (shared across features)
│   ├── database/                       # Room dependency source of truth
│   └── ui/                             # Theme, Color, Typography (shared across features)
└── feature/
    └── countries/
        ├── domain/                     # Pure Kotlin — Country model, Repository interface, Use Cases
        ├── data/                       # DTOs, Entities, Mappers, Repository implementation
        └── presentation/              # Compose screens, ViewModels, UiState
```

**Why modularize?**

```
1. Compilation speed — Gradle only recompiles modules that actually changed
2. Enforced boundaries — the compiler prevents domain from importing data or presentation
   (not just a convention — physically impossible without adding the dependency)
3. Scalability — a new feature (e.g. favorites) adds its own domain/data/presentation
   modules without touching the existing ones
```

**Module dependency direction (never broken):**

```
app → feature:countries:presentation → feature:countries:domain ← feature:countries:data
app → core:network
app → core:database → (used by feature:countries:data)
app → core:ui       → (used by feature:countries:presentation)
```

`domain` depends on nobody. Everything else depends on `domain`. This is Clean Architecture enforced by the compiler.

**Why `core:database` uses `api` instead of `implementation` for Room:**

```kotlin
// core/database/build.gradle.kts
api(libs.room.runtime)   // exposes Room annotations (@Entity, @Dao, @Database)
api(libs.room.ktx)       // to feature:countries:data so it can use them directly
```

`implementation` keeps dependencies private. `api` exposes them to dependents. Since `feature:countries:data` needs to write `@Entity`, `@Dao`, `@Database` annotations directly, `core:database` must use `api` — otherwise the compiler can't resolve those types.

---

#### Local caching with Room

Three new components in `feature:countries:data/local/`:

| File | Responsibility |
|---|---|
| `CountryEntity.kt` | Maps to a SQLite row. `List<String>` fields stored as comma-separated strings (Room doesn't support List natively) |
| `CountryDao.kt` | `getAllCountries(): Flow<List<CountryEntity>>` for reactive reading. `insertAll()` with `REPLACE` strategy for refresh |
| `CountryDatabase.kt` | Room database scoped to the countries feature |

Two mappers replace the single mapper from Part 1:

```
CountryDto   →  toEntity()   →  CountryEntity   (API response → SQLite row)
CountryEntity →  toDomain()  →  Country          (SQLite row → clean domain model)
```

---

#### Offline-first strategy (Single Source of Truth)

The UI **never reads from the network directly**. It always reads from Room. The network only writes to Room.

```
getCountries() — always reads from Room via Flow
                 Room notifies the UI automatically when data changes
                 never fails due to network issues

refreshCountries() — calls the API, writes results to Room
                     if it fails (no internet), Room still has previous data
                     the UI never crashes — it just stops updating
```

This is implemented in `CountryRepositoryImpl`:

```kotlin
// Reading — always Room, never network
override fun getCountries(): Flow<List<Country>> {
    return countryDao.getAllCountries().map { it.map { entity -> entity.toDomain() } }
}

// Writing — only function that touches the network
override suspend fun refreshCountries() {
    val remote = apiService.getCountries()
    countryDao.insertAll(remote.map { it.toEntity() })
}
```

---

#### Manual refresh (pull-to-refresh)

Users can force a data refresh by pulling down on the country list. Implemented with `PullToRefreshBox` from Material3, connected to `RefreshCountriesUseCase`.

---

### Tech Stack

| Category | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | Clean Architecture + MVVM + Multi-module |
| Dependency Injection | Hilt |
| Networking | Retrofit + OkHttp |
| Local persistence | Room |
| Reactive streams | Kotlin Flow |
| Image Loading | Coil |
| Testing | JUnit4 + MockK + Turbine |

---

### Testing

**17 unit tests across 3 modules** (Part 1 — single module):

| Module | Test class | Coverage |
|---|---|---|
| `app` | `MapperTest` | DTO → Domain transformation |
| `app` | `GetCountriesUseCaseTest` | Business logic and sorting |
| `app` | `HomeViewModelTest` | UI state management and search |

**Additional tests for Part 2 multi-module architecture:**

| Module | Test class | Coverage |
|---|---|---|
| `feature:countries:domain` | `GetCountriesUseCaseTest` | Flow emissions + sorting |
| `feature:countries:data` | `DtoToEntityMapperTest` | DTO → Entity (List → comma-separated String) |
| `feature:countries:data` | `EntityToDomainMapperTest` | Entity → Domain (String → List) |
| `feature:countries:data` | `CountryRepositoryImplTest` | Offline-first: Room reads, network writes, fallback |
| `feature:countries:presentation` | `HomeViewModelTest` | StateFlow + combine + pull-to-refresh |

Run all tests:

```bash
./gradlew test
```

---

### API

This app uses the [REST Countries API](https://restcountries.com/).

---

### Requirements

- Android Studio Hedgehog or later
- Android 7.0 (API 24) or higher

### How to Run

1. Clone the repository

```bash
git clone https://github.com/yourusername/AtomChallenge.git
```

2. Open the project in Android Studio

3. Run the app on an emulator or physical device
